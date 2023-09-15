package com.example.prm_projekt_2_s22599.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val path: String,
    val longitude: Double,
    val latitude: Double
)
