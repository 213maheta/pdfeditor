package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UnlockPdfViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value: PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun checkPassword(resolver: ContentResolver)
    {
        selectedPdf.value.uri?.let {

            if(selectedPdf.value.totalPageNumber > 0)
            {
                setUiIntent(ScreenCommonEvents.ShowToast("This file is not password protected"))
                return
            }
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }

    fun removePassword(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        selectedPdf.value.uri?.let {
            val isSuccess = PdfUtilities.removePassword(
                resolver = resolver,
                uri = it,
                selectedPdf.value.password,
            )
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowToast("Password removed successfully"))
                false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
            }
        }
    }
}