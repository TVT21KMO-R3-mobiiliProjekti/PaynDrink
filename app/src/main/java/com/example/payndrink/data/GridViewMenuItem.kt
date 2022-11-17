package com.example.payndrink.data

import android.graphics.Bitmap

data class GridViewMenuItem(
    val id: Int?,
    val itemName: String?,
    val pictureUrl: String?,
    val image: Bitmap?,
    val itemDescription: String?,
    val itemQuick: Int?,
    val itemPrice: Double?
)
