package com.example.payndrink.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL

class Utilities {
    /** Check is string numeric */
    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    /** Get image bitmap from URL */
    fun getImageBitmapFromURL(url: String?): Bitmap? {
        val bm : Bitmap? = try {
            BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
        } catch (e: Exception) {
            return null
        }
        return bm
    }
}