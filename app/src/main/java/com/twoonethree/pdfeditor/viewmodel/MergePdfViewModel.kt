package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class MergePdfViewModel() : ViewModel() {

    val pdfList = mutableStateListOf<PdfData>()
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun saveMergedPdf(contentResolver: ContentResolver) {
        when (pdfList.toList().size) {
            0,1 -> {
                val message = "Select at least two file"
                setUiIntent(ScreenCommonEvents.ShowToast(message))
                return
            }
            else -> {
                val isSuccess = PdfUtilities.mergePdf(
                    contentResolver,
                    pdfList.toList(),
                    FileManager.createPdfFile()
                )
                val message = if (isSuccess) "Merged successfully" else "Something went wrong"
                setUiIntent(ScreenCommonEvents.ShowToast(message))
            }
        }
    }

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removePdf(value: PdfData)
    {
        pdfList.remove(value)
    }

}