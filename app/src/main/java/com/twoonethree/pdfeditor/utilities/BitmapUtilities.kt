package com.twoonethree.pdfeditor.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream

object BitmapUtilities {

    fun resizeBitmap(imageByteArray: ByteArray, width: Float, height: Float): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        val newSize = getProperSize(width, height, bitmap.width, bitmap.height)
        Log.e("TAG", "resizeBitmap: $width $height ${bitmap.width} ${bitmap.height} ${newSize.first} ${newSize.second}", )
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newSize.first, newSize.second, false)

        val outStream = ByteArrayOutputStream()

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        bitmap.recycle()
        scaledBitmap.recycle()
        return outStream.toByteArray()
    }
    fun getProperSize(widthPage: Float, heightPage: Float, widthBitmap: Int, heightBitmap: Int): Pair<Int, Int> {
        if(heightPage>widthPage)
        {
            if(heightBitmap>widthBitmap)
            {
                val bitmapRatio = widthBitmap.toFloat()/heightBitmap
                val newHeight = heightPage
                val newWidth = heightPage * bitmapRatio
                return Pair(newWidth.toInt(), newHeight.toInt())
            }
            else
            {
                val bitmapRatio = heightBitmap.toFloat()/widthBitmap
                val newWidth = widthPage
                val newHeight = widthPage * bitmapRatio
                return Pair(newWidth.toInt(), newHeight.toInt())
            }
        }
        else
        {
            if(heightBitmap>widthBitmap)
            {
                val bitmapRatio = widthBitmap.toFloat()/heightBitmap
                val newHeight = heightPage
                val newWidth = heightPage * bitmapRatio
                return Pair(newWidth.toInt(), newHeight.toInt())
            }
            else
            {
                val bitmapRatio = heightBitmap.toFloat()/widthBitmap
                val newWidth = widthPage
                val newHeight = widthPage * bitmapRatio
                return Pair(newWidth.toInt(), newHeight.toInt())
            }
        }
    }
}