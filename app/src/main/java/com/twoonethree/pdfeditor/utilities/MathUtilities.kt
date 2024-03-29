package com.twoonethree.pdfeditor.utilities

import java.math.RoundingMode
import java.text.DecimalFormat

object MathUtilities {
    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}