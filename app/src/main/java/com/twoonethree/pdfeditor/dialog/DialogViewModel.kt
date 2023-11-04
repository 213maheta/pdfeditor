package com.twoonethree.pdfeditor.dialog

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DialogViewModel : ViewModel() {

    //Password Dialogue
    companion object {
        var selectedPdf = mutableStateOf(PdfData("", "", null, 0))
        val isPasswordDialogueVisible = mutableStateOf(false)
        var selectedIndex = -1
        val isDeleteDialogVisible = mutableStateOf(false)
        val isRenameDialogVisible = mutableStateOf(false)
    }

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val password = mutableStateOf("")

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value: PdfData) {
        selectedPdf.value = PdfData("", "", null, 0)
    }

    fun checkPassword(contentResolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        selectedPdf.value.uri?.let {
            val value = PdfUtilities.checkPdfPassword(
                contentResolver,
                uri = it,
                password = password.value,
            )

            when {
                value > 0 -> {
                    setUiIntent(
                        ScreenCommonEvents.GotPassword(
                            totalPageNumber = value,
                            password = password.value
                        )
                    )
                }
                value == 0 -> setUiIntent(ScreenCommonEvents.ShowToast("Password is incorrect"))
                value == -1 -> setUiIntent(ScreenCommonEvents.ShowToast("Something went wrong"))
            }
        }
    }

    //Rename Dialogue
    val newName = mutableStateOf("")


}