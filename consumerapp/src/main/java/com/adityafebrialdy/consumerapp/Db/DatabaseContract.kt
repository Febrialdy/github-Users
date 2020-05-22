package com.adityafebrialdy.consumerapp.Db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {
  const val AUTHORITY = "com.adityafebrialdy.proyekakhir"
  const val SCHEME = "content"

  internal class UserColumns: BaseColumns {
    companion object{
      const val TABLE_NAME = "users"
      const val _ID = "_id"
      const val USERNAME = "username"
      const val AVATAR = "avatar"
      const val URL = "url"

      // untuk membuat URI content://com.adityafebrialdy.consumerapp.users/user
      val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
        .authority(AUTHORITY)
        .appendPath(TABLE_NAME)
        .build()
    }
  }
}