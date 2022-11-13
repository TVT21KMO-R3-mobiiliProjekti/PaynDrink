package com.example.payndrink

import com.example.payndrink.database.DatabaseAccess
import junit.framework.TestCase.*
import org.junit.Test

class DatabaseUnitTest {
    @Test
    fun connectionTest(){
        val dbAccess = DatabaseAccess()
        assertEquals(true, dbAccess.connectToDatabase()!!.isValid(0))
    }
    @Test
    fun getRestaurantTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 1
        val restaurant = connection?.let { dbAccess.getRestaurant(it, restaurantID) }
        assertNotNull(restaurant!!.id)
    }

    @Test
    fun getRestaurantBySeatingTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val seatingID = 1
        val restaurant = connection?.let { dbAccess.getRestaurantBySeating(it, seatingID) }
        assertNotNull(restaurant!!.id)
    }

    @Test
    fun getItemsTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 1
        val items = connection?.let { dbAccess.getItems(it, restaurantID) }
        val itemCount = items?.size
        if (itemCount != null) {
            assertTrue(itemCount > 0)
        }
    }
    @Test
    fun getFoodTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 1
        val items = connection?.let { dbAccess.getFoodItems(it, restaurantID) }
        val itemCount = items?.size
        if (itemCount != null) {
            assertTrue(itemCount > 0)
        }
    }

    @Test
    fun getDrinksTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 1
        val items = connection?.let { dbAccess.getDrinks(it, restaurantID) }
        val itemCount = items?.size
        if (itemCount != null) {
            assertTrue(itemCount > 0)
        }
    }

    @Test
    fun getQuickOrderItemsTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 1
        val items = connection?.let { dbAccess.getQuickOrderItems(it, restaurantID) }
        val itemCount = items?.size
        if (itemCount != null) {
            assertTrue(itemCount > 0)
        }
    }

    @Test
    fun addItemToOrderTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val itemID = 3
        val quantity = 5
        val orderID = 1
        val id: Int? = connection?.let { dbAccess.addItemToOrder(it, quantity, itemID, orderID) }
        assertNotNull(id)
    }

    @Test
    fun createOrderTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurantID = 3
        val seatingID = 5
        assertNotNull(connection?.let { dbAccess.createOrder(it, restaurantID, seatingID) })
    }

    @Test
    fun getItemPriceTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val itemID = 2
        val price = connection?.let { dbAccess.getItemPrice(it, itemID) }
        assertNotNull(price)
    }

    @Test
    fun getOrderPriceTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val orderID = 7
        val price = connection?.let { dbAccess.getOrderPrice(it, orderID) }
        if (price != null) {
            assertTrue(price > 0)
        }
    }

    @Test
    fun sendOrderTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val orderID = 1
        assertNotNull(connection?.let { dbAccess.sendOrder(it, orderID) })
    }

    @Test
    fun getItemTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val id = 1
        val item = connection?.let { dbAccess.getItem(it, id) }
        assertNotNull(item)
    }

    @Test
    fun updateItemInOrderTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val itemID = 1
        val quantity = 3
        val orderID = 1
        val id: Int? = connection?.let { dbAccess.updateItemInOrder(it, quantity, itemID, orderID) }
        assertNotNull(id)
    }

    @Test
    fun deleteItemInOrderTest(){
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val orderItemID = 1
        val id: Int? = connection?.let { dbAccess.deleteItemInOrder(it, orderItemID) }
        assertNotNull(id)
    }
}