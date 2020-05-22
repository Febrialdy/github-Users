package com.adityafebrialdy.proyekakhir

import android.content.Intent
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityafebrialdy.proyekakhir.Adapter.UserAdapter
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.adityafebrialdy.proyekakhir.Helper.MappingHelper
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.adityafebrialdy.proyekakhir.Model.ProfileSQL
import com.adityafebrialdy.proyekakhir.Prefs.UserPreference
import com.adityafebrialdy.proyekakhir.Receiver.AlarmReceiver
import com.adityafebrialdy.proyekakhir.ViewModel.MainVM
import com.adityafebrialdy.proyekakhir.Widget.StackRemoteViewsFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {
  private lateinit var adapter: UserAdapter
  private lateinit var mainViewModel: MainVM
  private var listFavorite = ArrayList<ProfileSQL>()
  private var choiceProfileSQL: ProfileSQL? = null
  private var choiceReminder: Boolean = false
  private lateinit var mUserPreference: UserPreference
  private lateinit var alarmReceiver: AlarmReceiver
  private var listAvatar = ArrayList<Uri>()

  companion object{
    const val EXTRA_STATE = "EXTRA_STATE"
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    fab_btn_favorite.setOnClickListener(this)
    mUserPreference = UserPreference(this)
    choiceReminder = mUserPreference.getUser()
    alarmReceiver = AlarmReceiver()
    showLoading(true)


    val handlerThread = HandlerThread("DataObserver")
    handlerThread.start()
    val handler = Handler(handlerThread.looper)

    val myObserver = object: ContentObserver(handler){
      override fun onChange(selfChange: Boolean) {
        loadNotesAsync()
      }
    }
    contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

    adapter = UserAdapter()
    adapter.notifyDataSetChanged()
    rv_User.layoutManager = LinearLayoutManager(this)
    rv_User.adapter = adapter
    adapter.setOnItemClickCallBack(object: UserAdapter.OnItemClickCallback{
      override fun onItemClicked(data: Profile) {
        listFavorite.clear()
        loadNotesAsync()
        for(i in listFavorite.indices){
          if(listFavorite[i].username == data.username){
            choiceProfileSQL = listFavorite[i]
            break
          }
          else{
            choiceProfileSQL = null
          }
        }
        val intent = Intent(applicationContext, ActivityDetail::class.java)
        intent.putExtra(ActivityDetail.EXTRA_DATA, data)
        intent.putExtra(ActivityDetail.EXTRA_DATA_FAVORITE, choiceProfileSQL)
        startActivity(intent)
      }
    })

    if (savedInstanceState == null) {
      // proses ambil data
      loadNotesAsync()
    }

    mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainVM::class.java)
    val cek = mainViewModel.setUsers("adit", applicationContext)
    if(!cek){
      Toast.makeText(this, "faild to load data : Check your internet connection!\n Application will close automatically", Toast.LENGTH_LONG).show()
      Handler().postDelayed({finishAffinity()}, 5000L)
    }

    search_input.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
      @RequiresApi(Build.VERSION_CODES.M)
      override fun onQueryTextSubmit(query: String?): Boolean {
        if(query == null) return false
        showLoading(true)
        mainViewModel.setUsers(query, applicationContext)
        return true
      }
      override fun onQueryTextChange(newText: String?): Boolean {
        return false
      }
    })

    mainViewModel.getProfiles().observe(this, Observer { user ->
      if(user != null){
        adapter.setData(user)
        showLoading(false)
        Log.d("MyMainActivity", user.toString())
      }
      else{
        Toast.makeText(applicationContext, "failed to load data", Toast.LENGTH_LONG).show()
      }
    })
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelableArrayList(EXTRA_STATE, listFavorite)
  }

  override fun onClick(v: View?) {
    when(v?.id){
      R.id.fab_btn_favorite ->{
        val intent = Intent(this, Activity_favorite::class.java)
        startActivity(intent)
      }
    }
  }

  private fun showLoading(state: Boolean){
    if(state){
      progressBar.visibility = View.VISIBLE
    }
    else{
      progressBar.visibility = View.INVISIBLE
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when(item.itemId){
      R.id.settings -> {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Reminder")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_custom, null)
        val mySwitch  = dialogLayout.findViewById<SwitchMaterial>(R.id.reminder_switch)
        mySwitch.isChecked = choiceReminder
        builder.setView(dialogLayout)
        builder.setCancelable(true)
        builder.setPositiveButton("OK") { dialogInterface, i ->
          if(mySwitch.isChecked) mUserPreference.setUser(true)
          else mUserPreference.setUser(false)
        choiceReminder = mUserPreference.getUser()
        if(choiceReminder){
          alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING,
            "Reminder active")
        }
        else{
          alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
        }}
        builder.setNegativeButton("No") {dialog, which -> dialog.cancel() }
        builder.show()
        return true
      }
      else -> return true
    }
  }

  private fun loadNotesAsync() {
    GlobalScope.launch(Dispatchers.Main) {
      showLoading(true)
      val deferredNotes = async(Dispatchers.IO) {
        val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
        MappingHelper.mapCursorToArrayList(cursor)
      }
      showLoading(false)

      val notes = deferredNotes.await()
      if (notes.size > 0) {
        listFavorite.addAll(notes)
        return@launch
      } else {
        return@launch
      }
    }
  }

}
