package com.twoonethree.pdfeditor.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, out)
            out.flush()
            out.close()
            bitmap.recycle()
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun cachedBitmap(bitmap: Bitmap, uri: Uri, index:Int): File? {
        val dest = uriToFileName(uri)
        try {
            val outFile = File(cacheDir, "${dest}_$index").apply { createNewFile() }
            val out= FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            return outFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun isFileExist(uri: Uri, index:Int): File? {
        val cachedFileName = uriToFileName(uri)
        val cachedFile = File(cacheDir,  "${cachedFileName}_$index")
        return if(cachedFile.exists()) cachedFile else null
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