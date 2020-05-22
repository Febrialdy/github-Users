package com.adityafebrialdy.proyekakhir

import android.content.Intent
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adityafebrialdy.proyekakhir.Adapter.FavoriteAdapter
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.adityafebrialdy.proyekakhir.Helper.MappingHelper
import com.adityafebrialdy.proyekakhir.Model.Profile
import com.adityafebrialdy.proyekakhir.Model.ProfileSQL
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class Activity_favorite : AppCompatActivity() {

  companion object{
    private const val EXTRA_STATE = "EXTRA_STATE"
  }

  private lateinit var adapter: FavoriteAdapter
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_favorite)
    showLoading(true)

    supportActionBar?.title = "Favorite"
    rv_favorite.layoutManager = LinearLayoutManager(this)
    rv_favorite.setHasFixedSize(true)
    adapter =FavoriteAdapter(this)
    rv_favorite.adapter = adapter
    adapter.setOnItemClickCallBack(object: FavoriteAdapter.OnItemClickCallback{
      override fun onItemClicked(data: ProfileSQL) {
        val dataProfile = Profile(data.username, data.avatar, data.url)
        val intent = Intent(applicationContext, ActivityDetail::class.java)
        intent.putExtra(ActivityDetail.EXTRA_DATA, dataProfile)
        intent.putExtra(ActivityDetail.EXTRA_DATA_FAVORITE, data)
        startActivity(intent)
      }
    })


    val handlerThread = HandlerThread("DataObserver")
    handlerThread.start()
    val handler = Handler(handlerThread.looper)

    val myObserver = object: ContentObserver(handler){
      override fun onChange(selfChange: Boolean) {
        loadNotesAsync()
      }
    }

    contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

    if (savedInstanceState == null) {
      // proses ambil data
      loadNotesAsync()
    }
    else {
      val list = savedInstanceState.getParcelableArrayList<ProfileSQL>(EXTRA_STATE)
      if (list != null) {
        adapter.listFavorite = list
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavorite)
  }

  private fun showLoading(state: Boolean){
    if(state){
      progressBar_favorite.visibility = View.VISIBLE
    }
    else{
      progressBar_favorite.visibility = View.INVISIBLE
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
        adapter.listFavorite = notes
      } else {
        adapter.listFavorite = ArrayList()
        Toast.makeText(applicationContext, "Tidak ada data saat ini", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
