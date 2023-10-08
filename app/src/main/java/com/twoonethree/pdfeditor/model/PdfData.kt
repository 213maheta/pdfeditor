package com.twoonethree.pdfeditor.model

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class PdfData(
    val name: String,
    val size: String,
    val uri: Uri?,
    val thumbnail: ImageBitmap?,
    val totalPageNumber:Int,
    val addedDate: String? = null
)