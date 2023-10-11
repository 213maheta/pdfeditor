package com.twoonethree.pdfeditor.utilities

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtilities {
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time*1000)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return format.format(date)
    }
}