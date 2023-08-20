package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommonFunctions {
    fun convertMillisToTimeString(millis: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun convertMillisToDateString(millis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun convertMillisToDateTimeString(millis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }
}