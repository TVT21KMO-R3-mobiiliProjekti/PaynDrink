package com.example.payndrink.data

class Utilities {
    /** Check is string numeric */
    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }
}