package com.adityafebrialdy.proyekakhir.Db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.adityafebrialdy.proyekakhir.Db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import java.sql.SQLException

class UserHelper(context: Context) {
  private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
  private lateinit var database: SQLiteDatabase

  companion object {
    private const val DATABASE_TABLE = TABLE_NAME
    private var INSTANCE: UserHelper? = null

    fun getInstance(context: Context): UserHelper =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: UserHelper(context)
      }
  }

  @Throws(SQLException::class)
  fun open() {
    database = dataBaseHelper.writableDatabase
  }

  fun close() {
    dataBaseHelper.close()
    if (database.isOpen)
      database.close()
  }

  fun queryAll(): Cursor {
    return database.query(
      DATABASE_TABLE,
      null,
      null,
      null,
      null,
      null,
      "${BaseColumns._ID} ASC",
      null)
  }

  fun queryById(id: String): Cursor {
    open()
    return database.query(DATABASE_TABLE, null, "${BaseColumns._ID} = ?", arrayOf(id), null, null, null, null)
  }

  fun insert(values: ContentValues?): Long {
    open()
    return database.insert(DATABASE_TABLE, null, values)
  }

  fun update(id: String, values: ContentValues?): Int {
    open()
    return database.update(DATABASE_TABLE, values, "${BaseColumns._ID} = ?", arrayOf(id))
  }

  fun deleteById(id: String): Int {
    open()
    return database.delete(DATABASE_TABLE, "${BaseColumns._ID} = '$id'", null)
  }
}