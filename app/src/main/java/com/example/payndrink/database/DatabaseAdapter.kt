package com.example.payndrink.database

class DatabaseAdapter {

    fun getItemsByRestaurant(id : Int): MutableList<Item>? {
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        return connection?.let { dbAccess.getItems(it, id) }
    }

}