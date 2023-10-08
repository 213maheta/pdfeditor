package com.twoonethree.pdfeditor.utilities

import android.content.Context
import android.os.Build
import android.os.Environment
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appDir =
                File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/$appName")
        } else {
            appDir = File("${Environment.getExternalStorageDirectory()}/$appName")
        }
    }

    fun createPdfFile(): File {
        val pdfFile = File("$appDir/${getFileName()}")
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

    private fun deletePdfFile(file: File) {
        file.deleteOnExit()
    }

}