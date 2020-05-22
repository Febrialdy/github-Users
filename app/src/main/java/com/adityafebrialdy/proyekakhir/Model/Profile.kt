package com.adityafebrialdy.proyekakhir.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Profile (val username: String,
               val avatar: String,
               val url: String): Parcelable