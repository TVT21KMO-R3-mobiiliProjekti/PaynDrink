package com.example.payndrink.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.WindowManager.BadTokenException
import androidx.lifecycle.viewmodel.CreationExtras
import java.net.URL

class Utilities {
    /** Check is string numeric */
    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    /** Git imagebitmap from URL */
    fun getImageBitmapFromURL(url: String?): Bitmap? {
        var bm : Bitmap?
        try {
            bm = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
        }
        catch (e: Exception) {
            bm = null
        }
        return bm
    }
}