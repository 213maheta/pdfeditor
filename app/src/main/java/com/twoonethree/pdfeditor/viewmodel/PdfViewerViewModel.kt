package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class PdfViewerViewModel: ViewModel() {

     val pdfPageList = mutableStateListOf<ImageBitmap>()
     fun getAllPage(resolver: ContentResolver, uri: Uri, screenWidth: Int)
     {
          pdfPageList.addAll(PdfUtilities.getPdfPage(resolver, uri, screenWidth))
     }

}