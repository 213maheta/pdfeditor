package com.twoonethree.pdfeditor.pdfutilities

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.itextpdf.io.source.RandomAccessSourceFactory
import com.itextpdf.kernel.crypto.BadPasswordException
import com.itextpdf.kernel.pdf.EncryptionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.utils.PageRange
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.kernel.utils.PdfSplitter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException


object PdfUtilities {

    fun mergePdf(resolver: ContentResolver, fileList: List<PdfData>, pdfFile: File): Boolean {
        val pdf = PdfDocument(PdfWriter(pdfFile))
        val merger = PdfMerger(pdf)

        fileList.forEach { pdfData ->
            pdfData.uri?.let { uri ->
                val inputStream = resolver.openInputStream(uri)
                val pdfReader = getPdfReader(
                    resolver = resolver,
                    uri = uri,
                    pdfData.password
                )
                val srcPdf = PdfDocument(pdfReader)
                merger.merge(srcPdf, 1, srcPdf.numberOfPages)
                inputStream?.close()
                srcPdf.close()
            }
        }
        try {
            pdf.close()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun splitPdf(
        resolver: ContentResolver,
        srcFile: PdfData,
        dstFile: String,
        splitPointList: List<Int>,
        password: String?,
        setUiEvent: (ScreenCommonEvents) -> Unit
    ) {
        srcFile.uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val inputStream = resolver.openInputStream(it)
                val pdfReader = getPdfReader(
                    resolver = resolver,
                    uri = it,
                    password
                )
                val pdfDoc = PdfDocument(pdfReader)
                val splitDocuments = object : PdfSplitter(pdfDoc) {
                    var partNumber = 1
                    override fun getNextPdfWriter(documentPageRange: PageRange?): PdfWriter? {
                        return try {
                            PdfWriter(String.format(dstFile, partNumber++))
                        } catch (e: FileNotFoundException) {
                            throw RuntimeException(e)
                        }
                    }
                }.splitByPageNumbers(splitPointList)
                setUiEvent(ScreenCommonEvents.ShowToast("Split successfully"))

                delay(1000)
                for (doc in splitDocuments) {
                    try {
                        doc.close()
                    } catch (e: Exception) {
                        Unit
                    }
                }
                pdfDoc.close()
                inputStream?.close()
            }
        }
    }

    fun getTotalPageNumber(resolver: ContentResolver, uri: Uri): Int {
        try {
            val inputStream = resolver.openInputStream(uri)
            val reader = PdfReader(inputStream)
            val pdf = PdfDocument(reader)
            val pageCount = pdf.numberOfPages

            reader.close()
            inputStream?.close()
            pdf.close()
            return pageCount
        } catch (e: BadPasswordException) {
            return 0
        } catch (e: Exception) {
            return -1
        }
    }



    fun getPdfThumbnail(resolver: ContentResolver, uri: Uri): ImageBitmap? {
        resolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
            try {
                val pdfRenderer = PdfRenderer(parcelFileDescriptor).openPage(0)
                val bitmap = Bitmap.createBitmap(
                    pdfRenderer.width,
                    pdfRenderer.height,
                    Bitmap.Config.ARGB_8888
                )
                pdfRenderer.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                pdfRenderer.close()
                return bitmap.asImageBitmap()
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

    fun getPdfPage(
        resolver: ContentResolver,
        uri: Uri,
        screenWidth: Int
    ): MutableList<ImageBitmap> {
        val bitmapList = mutableListOf<ImageBitmap>()
        resolver.openFileDescriptor(uri, "r")?.use {
            val pdfRenderer = PdfRenderer(it)
            val pageCount = pdfRenderer.pageCount
            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)
                val heightFactor = page.height / page.width.toFloat()
                val bitmap = Bitmap.createBitmap(
                    screenWidth,
                    (screenWidth * heightFactor).toInt(),
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                bitmapList.add(bitmap.asImageBitmap())
            }
            pdfRenderer.close()
        }
        return bitmapList
    }

    fun addPageNumber(
        resolver: ContentResolver,
        uri: Uri,
        callBack: (ScreenCommonEvents) -> Unit,
        password: String?,
        getXYPosition: (Float, Float) -> Pair<Float, Float>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val src = resolver.openInputStream(uri)
            val dst = FileManager.createPdfFile()
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password
            )
            val pdfDoc = PdfDocument(pdfReader, PdfWriter(dst))
            val doc = Document(pdfDoc)
            val numberOfPages = pdfDoc.numberOfPages
            val x = pdfDoc.firstPage.pageSize.width
            val y = pdfDoc.firstPage.pageSize.height

            val position = getXYPosition(x, y)
            for (i in 1..numberOfPages) {
                doc.showTextAligned(
                    Paragraph(String.format("page %s of %s", i, numberOfPages)),
                    position.first,
                    position.second,
                    i,
                    TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE,
                    0f
                )
            }
            src?.close()
            pdfDoc.close()
            doc.close()
            callBack(ScreenCommonEvents.ShowToast("Page number added successfully"))
        }
    }

    fun changeOrientation(
        resolver: ContentResolver,
        uri: Uri,
        callBack: (ScreenCommonEvents) -> Unit,
        value: Int,
        password: String?,
        function: () -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val src = resolver.openInputStream(uri)
            val dst = FileManager.createPdfFile()
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password = password
            )
            val pdfDoc = PdfDocument(pdfReader, PdfWriter(dst))
            val doc = Document(pdfDoc)
            val numberOfPages = pdfDoc.numberOfPages

            for (i in 1..numberOfPages) {
                val page = pdfDoc.getPage(i)
                page.setRotation(value)
            }
            src?.close()
            pdfDoc.close()
            doc.close()
            function()
            callBack(ScreenCommonEvents.ShowToast("Orientation changed successfully"))
        }

    }

    fun getOrientation(
        resolver: ContentResolver,
        uri: Uri,
    ): Int {
        return try {
            val src = resolver.openInputStream(uri)
            val pdfDoc = PdfDocument(PdfReader(src).also { it.setUnethicalReading(true) })
            val orientaion = pdfDoc.firstPage.rotation
            src?.close()
            pdfDoc.close()
            orientaion
        } catch (e: BadPasswordException) {
            -1
        } catch (e: Exception) {
            -2
        }

    }

    fun setPassword(
        resolver: ContentResolver,
        uri: Uri,
        password: String,
        prePassword: String?,
        callBack: (ScreenCommonEvents) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val src = resolver.openInputStream(uri)
            val dst = FileManager.createPdfFile()
            val props = WriterProperties()
                .setStandardEncryption(
                    password.toByteArray(),
                    password.toByteArray(),
                    EncryptionConstants.ALLOW_PRINTING,
                    EncryptionConstants.STANDARD_ENCRYPTION_128
                )
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                prePassword
            )

            val pdfDoc = PdfDocument(
                pdfReader,
                PdfWriter(dst.outputStream(), props)
            )
            pdfDoc.close()
            callBack(ScreenCommonEvents.ShowToast("Password added successfully"))
        }
    }

    fun removePassword(
        resolver: ContentResolver,
        uri: Uri,
        password: String?,
        callBack: (ScreenCommonEvents) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val dst = FileManager.createPdfFile()
            val pdfReader = getPdfReader(
            resolver = resolver,
            uri = uri,
            password = password
        )
            try {
                val pdfDoc = PdfDocument(
                    pdfReader,
                    PdfWriter(dst.outputStream())
                )
                pdfDoc.close()
                callBack(ScreenCommonEvents.ShowToast("Password removed successfully"))
            } catch (e: Exception) {
                callBack(ScreenCommonEvents.ShowToast("Something gone wrong"))
            }
        }
    }

    fun getPdfReader(
        resolver: ContentResolver,
        uri: Uri,
        password: String?,
    ): PdfReader {
        val src = resolver.openInputStream(uri)
        val props = ReaderProperties().setPassword(password?.toByteArray())
        return PdfReader(src, props).also { it.setUnethicalReading(true) }
    }

    fun checkPdfPassword(
        resolver: ContentResolver,
        uri: Uri,
        password: String,
        callBack: (ScreenCommonEvents) -> Unit
    ) {
        try {
            val src = resolver.openInputStream(uri)
            val props = ReaderProperties().setPassword(password.toByteArray())
            val pdfReader = PdfReader(src, props).also { it.setUnethicalReading(true) }

            val pdfDoc = PdfDocument(pdfReader)
            val totalPageNumber = pdfDoc.numberOfPages
            callBack(
                ScreenCommonEvents.GotPassword(
                    totalPageNumber = totalPageNumber,
                    password = password
                )
            )
            pdfDoc.close()
            pdfReader.close()
            src?.close()
        } catch (e: BadPasswordException) {
            callBack(ScreenCommonEvents.ShowToast("Password is incorrect"))
        } catch (e: Exception) {
            callBack(ScreenCommonEvents.ShowToast("Something went wrong"))
        }
    }


    fun reOrderPdf(
        resolver: ContentResolver,
        uri: Uri,
        password: String?,
        pageOrderList:List<Int>,
        callBack: (ScreenCommonEvents) -> Unit,
    )
    {
        val inputStream = resolver.openInputStream(uri)
        val dst = FileManager.createPdfFile()
        val pdfReader = getPdfReader(
            resolver = resolver,
            uri = uri,
            password
        )
        val srcDoc = PdfDocument(pdfReader)
        val dstDoc = PdfDocument(PdfWriter(dst))

        dstDoc.initializeOutlines()

        srcDoc.copyPagesTo(pageOrderList, dstDoc)

        inputStream?.close()
        srcDoc.close()
        dstDoc.close()
        callBack(ScreenCommonEvents.ShowToast("PDF reorder successfully"))
    }
}
