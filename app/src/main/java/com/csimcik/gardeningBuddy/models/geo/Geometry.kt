package com.csimcik.gardeningBuddy.models.geo

import android.util.Log
import com.google.android.gms.maps.model.LatLng

class Geometry(
    val poly_coordinates: List<List<List<Double>>>?,
    val multi_coordinates: List<List<List<List<Double>>>>?
) {

    fun getPolys(): List<List<LatLng>> {
        val list = ArrayList<List<LatLng>>()
        poly_coordinates?.let {
            it.forEach { outer ->
                val tmp = ArrayList<LatLng>()
                outer.forEach { inner ->
                    tmp.add(LatLng(inner[1], inner[0]))
                }
                list.add(tmp)
            }
        }
        multi_coordinates?.let {
            it.forEach { outer ->
                val tmp = ArrayList<LatLng>()
                outer.forEach { inner ->
                    inner.forEach { core ->
                        tmp.add(LatLng(core[1], core[0]))
                    }
                    list.add(tmp)
                    Log.d("*GEO", "boogie $tmp")
                }
            }
        }
        return list
    }
}

