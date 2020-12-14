package com.csimcik.gardeningBuddy.custom

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions


object PermissionHelper {

    fun checkPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        } else {
            Log.e("DB", "PERMISSION GRANTED")
        }
    }
}