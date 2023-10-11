package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.itextpdf.kernel.pdf.PdfReader
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class SplitPDFViewModel() : ViewModel() {

     val splitPointList = mutableStateListOf<Int>()
     var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))
     var totalPageNumber = 0
     var pdfReader:PdfReader? = null

     val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

     fun setUiIntent(value: ScreenCommonEvents) {
          uiIntent.value = value
     }

     fun saveSplitPdf(contentResolver: ContentResolver)
     {
          when{
               splitPointList.size<1 -> setUiIntent(ScreenCommonEvents.ShowToast("Add at least one split point"))
               else ->{
                    PdfUtilities.splitPdf(
                         contentResolver,
                         selectedPdf.value,
                         FileManager.getSplitFilePath(),
                         splitPointList.toList().sortedBy { it },
                         pdfReader,
                         ::setUiIntent
                    )
               }
          }
     }

     fun addSplitPoints(value:String)
     {
          if(value.isEmpty())
               return
          if(totalPageNumber == 0)
          {
               setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
               return
          }
          val splitPoint = value.toInt()

          when{
               splitPoint > totalPageNumber -> setUiIntent(ScreenCommonEvents.ShowToast("Split point should be less than total page count"))
               splitPoint < 1 -> setUiIntent(ScreenCommonEvents.ShowToast("Split point be should more than 0"))
               splitPointList.contains(splitPoint) -> setUiIntent(ScreenCommonEvents.ShowToast("Already added"))
               else -> splitPointList.add(splitPoint)
          }
     }

     fun removeSplitPoints(value:Int)
     {
          splitPointList.remove(value)
     }

     fun updatePageNumber()
     {
          selectedPdf.value = selectedPdf.value.copy(totalPageNumber = totalPageNumber)
     }

     fun removeAllSplitPoints()
     {
          splitPointList.clear()
     }

     fun removeSelectedPdf(value:PdfData)
     {
          totalPageNumber = 0
          selectedPdf.value = PdfData("", "" , null, null, 0)
          removeAllSplitPoints()
          pdfReader = null
     }
}