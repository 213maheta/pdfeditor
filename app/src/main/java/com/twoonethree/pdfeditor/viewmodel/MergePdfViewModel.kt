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

class MergePdfViewModel() : ViewModel() {

    val pdfList = mutableStateListOf<PdfData>()
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    var lockedIndex = -1

    val showProgressBar = mutableStateOf(false)
    val showProgressValue = mutableStateOf(0f)

    fun saveMergedPdf(contentResolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default){
        when (pdfList.toList().size) {
            0,1 -> {
                val message = "Select at least two file"
                setUiIntent(ScreenCommonEvents.ShowToast(message))
                return@launch
            }
            else -> {
                if(checkAllUnlocked())
                {
                    setUiIntent(ScreenCommonEvents.ShowProgressBar(true))

                    val isSuccess = PdfUtilities.mergePdf(
                        contentResolver,
                        pdfList.toList(),
                        FileManager.createPdfFile()
                    ) { progress: Float -> showProgressValue.value = progress }
                    when(isSuccess)
                    {
                        true -> setUiIntent(ScreenCommonEvents.ShowToast("Merged successfully"))
                        false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
                    }
                    setUiIntent(ScreenCommonEvents.ShowProgressBar(false))
                }
            }
        }
    }

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun checkAllUnlocked():Boolean
    {
        pdfList.forEachIndexed{ index, pdfData ->
            if(pdfData.totalPageNumber == 0)
            {
                lockedIndex = index
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return false
            }
        }
        return true
    }

    fun removePdf(value: PdfData)
    {
        pdfList.remove(value)
    }

}