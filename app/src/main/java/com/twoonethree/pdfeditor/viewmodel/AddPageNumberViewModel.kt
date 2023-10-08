package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.AddPageNumberSelection
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class AddPageNumberViewModel:ViewModel() {

    val splitPointList = mutableStateListOf<Int>()
    var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))
    var totalPageNumber = 0

    val selectedCorner = mutableStateOf<AddPageNumberSelection>(AddPageNumberSelection.EMPTY)

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null, null, 0)
    }

    fun addPageNumber(resolver: ContentResolver, uri: Uri?)
    {
        uri?.let {
            PdfUtilities.addPageNumber(resolver, it, ::setUiIntent, ::getXYposition)
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }

    fun getXYposition(x:Float, y:Float): Pair<Float, Float> {
        when(selectedCorner.value)
        {
            is AddPageNumberSelection.TOP_LEFT -> return Pair(40f,y-30f)
            is AddPageNumberSelection.TOP_RIGHT -> return Pair(x-40,y-30f)
            is AddPageNumberSelection.BOTTOM_LEFT -> return Pair(40f,30f)
            is AddPageNumberSelection.BOTTOM_RIGHT -> return Pair(x-40,30f)
            is AddPageNumberSelection.EMPTY -> return Pair(x,y)
        }
    }
}