package com.twoonethree.pdfeditor.pdfviewver

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PdfViewerViewModel: ViewModel() {

     val pdfPageList = mutableStateListOf<File>()
     lateinit var selectedUri:Uri
     var screenWidth: Int = 0
     var pdfIterator = 0
     val showProgressBar = mutableStateOf(false)

     fun getPdfPage(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.IO)
     {
          if(!showProgressBar.value)
          {
               showProgressBar.value = true

               val dataList = PdfUtilities.getPdfPage(resolver, selectedUri, screenWidth, pdfIterator, 20)
               pdfPageList.addAll(dataList)

               pdfIterator = pdfPageList.size

               showProgressBar.value = false
          }
     }
}