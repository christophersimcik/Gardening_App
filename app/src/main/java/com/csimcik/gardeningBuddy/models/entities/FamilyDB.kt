package com.csimcik.gardeningBuddy.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class FamilyDB(
    @PrimaryKey
    val name: String,
    val common_name: String,
    val slug: String,
    val divisionOrder: String,
    val divisionClass: String,
    val division: String,
    val subkingdom: String,
    val kingdom: String,
    val color: Int
)