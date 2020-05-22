package com.adityafebrialdy.proyekakhir.Db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  companion object{
    private const val DATABASE_NAME = "dbusersgithub"
    private const val DATABASE_VERSION = 1

    private val SQL_CREATE_TABLE_NOTE = "CREATE TABLE ${DatabaseContract.UserColumns.TABLE_NAME}" +
      " (${DatabaseContract.UserColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
      " ${DatabaseContract.UserColumns.USERNAME} TEXT NOT NULL," +
      " ${DatabaseContract.UserColumns.AVATAR} TEXT NOT NULL," +
      " ${DatabaseContract.UserColumns.URL} TEXT NOT NULL)"
  }

  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(SQL_CREATE_TABLE_NOTE)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.UserColumns.TABLE_NAME}")
    onCreate(db)
  }
}