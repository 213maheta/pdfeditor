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

    fun setPassword(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
            if(password.value.isEmpty())
            {
                setUiIntent(ScreenCommonEvents.ShowToast("Password is blank"))
                return@launch
            }
            setUiIntent(ScreenCommonEvents.ShowProgressBar(true))
            val isSuccess = PdfUtilities.setPassword(
                resolver = resolver,
                uri = it,
                password.value,
                selectedPdf.value.password)

            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowToast("Password added successfully"))
                false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
            }
            setUiIntent(ScreenCommonEvents.ShowProgressBar(false))
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
        }
    }



}