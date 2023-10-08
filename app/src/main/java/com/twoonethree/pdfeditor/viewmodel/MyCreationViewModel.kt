package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class MyCreationViewModel:ViewModel() {

    val pdfList = mutableStateListOf<PdfData>()
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun getAllPdf(contentResolver: ContentResolver)
    {
        val addAllPdf:(pdfList:List<PdfData>) -> Unit = {
            pdfList.clear()
            pdfList.addAll(it)
        }
        FileUtilities.getAllPdf(contentResolver, addAllPdf)
    }

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }
}