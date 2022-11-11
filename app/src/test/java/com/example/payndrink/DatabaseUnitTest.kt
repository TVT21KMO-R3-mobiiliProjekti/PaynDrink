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
}