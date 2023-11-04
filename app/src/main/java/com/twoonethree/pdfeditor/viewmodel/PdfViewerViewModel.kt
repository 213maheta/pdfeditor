package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfViewerViewModel: ViewModel() {

     val pdfPageList = mutableStateListOf<ImageBitmap>()
     fun getAllPage(resolver: ContentResolver, uri: Uri, screenWidth: Int) = viewModelScope.launch(Dispatchers.Default)
     {
          pdfPageList.addAll(PdfUtilities.getPdfPage(resolver, uri, screenWidth))
     }

}