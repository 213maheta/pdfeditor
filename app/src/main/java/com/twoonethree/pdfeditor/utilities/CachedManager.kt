package com.twoonethree.pdfeditor.utilities

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


object CachedManager {

    lateinit var cacheDir: File
    fun init(context: Context) {
        cacheDir = context.cacheDir
    }

    fun isFileExist(uri: Uri): File? {
        val cachedFileName = uriToFileName(uri)
        val cachedFile = File(cacheDir, cachedFileName)
        return if(cachedFile.exists()) cachedFile else null
    }

    fun cachedBitmap(bitmap: Bitmap, uri: Uri): File? {
        val dest = uriToFileName(uri)
        try {
            val outFile = File(cacheDir, dest).apply { createNewFile() }
            val out= FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, out)
            out.flush()
            out.close()
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun cachedImageBytes(imageByteArray: ByteArray, uri: Uri): File? = withContext(Dispatchers.IO){
        Log.e("TAG", "cachedImageBytes: ${imageByteArray.size}", )
        val dest = uriToFileName(uri)
        try {
//            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            val outFile = File(cacheDir, dest).apply { createNewFile() }
            val out= FileOutputStream(outFile)
            out.write(imageByteArray)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
//            bitmap.recycle()
            return@withContext outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    fun uriToFileName(uri: Uri): String {
        return uri.toString().replace("/", "_")
    }
    fun clearCache() {
        cacheDir.delete()
    }

    fun getTempPngFile(): File {
        val pngFile = File(cacheDir, "WaterMark.png")
        pngFile.createNewFile()
        return pngFile
    }
}