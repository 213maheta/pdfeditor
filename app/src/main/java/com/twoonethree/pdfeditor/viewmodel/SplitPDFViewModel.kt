package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplitPDFViewModel() : ViewModel() {

     val splitPointList = mutableStateListOf<Int>()
     var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))

     val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

     val showProgressBar = mutableStateOf(false)


     fun setUiIntent(value: ScreenCommonEvents) {
          uiIntent.value = value
     }

     fun saveSplitPdf(contentResolver: ContentResolver) = viewModelScope.launch(Dispatchers.IO)
     {
          when{
               splitPointList.size<1 -> setUiIntent(ScreenCommonEvents.ShowToast("Add at least one split point"))
               else ->{
                    selectedPdf.value.uri?.let {

                         setUiIntent(ScreenCommonEvents.ShowProgressBar(true))

                         val isSuccess = PdfUtilities.splitPdf(
                              contentResolver,
                              it,
                              FileManager.getSplitFilePath(),
                              splitPointList.toList().sortedBy { it },
                              selectedPdf.value.password)

                         when(isSuccess)
                         {
                              true -> setUiIntent(ScreenCommonEvents.ShowToast("Pdf splitted successfully"))
                              false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
                         }

                         setUiIntent(ScreenCommonEvents.ShowProgressBar(false))
                    }
               }
          }
     }

     fun addSplitPoints(value:String)
     {
          if(value.isEmpty())
               return
          if(selectedPdf.value.totalPageNumber == 0)
          {
               setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
               return
          }
          val splitPoint = value.toInt()

          when{
               splitPoint > selectedPdf.value.totalPageNumber -> setUiIntent(ScreenCommonEvents.ShowToast("Split point should be less than total page count"))
               splitPoint < 1 -> setUiIntent(ScreenCommonEvents.ShowToast("Split point be should more than 0"))
               splitPointList.contains(splitPoint) -> setUiIntent(ScreenCommonEvents.ShowToast("Already added"))
               else -> splitPointList.add(splitPoint)
          }
     }

     fun removeSplitPoints(value:Int)
     {
          splitPointList.remove(value)
     }


     fun removeAllSplitPoints()
     {
          splitPointList.clear()
     }

     fun removeSelectedPdf(value:PdfData)
     {
          selectedPdf.value = PdfData("", "" , null,  0)
          removeAllSplitPoints()
     }
}