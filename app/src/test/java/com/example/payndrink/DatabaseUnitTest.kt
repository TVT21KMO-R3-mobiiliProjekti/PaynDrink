package com.example.payndrink

import com.example.payndrink.database.DatabaseAccess
import junit.framework.TestCase.assertEquals
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
        if (restaurant != null) {
            assertEquals(restaurantID, restaurant.id)
        }
    }
}