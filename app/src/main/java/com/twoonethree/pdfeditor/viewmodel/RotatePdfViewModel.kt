package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RotatePdfViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0, null, null))
    val currentOrientation = mutableStateOf(0)
    var previousOrientation = -1
    val showProgressBar = mutableStateOf(false)
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

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
                setUiIntent(ScreenCommonEvents.ShowToast("Current orientation is same as selected"))
                return@launch
            }
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
           setUiIntent(ScreenCommonEvents.ShowProgressBar(true))
           val isSuccess = PdfUtilities.changeOrientation(resolver = resolver,
                uri = it,
                value = currentOrientation.value,
                password = selectedPdf.value.password
            )
           when(isSuccess)
           {
               true -> {
                   previousOrientation = currentOrientation.value
                   setUiIntent(ScreenCommonEvents.ShowToast("Orientation changed successfully"))
               }
               false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
           }
           setUiIntent(ScreenCommonEvents.ShowProgressBar(false))
       }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }



    fun getOrientation(resolver: ContentResolver, uri: Uri?) = viewModelScope.launch(Dispatchers.Default) {
        uri?.let {
            currentOrientation.value =  PdfUtilities.getOrientation(resolver, it)
            previousOrientation = currentOrientation.value
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }


}