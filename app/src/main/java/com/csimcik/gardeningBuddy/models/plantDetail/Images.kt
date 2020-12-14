package com.csimcik.gardeningBuddy.models.plantDetail

data class Images(
    val flower : List<Image?>,
    val leaf : List<Image?>,
    val habit : List<Image?>,
    val fruit : List<Image?>,
    val bark : List<Image?>,
    val other : List<Image?>
)