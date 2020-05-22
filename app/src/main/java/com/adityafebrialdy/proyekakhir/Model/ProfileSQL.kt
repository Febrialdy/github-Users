package com.adityafebrialdy.proyekakhir.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileSQL (val id: Int = 0,
                  val username: String,
                  val avatar: String,
                  val url: String): Parcelable