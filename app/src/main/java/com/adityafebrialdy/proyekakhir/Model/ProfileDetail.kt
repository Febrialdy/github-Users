package com.adityafebrialdy.proyekakhir.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProfileDetail(
    val username: String? = null,
    val name: String? = null,
    val following: String? = null,
    val followers: String? = null,
    val company: String? = null,
    val location: String? = null,
    val listFollowers: String? = null
): Parcelable