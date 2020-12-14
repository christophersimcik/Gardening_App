package com.csimcik.gardeningBuddy.models

import com.csimcik.Config
import com.csimcik.gardeningBuddy.models.plantDetail.Link

data class PlantStub(
    val id : Int?,
    val slug : String?,
    val common_name : String?,
    val scientific_name : String?,
    val family_common_name : String?,
    val image_url : String?,
    val synonyms : List<String>?,
    val genus : String?,
    val family : String?,
    val links : Link?
){
    fun listSynonyms(): String {
        val stringBuilder = StringBuilder()
        if (synonyms != null) {
            for (synonym in synonyms) {
                stringBuilder.append(synonym)
                if (synonym != synonyms.last()) {
                    stringBuilder.append(",\n\n")
                }
            }
        }
        return if (stringBuilder.isNotEmpty()) stringBuilder.toString() else Config.EMPTY
    }
}