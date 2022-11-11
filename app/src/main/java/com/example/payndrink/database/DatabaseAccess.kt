package com.example.payndrink.database

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

//restaurant model class
data class Restaurant(val id: Int?, val name: String?, val address: String?, val description: String?,
                      val pictureUrl: String?, val typeID: Int?)

//item model class
data class Item(val id: Int?, val name: String?, val quantity: Int?, val description: String?,
                val price: Double?, val quick: Int?, val pictureUrl: String?, val type: Int?,
                val restaurantID: Int?)

//seating model class
data class Seating(val id: Int, val seatNumber: Int, val restaurantID: Int)

//waiter model class
data class Waiter(val id: Int, val firstName: String?, val lastName: String?, val description: String?,
                  val pictureUrl: String?, val restaurantID: Int)

//order model class
data class Order(val id: Int, val price: Double, val placed: Date, val fulfilled: Date,
                 val refund: Double?, val refundReason: String?, val restaurantID: Int,
                 val seatingID: Int, val waiterID: Int)

//tip model class
data class Tip(val id: Int, val amount: Double?, val orderID: Int, val waiterID: Int)

//restaurant type model class
data class RestaurantType(val id: Int, val name: String, val pictureUrl: String?)

//seating type model class
data class SeatingType(val id: Int, val name: String, val seatingID: Int)

//item type model class
data class ItemType(val id: Int, val name: String, val pictureUrl: String?)

//item has types model class
data class ItemHasTypes(val id: Int, val itemID: Int, val typeID: Int)

//seating has items model class
data class SeatingHasItems(val id: Int, val seatingTypeID: Int, val itemTypeID: Int)

//order has items model class
data class OrderHasItems(val id: Int, val quantity: Int, val deliveredQuantity: Int?,
                         val itemID: Int, val orderID: Int)

class DatabaseAccess {

    fun connectToDatabase(): Connection? {
        val jdbcUrl =
            "jdbc:postgresql://dpg-cdj2l8mn6mpngrtb5a5g-a.frankfurt-postgres.render.com/" +
                    "tvt21kmo_r3_mobiiliprojekti"
        val userName = "tvt21kmo_r3"
        val password = "H9V1M6gtYtQmWg6nJiqU0sstkCs2LxTl"
        return DriverManager.getConnection(jdbcUrl, userName, password)
    }

    fun getRestaurant(connection: Connection, restaurantID: Int): Restaurant {
        val query = connection.prepareStatement(
            "SELECT * FROM restaurant WHERE id_restaurant=$restaurantID"
        )
        var id: Int? = null
        var name: String? = null
        var address: String? = null
        var description: String? = null
        var pictureUrl: String? = null
        var typeID: Int? = null
        val result = query.executeQuery()
        while(result.next()){
            id = result.getInt("id_restaurant")
            name = result.getString("restaurant_name")
            address = result.getString("restaurant_address")
            description = result.getString("restaurant_description")
            pictureUrl = result.getString("picture_url")
            typeID = result.getInt("id_restaurant_type")
        }
        return Restaurant(id, name, address, description, pictureUrl, typeID)
    }

    fun getItems(connection: Connection, restaurantID: Int): MutableList<Item>{
        val query = "SELECT * FROM item WHERE id_restaurant=$restaurantID"
        val result = connection.prepareStatement(query).executeQuery()
        val items = mutableListOf<Item>()
        while(result.next()){
            val id = result.getInt("id_item")
            val name = result.getString("item_name")
            val quantity = result.getInt("quantity")
            val price = result.getDouble("price")
            val quick = result.getInt("quick_order")
            val description = result.getString("item_description")
            val pictureUrl = result.getString("picture_url")
            val type = result.getInt("item_type")
            items.add(Item(id, name, quantity, description, price, quick, pictureUrl, type,
                restaurantID))
        }
        return items
    }
}