package com.adityafebrialdy.proyekakhir

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.adityafebrialdy.proyekakhir.Adapter.SectionPagerAdapter
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.adityafebrialdy.proyekakhir.Model.ProfileSQL
import com.adityafebrialdy.proyekakhir.ViewModel.ListReposVM
import com.adityafebrialdy.proyekakhir.ViewModel.ProfileVM
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity(), View.OnClickListener {

  private var profileSQL: ProfileSQL? = null
  private lateinit var data: Profile
  private var favorite = true
  private lateinit var uriWithId: Uri

  companion object{
    const val EXTRA_DATA = "EXTRA_DATA"
    const val EXTRA_DATA_FAVORITE = "EXTRA_DATA_FAVORITE"
  }

  private lateinit var profileViewModel: ProfileVM
  private lateinit var reposViewModel: ListReposVM

  private fun myUrl(username: String, choice: Int): String{
    val url = when(choice){
      0 -> "https://api.github.com/users/${username}/followers"
      1 -> "https://api.github.com/users/${username}/following"
      else -> "https://api.github.com/users/${username}/repos"
    }
    return url
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    btn_favorite_detail.setOnClickListener(this)
    showLoading(true)

    profileSQL = intent.getParcelableExtra(EXTRA_DATA_FAVORITE)

    if(profileSQL != null){
      favorite = false
      uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + profileSQL?.id)
      btn_favorite_detail.setImageResource(R.drawable.ic_favorite_active)
    }

    data = intent.getParcelableExtra(EXTRA_DATA) as Profile
    Glide.with(this)
      .load(data.avatar)
      .into(user_avatar)
    choiceVM("Profile", data)
    choiceVM("Repos", data)
    showPager(myUrl(data.username, 0), myUrl(data.username, 1))
  }

  private fun showPager(follower_url: String, following_url: String){
    val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager)
    sectionPagerAdapter.getUrl(follower_url)
    sectionPagerAdapter.getListFollowing(following_url)
    view_pager.adapter = sectionPagerAdapter
    tabs.setupWithViewPager(view_pager)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun choiceVM(Choice: String, data: Profile){
    when(Choice){
      "Profile" ->{
        //GET DATA FROM API
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileVM::class.java)
        val cek = profileViewModel.setProfile(data.url, applicationContext)
        if(!cek){
          Toast.makeText(this, "faild to load data : Check your internet connection!\n Application will close automatically", Toast.LENGTH_LONG).show()
          Handler().postDelayed({finishAffinity()}, 5000L)
        }

        profileViewModel.getProfiles().observe(this, Observer { user ->
          if(user != null){
            supportActionBar?.setTitle(user.username)
            user_name.text = user.name
            user_followers.text = "${applicationContext.getString(R.string.follower)} : " + user.followers
            user_following.text = "${applicationContext.getString(R.string.following)} : " + user.following
            user_comp.text = if(user.company == "null") "Company" else user.company
            user_location.text = if(user.location == "null") "Location" else user.location
            Log.d("MyMainActivity", user.toString())
          }
        })
      }
      "Repos" ->{
        reposViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ListReposVM::class.java)
        val cek = reposViewModel.setRepos(myUrl(data.username, 2), applicationContext)
        if(!cek){
          Toast.makeText(this, "faild to load data : Check your internet connection!\n Application will close automatically", Toast.LENGTH_LONG).show()
          Handler().postDelayed({finishAffinity()}, 5000L)
        }
        reposViewModel.getRepos().observe(this, Observer { jumlahRepos ->
          if(jumlahRepos != null){
            user_repos.text = "$jumlahRepos ${applicationContext.getString(R.string.repository)}"
            showLoading(false)
          }
        })
      }
    }
  }

  private fun showLoading(state: Boolean){
    if(state){
      myprogressBar.visibility = View.VISIBLE
      relativeProfile.visibility = View.INVISIBLE
    }
    else{
      myprogressBar.visibility = View.INVISIBLE
      relativeProfile.visibility = View.VISIBLE
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onClick(v: View?) {
    when(v?.id){
      R.id.btn_favorite_detail -> {
        if(favorite){
          val dialogTitle = "Add"
          val dialogMessage = "are you sure, add ${data.username} to your favorite list?"

          val alertDialogBuilder = AlertDialog.Builder(this)

          alertDialogBuilder.setTitle(dialogTitle)
          alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
              val values = ContentValues()
              values.put(DatabaseContract.UserColumns.USERNAME, data.username)
              values.put(DatabaseContract.UserColumns.AVATAR, data.avatar)
              values.put(DatabaseContract.UserColumns.URL, data.url)
              contentResolver.insert(CONTENT_URI, values)
              btn_favorite_detail.setImageResource(R.drawable.ic_favorite_active)
              Toast.makeText(this, "add ${data.username} to Favorite", Toast.LENGTH_SHORT).show()
              finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
          val alertDialog = alertDialogBuilder.create()
          alertDialog.show()
          favorite = false
        }
        else{
          val dialogTitle = "Remove"
          val dialogMessage = "are you sure, remove ${data.username} from your favorite list?"
          val alertDialogBuilder = AlertDialog.Builder(this)

          alertDialogBuilder.setTitle(dialogTitle)
          alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
              contentResolver.delete(uriWithId, null, null)
              btn_favorite_detail.setImageResource(R.drawable.ic_favorite_inactive)
              Toast.makeText(this, "Remove ${uriWithId} from favorite", Toast.LENGTH_SHORT).show()
              favorite = true
              finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
          val alertDialog = alertDialogBuilder.create()
          alertDialog.show()

        }
      }
    }
  }
}
