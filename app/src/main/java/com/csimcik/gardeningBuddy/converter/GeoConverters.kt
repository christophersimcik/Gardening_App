package com.csimcik.gardeningBuddy.converter
import android.util.Log
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.lang.StringBuilder

class GeoConverters {

    @TypeConverter
    fun holesToString(list: List<List<LatLng?>?>?): String?{
        val stringBuilder = StringBuilder()
        var cnt = 0
        list?.forEach { listOfLatLngs->
            listOfLatLngs?.forEach {
                stringBuilder
                    .append(it?.latitude.toString())
                    .append(":")
                    .append(it?.longitude.toString())
                    .append(",")
            }
            stringBuilder.append("/")
            cnt ++
        }
        Log.d("NUM ","cnt = $cnt")
        return stringBuilder.toString()
    }

  /*  @TypeConverter
    fun holesToString(list: List<List<LatLng?>?>?): String?{
        val stringBuilder = StringBuilder()
        list?.forEach { inner->
            inner?.forEach {
                stringBuilder
                    .append(it?.latitude.toString())
                    .append(":")
                    .append(it?.longitude.toString())
                    .append(",")
            }
            stringBuilder.append("/")
        }
        return stringBuilder.toString()
    }
*/


    @TypeConverter
    fun toListOfHoles(string: String?): List<List<LatLng>>? {
        val list = ArrayList<List<LatLng>>()
        val polygons = string?.split("/")
        Log.d("*GEO*", "sz = ${polygons?.size}")
        polygons?.forEach { polys ->
            val tmpList = ArrayList<LatLng>()
            polys.split(",").let { latlngs ->
                latlngs.forEach { latlng ->
                    if (latlng.isNotEmpty()) {
                        latlng.split(":").let {
                            tmpList.add(LatLng(it[0].toDouble(), it[1].toDouble()))
                        }
                    }
                }
            }
            list.add(tmpList)
        }
        Log.d("LT LNGS ", "${list.size}")
            return list
        }

}