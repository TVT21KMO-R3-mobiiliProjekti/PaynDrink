package com.example.payndrink.data

data class StatusItem (
    val id: Int,
    val itemName: String?,
    val orderedQty: Int,
    val refundedQty: Int,
    val deliveredQty: Int,
    val rejected: Boolean
)