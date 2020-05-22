package com.adityafebrialdy.consumerapp.Prefs

import android.content.Context
import androidx.core.content.edit

class UserPreference(context: Context) {
  companion object {
    private const val PREFS_NAME = "user_pref"
    private const val MYREMINDER = "myreminder"
  }

  private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun setUser(value: Boolean){
    preferences.edit {
      putBoolean(MYREMINDER, value)
    }
  }

  fun getUser(): Boolean{
    val myReminder = preferences.getBoolean(MYREMINDER, false)
    return myReminder
  }
}