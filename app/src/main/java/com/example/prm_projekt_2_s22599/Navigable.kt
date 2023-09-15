package com.example.prm_projekt_2_s22599

interface Navigable {
    enum class Destination{
        List, Add, Edit, Draw
    }
    fun navigate(to: Destination, id: Long? = null)
}