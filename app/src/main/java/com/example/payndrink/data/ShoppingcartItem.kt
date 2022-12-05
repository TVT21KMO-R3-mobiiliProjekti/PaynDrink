package com.example.payndrink.data

data class ShoppingcartItem (
    val id: Int,
    val itemName: String?,
    var itemQty: Int,
    val itemPrice: Double?
)