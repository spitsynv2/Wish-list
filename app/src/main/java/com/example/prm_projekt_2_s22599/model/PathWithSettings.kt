package com.example.prm_projekt_2_s22599.model

import android.graphics.Color
import android.graphics.Path

data class PathWithSettings(
    val path: Path = Path(),
    val color: Int = Color.BLACK,
    val size: Float
)
