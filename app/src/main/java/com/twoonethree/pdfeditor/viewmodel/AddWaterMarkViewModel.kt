package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddWaterMarkViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))

    val showProgressBar = mutableStateOf(false)
    val showProgressValue = mutableStateOf(0f)

    val waterMarkUri = mutableStateOf<Uri?>(null)

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,0)
    }

    fun addWaterMark(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.IO)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }

            if(waterMarkUri.value == null)
            {
                setUiIntent(ScreenCommonEvents.ShowSnackBar("Select Watermark", Blue))
                return@launch
            }
            showProgressBar.value = true
            val isSuccess = PdfUtilities.addWaterMark(
                resolver = resolver,
                uri = selectedPdf.value.uri!!,
                FileManager.createPdfFile(),
                password = selectedPdf.value.password,
                imageUri = waterMarkUri.value!!
            ){ progress: Float -> showProgressValue.value = progress }

            showProgressBar.value = false
            showProgressValue.value = 0f

            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Watermark added successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }
}