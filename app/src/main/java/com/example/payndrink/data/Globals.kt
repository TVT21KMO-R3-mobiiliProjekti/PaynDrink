package com.example.payndrink.data

import android.app.Application
import android.content.SharedPreferences

class Globals : Application() {
    companion object {
        var ActiveSeatID : Int = -1
        var ActiveOrderID : Int = -1
        var TrackedOrderIDs : MutableList<Int> = ArrayList()
        var PaymentOK : Boolean = false
        lateinit var sharedPreferences: SharedPreferences
    }
    // Save active seat and order to preferences in phone memory
    fun savePreferences(){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        ActiveSeatID.let { editor.putInt("seat", it) }
        ActiveOrderID.let { editor.putInt("order", it) }
        editor.putInt("orders", TrackedOrderIDs.size)
        var count = 0
        for(orderID in TrackedOrderIDs){
            count++
            val key = "order$count"
            editor.putInt(key, orderID)
        }
        editor.putBoolean("payment", PaymentOK)
        editor.apply()
    }

    // Load active seat and order from preferences in phone memory
    fun loadPreferences(){
        try {
            ActiveSeatID = sharedPreferences.getInt("seat", -1)
            ActiveOrderID = sharedPreferences.getInt("order", -1)
            val orders = sharedPreferences.getInt("orders", 0)
            for (i in 1..orders) {
                val key = "order$i"
                if(!TrackedOrderIDs.contains(sharedPreferences.getInt(key, 0))) {
                    TrackedOrderIDs.add(sharedPreferences.getInt(key, 0))
                }
            }
            PaymentOK = sharedPreferences.getBoolean("payment", false)
        }catch (_: Exception){

        }
    }
}