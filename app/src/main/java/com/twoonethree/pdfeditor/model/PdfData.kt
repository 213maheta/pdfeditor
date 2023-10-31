package com.twoonethree.pdfeditor.model

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class PdfData(
    val name: String,
    val size: String,
    val uri: Uri?,
    var totalPageNumber:Int,
    val date: String? = null,
    var password:String? = null
)