package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LockPdfViewModel: ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val password = mutableStateOf("")
    val showProgressBar = mutableStateOf(false)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun setPassword(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.IO)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
            if(password.value.isEmpty())
            {
                setUiIntent(ScreenCommonEvents.ShowSnackBar("Password is blank", Blue))
                return@launch
            }
            showProgressBar.value = true
            val isSuccess = PdfUtilities.setPassword(
                resolver = resolver,
                uri = it,
                FileManager.createPdfFile(),
                password.value,
                selectedPdf.value.password)

            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Password added successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
            showProgressBar.value = false
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }
    }



}