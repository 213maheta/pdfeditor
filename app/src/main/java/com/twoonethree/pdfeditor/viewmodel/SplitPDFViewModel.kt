package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.PdfUtilities
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
               splitPointList.size<1 -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Add at least one split point", Blue))
               else ->{
                    selectedPdf.value.uri?.let {

                         showProgressBar.value = true

                         val isSuccess = PdfUtilities.splitPdf(
                              contentResolver,
                              it,
                              FileManager.getSplitFilePath(),
                              splitPointList.toList().sortedBy { it },
                              selectedPdf.value.password)

                         when(isSuccess)
                         {
                              true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Pdf splitted successfully", Green))
                              false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
                         }

                         showProgressBar.value = false
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
               splitPoint > selectedPdf.value.totalPageNumber -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Split point should be less than total page count", Blue))
               splitPoint < 1 -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Split point be should more than 0", Blue))
               splitPointList.contains(splitPoint) -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Already added", Blue))
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