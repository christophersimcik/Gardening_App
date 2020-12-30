package com.csimcik.gardeningBuddy.models.plantDetail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    val image_url: String?,
    val copyright: String?
) : Parcelable