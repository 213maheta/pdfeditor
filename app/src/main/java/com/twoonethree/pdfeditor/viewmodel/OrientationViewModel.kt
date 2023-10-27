package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class OrientationViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0, null, null))
    val currentOrientation = mutableStateOf(0)
    var previousOrientation = 0


    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value: PdfData)
    {
        selectedPdf.value = PdfData("", "" , null, null, 0)
    }

    fun changeOrientation(resolver: ContentResolver)
    {
       selectedPdf.value.uri?.let {
            if(currentOrientation.value == previousOrientation)
            {
                setUiIntent(ScreenCommonEvents.ShowToast("Current orientation is same as selected"))
                return
            }
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
            PdfUtilities.changeOrientation(resolver = resolver,
                uri = it,
                callBack = ::setUiIntent,
                value = currentOrientation.value,
                function = { previousOrientation = currentOrientation.value },
                password = selectedPdf.value.password
            )
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }



    fun getOrientation(resolver: ContentResolver, uri: Uri?) {
        uri?.let {
            currentOrientation.value =  PdfUtilities.getOrientation(resolver, it)
            previousOrientation = currentOrientation.value
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }


}