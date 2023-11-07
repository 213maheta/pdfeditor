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

class RotatePdfViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0, null, null))
    val currentOrientation = mutableStateOf(0)
    var previousOrientation = -1
    val showProgressBar = mutableStateOf(false)
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val showProgressValue = mutableStateOf(0f)


    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value: PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun changeOrientation(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
       selectedPdf.value.uri?.let {
            if(currentOrientation.value == previousOrientation)
            {
                setUiIntent(ScreenCommonEvents.ShowSnackBar("Current orientation is same as selected", Blue))
                return@launch
            }
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
           showProgressBar.value = true
           val isSuccess = PdfUtilities.changeOrientation(resolver = resolver,
                uri = it,
                FileManager.createPdfFile(),
                value = currentOrientation.value,
                password = selectedPdf.value.password
            ){ progress: Float -> showProgressValue.value = progress }
           when(isSuccess)
           {
               true -> {
                   previousOrientation = currentOrientation.value
                   setUiIntent(ScreenCommonEvents.ShowSnackBar("Orientation changed successfully", Green))
               }
               false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
           }
           showProgressBar.value = false
       }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }



    fun getOrientation(resolver: ContentResolver, uri: Uri?) = viewModelScope.launch(Dispatchers.Default) {
        uri?.let {
            currentOrientation.value =  PdfUtilities.getOrientation(resolver, it)
            previousOrientation = currentOrientation.value
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }


}