package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
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
                setUiIntent(ScreenCommonEvents.ShowSnackBar("This file is not password protected", Blue))
                return
            }
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }

    fun removePassword(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        selectedPdf.value.uri?.let {
            val isSuccess = PdfUtilities.removePassword(
                resolver = resolver,
                uri = it,
                FileManager.createPdfFile(),
                selectedPdf.value.password,
            )
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Password removed successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
        }
    }
}