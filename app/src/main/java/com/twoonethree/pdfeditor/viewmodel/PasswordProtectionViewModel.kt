package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class PasswordProtectionViewModel: ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val password = mutableStateOf("")

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null, null, 0)
    }

    fun setPassword(resolver: ContentResolver)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
            if(password.value.isEmpty())
            {
                setUiIntent(ScreenCommonEvents.ShowToast("Password is blank"))
                return
            }
            PdfUtilities.setPassword(
                resolver = resolver,
                uri = it,
                password.value,
                selectedPdf.value.password,
                ::setUiIntent
                )
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }



}