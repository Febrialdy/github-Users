package com.adityafebrialdy.consumerapp.Helper

import android.database.Cursor
import com.adityafebrialdy.consumerapp.Db.DatabaseContract
import com.adityafebrialdy.consumerapp.Model.ProfileSQL

object MappingHelper {

  private lateinit var profileSQL: ProfileSQL

  fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<ProfileSQL> {
    val notesList = ArrayList<ProfileSQL>()
    notesCursor?.apply {
      while (moveToNext()) {
        val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
        val username = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
        val avatar = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
        val url = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.URL))
        notesList.add(ProfileSQL(id, username, avatar, url))
      }
    }
    return notesList
  }

  fun mapCursorToObject(notesCursor: Cursor?): ProfileSQL {
    notesCursor?.apply {
      moveToFirst()
      val id = getInt(getColumnIndexOrThrow(DatabaseContract.UserColumns._ID))
      val title = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.USERNAME))
      val description = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.AVATAR))
      val date = getString(getColumnIndexOrThrow(DatabaseContract.UserColumns.URL))
      profileSQL = ProfileSQL(id, title, description, date)
    }
    return profileSQL
  }
}