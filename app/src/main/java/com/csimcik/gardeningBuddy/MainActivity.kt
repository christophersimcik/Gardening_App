package com.csimcik.gardeningBuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.csimcik.gardeningBuddy.database.CountriesDatabase
import com.csimcik.gardeningBuddy.models.entities.Country
import com.csimcik.gardeningBuddy.models.geo.Geography
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.HashMap

const val ITEMS_PER_PAGE = 20
const val SHARED_PREFERENCES = "SharedPreferences"


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "On Create")
        if (!checkForCountriesDatabase()) createCountriesDatabase()
    }

    private fun getJson(): String {
        val inputStream = this.resources?.openRawResource(R.raw.low_poly_data)
        inputStream?.let {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)
            return String(bytes)
        }
        return ""
    }

    private fun checkForCountriesDatabase(): Boolean {
        return !baseContext.getDatabasePath(CountriesDatabase.DATABASE_NAME).exists()
    }

    private fun createCountriesDatabase() {
        populateDatabase(parseJsonIntoListOfCountries(getJson()))
    }

    private fun populateDatabase(countries: List<Country>) {
        val db = CountriesDatabase.getInstance(this)
        val dao = db?.countriesDao()
        CoroutineScope(Dispatchers.Main).launch {
            dao?.insertAll(countries)
        }
    }

    private fun parseJsonIntoListOfCountries(j: String): List<Country> {
        val geography = Gson().fromJson(j, Geography::class.java)
        val mapOfCountriesToCode = getCodes().also{
            it.forEach { s, s2 -> Log.d("TAG", "key = $s and val = $s2")  }
        }
        return ArrayList<Country>().also { countries ->
            geography.features?.forEach { feature ->
                countries.add(
                    Country(
                        feature.properties.name,
                        mapOfCountriesToCode[feature.properties.iso]?:"",
                        feature.geometry.getPolys()
                    )
                )
            }
        }
    }

    private fun getCodeAsJson(): String {
        val inputStream = this.resources?.openRawResource(R.raw.geojson_level_4)
        inputStream?.let {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)
            return String(bytes)
        }
        return ""
    }

    private fun getCodes(): HashMap<String,String>{
        val data = Gson().fromJson(getCodeAsJson(),com.csimcik.gardeningBuddy.models.tdwg.Geography::class.java)
        val map = HashMap<String,String>()
        data.features?.let{list->
            list.forEach { feature->
                map[feature.properties.iso] = feature.properties.code
            }
        }
        return map
    }

}