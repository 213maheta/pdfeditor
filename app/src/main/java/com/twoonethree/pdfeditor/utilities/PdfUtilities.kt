package com.twoonethree.pdfeditor.utilities

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.crypto.BadPasswordException
import com.itextpdf.kernel.geom.AffineTransform
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.EncryptionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.kernel.utils.PageRange
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.kernel.utils.PdfSplitter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import java.io.File
import java.io.FileNotFoundException


object PdfUtilities {
    suspend fun mergePdf(
        resolver: ContentResolver,
        dataList: List<Triple<Uri?, String?, Int>>,
        pdfFile: File,
        onProgress: (Float) -> Unit,
    ): Boolean {
        try {
            val pdf = PdfDocument(PdfWriter(pdfFile))
            val merger = PdfMerger(pdf)

            val totalPageNumber = dataList.map { it.third }.reduce { a: Int, b: Int -> a + b }
            var donePageNumber = 0

            dataList.forEach { triplet ->
                triplet.first?.let { uri ->
                    val inputStream = resolver.openInputStream(uri)
                    val pdfReader = getPdfReader(
                        resolver = resolver,
                        uri = uri,
                        triplet.second
                    )
                    val srcPdf = PdfDocument(pdfReader)
                    merger.merge(srcPdf, 1, srcPdf.numberOfPages)
                    donePageNumber += srcPdf.numberOfPages
                    inputStream?.close()
                    srcPdf.close()
                    pdfReader.close()
                    val progress = donePageNumber * 1f / totalPageNumber
                    onProgress(progress)
                }
            }
            pdf.close()
            return true
        } catch (e: Exception) {
            pdfFile.delete()
            return false
        }
    }

    suspend fun splitPdf(
        resolver: ContentResolver,
        srcFile: Uri,
        dstFile: String,
        splitPointList: List<Int>,
        password: String?,
    ): Boolean  {
        try {
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = srcFile,
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

            for (doc in splitDocuments) {
                doc.close()
            }
            pdfDoc.close()
            pdfReader.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getTotalPageNumber(resolver: ContentResolver, uri: Uri): Int {
            try {
                val inputStream = resolver.openInputStream(uri)
                val pdfReader = PdfReader(inputStream)
                val pdf = PdfDocument(pdfReader)
                val pageCount = pdf.numberOfPages

                inputStream?.close()
                pdf.close()
                pdfReader.close()
                return pageCount
            } catch (e: BadPasswordException) {
                return 0
            } catch (e: Exception) {
                return -1
            }
        }


    suspend fun getPdfThumbnail(resolver: ContentResolver, uri: Uri): Bitmap?{
            resolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
                try {
                    val pdfRenderer = PdfRenderer(parcelFileDescriptor).openPage(0)
                    val bitmap = Bitmap.createBitmap(
                        50,
                        50,
                        Bitmap.Config.ARGB_8888
                    )
                    pdfRenderer.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfRenderer.close()
                    return bitmap
                } catch (e: Exception) {
                    return null
                }
            }
            return null
        }

    suspend fun cachedThumbnail(resolver: ContentResolver, uri: Uri): File? {
            CachedManager.isFileExist(uri)?.let {
                return it
            }?: kotlin.run {
                resolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
                    try {
                        val pdfRenderer = PdfRenderer(parcelFileDescriptor).openPage(0)
                        val bitmap = Bitmap.createBitmap(
                            50,
                            50,
                            Bitmap.Config.ARGB_8888
                        )
                        pdfRenderer.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        pdfRenderer.close()
                        return CachedManager.cachedBitmap(bitmap, uri)
                    } catch (e: Exception) {
                        return null
                    }
                }
                return null
            }
        }

    suspend fun getBitmap(pdfRenderer:PdfRenderer, i:Int): Bitmap {
        val page = pdfRenderer.openPage(i)
        val bitmap = Bitmap.createBitmap(
            50,
            50,
            Bitmap.Config.ARGB_8888
        )
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }

    suspend fun getPdfPage(
        resolver: ContentResolver,
        uri: Uri,
        screenWidth: Int,
        iterator: Int,
        slotSize: Int
    ): List<File>
    {
        val imageList = mutableListOf<File>()

        Log.e("TAG", "getPdfPage: ${System.currentTimeMillis()}", )
        resolver.openFileDescriptor(uri, "r")?.use {
            Log.e("TAG", "getPdfPage: ${System.currentTimeMillis()}", )
            val pdfRenderer = PdfRenderer(it)
            val pageCount = pdfRenderer.pageCount

            if(iterator >= pageCount)
                return imageList

            var iterateUntil = iterator + slotSize

            if (iterateUntil > pageCount)
                iterateUntil = pageCount

            for (i in iterator until iterateUntil) {
                val value = CachedManager.isFileExist(uri, i)
                value?.let {
                    imageList.add(it)
                }?: kotlin.run {
                    val page = pdfRenderer.openPage(i)
                    val heightFactor = page.height / page.width.toFloat()
                    val bitmap = Bitmap.createBitmap(
                        screenWidth,
                        (screenWidth * heightFactor).toInt(),
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    val data = CachedManager.cachedBitmap(bitmap, uri, i)
                    data?.let { it1 -> imageList.add(it1) }
                    bitmap.recycle()
                }
            }
            pdfRenderer.close()
        }

        return imageList
    }

    suspend fun addPageNumber(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        getXYPosition: (Rectangle) -> Pair<Float, Float>,
        onProgress: (Float) -> Unit,
    ): Boolean  {
        try {
            val src = resolver.openInputStream(uri)

            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password
            )
            val pdfDoc = PdfDocument(pdfReader, PdfWriter(dst))
            val doc = Document(pdfDoc)
            val numberOfPages = pdfDoc.numberOfPages
            val pageSize = pdfDoc.firstPage.pageSize

            val position = getXYPosition(pageSize)
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
                val progress = i * 1f / numberOfPages
                onProgress(progress)
            }
            src?.close()
            pdfDoc.close()
            doc.close()
            pdfReader.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun changeOrientation(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        value: Int,
        password: String?,
        onProgress: (Float) -> Unit,
    ): Boolean {
        try {
            val src = resolver.openInputStream(uri)
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
                val progress = i * 1f / numberOfPages
                onProgress(progress)
                page.flush()
            }
            src?.close()
            pdfDoc.close()
            doc.close()
            pdfReader.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun getOrientation(
        resolver: ContentResolver,
        uri: Uri,
    ): Int  {
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

    suspend fun setPassword(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String,
        prePassword: String?,
    ): Boolean  {
        try {
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
            pdfReader.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun removePassword(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
    ): Boolean {
        try {
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password = password
            )
            val pdfDoc = PdfDocument(
                pdfReader,
                PdfWriter(dst.outputStream())
            )
            pdfDoc.close()
            pdfReader.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun getPdfReader(
        resolver: ContentResolver,
        uri: Uri,
        password: String?,
    ): PdfReader  {
        val src = resolver.openInputStream(uri)
        val props = ReaderProperties().setPassword(password?.toByteArray())
        return PdfReader(src, props).also { it.setUnethicalReading(true) }
    }

    suspend fun checkPdfPassword(
        resolver: ContentResolver,
        uri: Uri,
        password: String,
    ): Int {
        try {
            val src = resolver.openInputStream(uri)
            val props = ReaderProperties().setPassword(password.toByteArray())
            val pdfReader = PdfReader(src, props).also { it.setUnethicalReading(true) }

            val pdfDoc = PdfDocument(pdfReader)
            val totalPageNumber = pdfDoc.numberOfPages

            pdfDoc.close()
            pdfReader.close()
            src?.close()
            pdfReader.close()
            return totalPageNumber
        } catch (e: BadPasswordException) {
            return 0
        } catch (e: Exception) {
            return -1
        }
    }


    suspend fun reOrderPdf(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        pageOrderList: List<Int>,
    ): Boolean {
        try {
            val inputStream = resolver.openInputStream(uri)
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
            pdfReader.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun imageToPdf(
        resolver: ContentResolver,
        uriList: List<Uri>,
        dst: File,
    ): Boolean {
        try {
            val pdfDocument = PdfDocument(PdfWriter(dst)).also {
                it.defaultPageSize = PageSize.A4
            }
            val document = Document(pdfDocument)

            uriList.forEach {
                val srcStream = resolver.openInputStream(it)
                val imageByteArray = srcStream?.readBytes()
                val imageData = ImageDataFactory.create(imageByteArray)
                val image = Image(imageData)
                document.pdfDocument.defaultPageSize = PageSize(image.imageWidth, image.imageHeight)
                document.add(image)
                srcStream?.close()
            }
            pdfDocument.close()
            document.close()
            return true
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun addWaterMark(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        imageUri: Uri,
        onProgress: (Float) -> Unit,
    ): Boolean {
        try {
            val pdfWriter = PdfWriter(dst)
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password
            )
            val pdfDoc = PdfDocument(pdfReader, pdfWriter)
            val doc = Document(pdfDoc)

            val imgStream = resolver.openInputStream(imageUri)

            imgStream?.let {

                val pdfPage1 = pdfDoc.getPage(1)
                val pageSize1 = pdfPage1.pageSizeWithRotation

                val orgImgBytes = it.readBytes()
                val imageByteArray = BitmapUtilities.resizeBitmap(orgImgBytes, pageSize1.width, pageSize1.height)
                val imageData = ImageDataFactory.create(imageByteArray)

                Log.e("TAG", "addWaterMark1: ${orgImgBytes.size} ${imageByteArray.size}")
                val w = imageData.width
                val h = imageData.height

                Log.e("TAG", "addWaterMark2: $w $h ${imageData.rotation}")

                val gs1 = PdfExtGState().setFillOpacity(0.5f)

                for (i in 1..pdfDoc.numberOfPages) {
                    val pdfPage = pdfDoc.getPage(i)
                    val pageSize = pdfPage.pageSize

                    Log.e("TAG", "addWaterMark3: ${pageSize}")

                    pdfPage.isIgnorePageRotationForContent = false
                    val x = (pageSize.getLeft() + pageSize.getRight()) / 2
                    val y = (pageSize.getTop() + pageSize.getBottom()) / 2
                    val over = PdfCanvas(pdfDoc.getPage(i))
                    over.saveState()
                    over.setExtGState(gs1)
                    over.addImage(imageData, x - w / 2, y - h / 2, false)
                    over.restoreState()
                    pdfPage.flush()
                    val progress = i * 1f / pdfDoc.numberOfPages
                    onProgress(progress)
                }
                doc.close()
                pdfDoc.close()
                imgStream.close()
                return true
            }

            return false
        } catch (e: Exception) {
            dst.delete()
            return false
        }
    }

    suspend fun compressPdf(
        resolver: ContentResolver,
        uri: Uri,
        dst: File,
        password: String?,
        onProgress: (Float) -> Unit,
    ): Boolean  {
        try {
            val pdfReader = getPdfReader(
                resolver = resolver,
                uri = uri,
                password = password
            )
            val pdfWriter = PdfWriter(dst)

            val destpdf = PdfDocument(pdfWriter)
            val srcPdf = PdfDocument(pdfReader)

            for (i in 1..srcPdf.numberOfPages) {
                val origPage = srcPdf.getPage(i)
                val orig = origPage.pageSizeWithRotation
                val page = destpdf.addNewPage(PageSize(orig.width * 0.5f, orig.height * 0.5f))

                val transformationMatrix = AffineTransform.getScaleInstance(
                    0.5,
                    0.5
                )
                val canvas = PdfCanvas(page)
                canvas.concatMatrix(transformationMatrix)
                val pageCopy = origPage.copyAsFormXObject(destpdf)
                canvas.addXObject(pageCopy, orig.left, orig.top)

                val progress = i * 1f / srcPdf.numberOfPages
                onProgress(progress)
            }

            val doc = Document(destpdf)

            doc.close()
            return true
        } catch (e: Exception) {
            Log.e("TAG", "compressPdf: ${e.message}")
            dst.delete()
            return false
        }
    }
}
