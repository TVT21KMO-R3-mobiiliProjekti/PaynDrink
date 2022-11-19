package com.example.payndrink.data

import android.app.Application
import com.example.payndrink.database.Item
import com.example.payndrink.database.Restaurant

class Globals : Application() {
    companion object {
        var ActiveSeatID : Int? = null
        var ActiveOrderID : Int? = null
    }

}