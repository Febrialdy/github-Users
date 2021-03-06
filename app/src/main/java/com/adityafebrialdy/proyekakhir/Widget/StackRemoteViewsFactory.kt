package com.adityafebrialdy.proyekakhir.Widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toIcon
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract
import com.adityafebrialdy.proyekakhir.Helper.MappingHelper
import com.adityafebrialdy.proyekakhir.R
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

internal class StackRemoteViewsFactory(private val mContext: Context): RemoteViewsService.RemoteViewsFactory {

  private var mWidgetItems = ArrayList<Uri>()

  private fun loadNotesAsync() {
    GlobalScope.launch(Dispatchers.Main) {
      val deferredNotes = async(Dispatchers.Main) {
        val cursor = mContext.contentResolver?.query(DatabaseContract.UserColumns.CONTENT_URI, null, null, null, null)
        MappingHelper.mapCursorToArrayList(cursor)
      }
      val notes = deferredNotes.await()
      if (notes.size > 0) {
        for (i in notes.indices){
          mWidgetItems.add(notes[i].avatar.toUri())
        }

      } else {
        return@launch
      }
    }
  }

  override fun onCreate() {

  }

  override fun getLoadingView(): RemoteViews? = null

  override fun getItemId(position: Int): Long = 0

  override fun onDataSetChanged() {
    loadNotesAsync()
  }

  override fun hasStableIds(): Boolean = false

  @RequiresApi(Build.VERSION_CODES.M)
  override fun getViewAt(position: Int): RemoteViews {
    val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
    rv.setImageViewIcon(R.id.imageView, mWidgetItems[position].toIcon())


    val extra = bundleOf(
      ImageBannerWidget.EXTRA_ITEM to position
    )
    val fillInIntent = Intent()
    fillInIntent.putExtras(extra)
    rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

    return rv
  }

  override fun getCount(): Int = mWidgetItems.size

  override fun getViewTypeCount(): Int = 1

  override fun onDestroy() {

  }
}