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

    fun setPassword(resolver: ContentResolver, uri: Uri?, pdfReader: PdfReader?)
    {
        uri?.let {
            if(selectedPdf.value.totalPageNumber == 0 && pdfReader == null)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
            PdfUtilities.setPassword(
                resolver = resolver,
                uri = it,
                password.value,
                pdfReader,
                ::setUiIntent
                )
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }



}