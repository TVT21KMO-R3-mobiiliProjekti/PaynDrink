package com.example.payndrink.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Globals : Application() {
    companion object {
        var ActiveSeatID : Int? = null
        var ActiveOrderID : Int? = null
    }

    // Save active seat and order to preferences in phone memory
    fun savePreferences(){
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        ActiveSeatID?.let { editor.putInt("seat", it) }
        ActiveOrderID?.let { editor.putInt("order", it) }
        editor.apply()
    }

    // Load active seat and order from preferences in phone memory
    fun loadPreferences(){
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        ActiveSeatID = sharedPreferences.getInt("seat", 0)
        ActiveOrderID = sharedPreferences.getInt("order", 0)
    }
}