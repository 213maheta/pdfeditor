package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PasswordDialogViewModel:ViewModel() {

    companion object{
        var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))
        val isVisible = mutableStateOf(false)
    }

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val password = mutableStateOf("")

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }
    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null, null, 0)
    }

    fun getPasswordProtectedPDFReader(
        resolver: ContentResolver,
        callBack: (ScreenCommonEvents) -> Unit
    ) = viewModelScope.launch {
        /*val pdfReader = PdfUtilities.getPasswordProtectedPDFReader(
                resolver = resolver,
                uri = selectedPdf.value.uri!!,
                password = password.value,
                callBack = callBack
            )
        pdfReader?.let {
            callBack(ScreenCommonEvents.GotProtectedPdf(it))
        }*/
    }

    fun checkPassword(
        contentResolver: ContentResolver,
        ) {
        selectedPdf.value.uri?.let {
            PdfUtilities.checkPdfPassword(
                contentResolver,
                uri = it,
                password = password.value,
                ::setUiIntent
            )
        }
    }
}