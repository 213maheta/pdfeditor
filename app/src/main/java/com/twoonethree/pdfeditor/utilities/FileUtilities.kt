package com.twoonethree.pdfeditor.utilities

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.twoonethree.pdfeditor.model.PdfData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


object FileUtilities {

    fun getFileData(resolver: ContentResolver, uri: Uri): PdfData {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getString(sizeIndex)
        returnCursor.close()
        return PdfData(name = name,
            size = convertByteToMB(size),
            uri = uri,
            thumbnail = PdfUtilities.getPdfThumbnail(resolver = resolver, uri = uri),
            totalPageNumber = PdfUtilities.getTotalPageNumber(resolver = resolver, uri = uri)
        )
    }

    fun convertByteToMB(value: String): String {
        return "${MathUtilities.roundOffDecimal(value.toLong()/1000000.0)} mb"
    }

    fun getAllPdf(resolver: ContentResolver, addAllPdf: (pdfList: List<PdfData>) -> Unit){

        CoroutineScope(Dispatchers.IO).launch {
            val pdfList = mutableListOf<PdfData>()

            val projection = arrayOf(
                MediaStore.Files.FileColumns.DISPLAY_NAME,
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
                        val columnDate  = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                        val columnSize  = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                        do {
                            val name = cursor.getString(columnName)
                            val filePath = cursor.getString(columnData)
                            val addedDate = cursor.getString(columnDate)

                            val file = File(filePath)
                            val uri = file.toUri()

                            val imageBitmap = PdfUtilities.getPdfThumbnail(resolver, uri)
                            val totalPageNumber = PdfUtilities.getTotalPageNumber(resolver,uri)
                            val size = convertByteToMB(file.length().toString())
                            val dateTime = TimeUtilities.convertLongToTime(addedDate.toLong())

                            val pdfData = PdfData(
                                name = name,
                                size = size,
                                uri = file.toUri(),
                                thumbnail = imageBitmap,
                                totalPageNumber = totalPageNumber,
                                addedDate = dateTime
                            )
                            pdfList.add(pdfData)
                        } while (cursor.moveToNext())
                    }
                }
            addAllPdf(pdfList)
        }
    }
}