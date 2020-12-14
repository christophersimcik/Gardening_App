package com.csimcik.gardeningBuddy.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var search = ""
}