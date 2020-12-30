package com.csimcik.gardeningBuddy.models.plantDetail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Images(
    val flower : List<Image?>,
    val leaf : List<Image?>,
    val habit : List<Image?>,
    val fruit : List<Image?>,
    val bark : List<Image?>,
    val other : List<Image?>
) : Parcelable