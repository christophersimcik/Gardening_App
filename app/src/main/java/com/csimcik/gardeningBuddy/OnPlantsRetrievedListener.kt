package com.csimcik.gardeningBuddy

import com.csimcik.gardeningBuddy.models.PlantStub

interface OnPlantsRetrievedListener {
    fun onPlantsRetrieved(plants: List<PlantStub>?)
}