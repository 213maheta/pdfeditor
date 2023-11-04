package com.twoonethree.pdfeditor.utilities

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


object FileManager {

    private val appName = "PDF_Editor"
    var appDir: File? = null

    fun createAppDirectory() {
        getAppDirectoryPath()
        if(!isAppDirectoryExist())
            appDir?.mkdirs()
    }

    fun isAppDirectoryExist(): Boolean {
        appDir?.let {
            return it.exists()
        }
        return false
    }

    fun getAppDirectoryPath() {
        when(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            true -> appDir = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/$appName")
            false -> appDir = File("${Environment.getExternalStorageDirectory()}/$appName")
        }
    }

    fun createPdfFile(customeName:String? = null): File {
        val fileName = customeName?:getFileName()
        val pdfFile = File("$appDir/$fileName")
        pdfFile.createNewFile()
        return pdfFile
    }

    fun getSplitFilePath(): String {
        val DEST = "${appDir}/${getFileName()}_%s.pdf"
        return DEST
    }

    private fun getFileName(): String {
        val fileName = "PDF_${TimeUtilities.getCurrentTime()}.pdf"
        return fileName
    }


    fun deleteFile(uri: Uri): Boolean {
        return uri.toFile().delete()
    }

    suspend fun renameFile(src: Uri, dstName:String): Boolean = withContext(Dispatchers.IO) {
        val dst = createPdfFile(dstName)
        return@withContext src.toFile().renameTo(dst)
    }

}