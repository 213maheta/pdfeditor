package com.twoonethree.pdfeditor.dialog

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class DialogViewModel:ViewModel() {

    //Password Dialogue
    companion object{
        var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))
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
    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun checkPassword(
        contentResolver: ContentResolver,
        callback: (ScreenCommonEvents) -> Unit,
        ) {
        selectedPdf.value.uri?.let {
            PdfUtilities.checkPdfPassword(
                contentResolver,
                uri = it,
                password = password.value,
                callback
            )
        }
    }

    //Rename Dialogue

}