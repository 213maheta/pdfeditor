package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.twoonethree.pdfeditor.events.AddPageNumberSelection
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddPageNumberViewModel:ViewModel() {

    val splitPointList = mutableStateListOf<Int>()
    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))
    var totalPageNumber = 0

    val showProgressBar = mutableStateOf(false)
    val showProgressValue = mutableStateOf(0f)

    val selectedCorner = mutableStateOf<AddPageNumberSelection>(AddPageNumberSelection.BOTTOM_RIGHT)

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,0)
    }

    fun addPageNumber(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.IO)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
            showProgressBar.value = true
            val isSuccess = PdfUtilities.addPageNumber(resolver, it, FileManager.createPdfFile(),selectedPdf.value.password,::getXYposition)
            { progress: Float -> showProgressValue.value = progress }
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Page number added successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
            showProgressBar.value = false
            showProgressValue.value = 0f
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }

    fun getXYposition(pageSize: Rectangle): Pair<Float, Float> {
        Log.e("TAG", "getXYposition: ${pageSize.left} ${pageSize.right} ${pageSize.top} ${pageSize.bottom} ${pageSize.x} ${pageSize.y}", )
        when(selectedCorner.value)
        {
            is AddPageNumberSelection.TOP_LEFT -> return Pair(pageSize.left + 40, pageSize.top - 30)
            is AddPageNumberSelection.TOP_RIGHT -> return Pair(pageSize.right - 42, pageSize.top - 30)
            is AddPageNumberSelection.BOTTOM_LEFT -> return Pair(pageSize.left + 40, pageSize.bottom + 30)
            is AddPageNumberSelection.BOTTOM_RIGHT -> return Pair(pageSize.right - 40, pageSize.bottom + 30)
            is AddPageNumberSelection.EMPTY -> return Pair(pageSize.x, pageSize.y)
        }
    }
}