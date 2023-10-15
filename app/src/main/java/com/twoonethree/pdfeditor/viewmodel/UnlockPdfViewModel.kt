package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.itextpdf.kernel.pdf.PdfReader
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class UnlockPdfViewModel:ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, null, 0))

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value: PdfData)
    {
        selectedPdf.value = PdfData("", "" , null, null, 0)
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

    fun removePassword(resolver: ContentResolver)
    {
        selectedPdf.value.uri?.let {
            PdfUtilities.removePassword(
                resolver = resolver,
                uri = it,
                selectedPdf.value.password,
                ::setUiIntent
            )
        }
    }
}