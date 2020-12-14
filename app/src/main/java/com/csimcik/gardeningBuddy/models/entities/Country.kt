package com.csimcik.gardeningBuddy.models.entities

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions

@Entity
class Country(
    @PrimaryKey
    val name: String,
    val code: String,
    val coordinates: List<List<LatLng>>,
) {
    @Ignore
    private val polygons: MutableList<Polygon> = ArrayList()

    fun getLatLngBounds(): LatLngBounds {
        val bounds = LatLngBounds.builder()
        coordinates.forEach {
            it.forEach { latLng ->
                bounds.include(latLng)
            }
        }
        return bounds.build()
    }

    fun getPolygons(): List<Polygon> {
        return polygons
    }

    fun addPolygon(polygon: Polygon?) {
        polygon?.let { polygons.add(polygon) }
    }

    fun getPolygonOptions(color: Int): List<PolygonOptions> {
        return ArrayList<PolygonOptions>().also { list ->
            coordinates.forEach { latLngs ->
                if (latLngs.isNotEmpty()) {
                    list.add(PolygonOptions().apply {
                        addAll(latLngs)
                        fillColor(color)
                        strokeColor(Color.WHITE)
                    })
                }
            }
        }
    }

}