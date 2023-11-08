package com.twoonethree.pdfeditor.utilities

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.twoonethree.pdfeditor.model.PdfData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


object FileUtilities {

    suspend fun getFileData(resolver: ContentResolver, uri: Uri): PdfData {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getString(sizeIndex)
        returnCursor.close()
        return PdfData(
            name = name,
            size = convertByteToMB(size),
            uri = uri,
            totalPageNumber = PdfUtilities.getTotalPageNumber(resolver = resolver, uri = uri)
        )
    }

    fun convertByteToMB(value: String): String {
        return "${MathUtilities.roundOffDecimal(value.toLong() / 1000000.0)} mb"
    }

    suspend fun getAllPdf(resolver: ContentResolver):List<PdfData> = withContext(Dispatchers.IO){

            val pdfList = mutableListOf<PdfData>()

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
            )

            val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

            val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"

            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
            val selectionArgs = arrayOf(mimeType)

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Files.getContentUri("external")
            }

            resolver.query(collection, projection, selection, selectionArgs, sortOrder)
                .use { cursor ->
                    assert(cursor != null)
                    if (cursor?.moveToFirst() == true) {
                        val columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                        val columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                        val columnModifiedDate = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)
                        val columnAddedDate = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                        val columnSize = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                        do {
                            val name = cursor.getString(columnName)
                            val filePath = cursor.getString(columnData)
                            val modifiedDate = cursor.getString(columnModifiedDate)
                            val addedDate = cursor.getString(columnAddedDate)

                            val file = File(filePath)
                            val size = convertByteToMB(file.length().toString())
                            val dateTime =
                                if (modifiedDate.isNullOrEmpty())
                                    TimeUtilities.convertLongToTime(addedDate.toLong())
                                else
                                    TimeUtilities.convertLongToTime(modifiedDate.toLong())

                            val pdfData = PdfData(
                                name = name,
                                size = size,
                                uri = file.toUri(),
                                totalPageNumber = -2,
                                date = dateTime
                            )
                            pdfList.add(pdfData)
                        } while (cursor.moveToNext())
                    }
                }
        return@withContext pdfList

    }
}