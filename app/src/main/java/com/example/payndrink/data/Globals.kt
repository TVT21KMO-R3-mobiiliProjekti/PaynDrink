package com.example.payndrink.data

import android.app.Application

class Globals : Application() {
    companion object {
        var ActiveSeatID : Int? = null
        var ActiveOrderID : Int? = null
        var PendingOrderID : Int? = null
        var PaymentOK : Boolean = false
    }

}