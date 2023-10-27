package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities

class PdfViewerViewModel: ViewModel() {

     val pdfPageList = mutableStateListOf<ImageBitmap>()
     fun getAllPage(resolver: ContentResolver, uri: Uri, screenWidth: Int)
     {
          pdfPageList.addAll(PdfUtilities.getPdfPage(resolver, uri, screenWidth))
     }

}