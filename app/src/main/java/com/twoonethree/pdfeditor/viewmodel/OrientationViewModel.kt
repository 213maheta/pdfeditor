package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class OrientationViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))
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

    fun changeOrientation(resolver: ContentResolver, uri: Uri?)
    {
        uri?.let {

            if(currentOrientation.value == previousOrientation)
            {
                setUiIntent(ScreenCommonEvents.ShowToast("Current orientation is same as selected"))
                return
            }
            PdfUtilities.changeOrientation(resolver, it, ::setUiIntent, currentOrientation.value) {
                previousOrientation = currentOrientation.value
            }
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