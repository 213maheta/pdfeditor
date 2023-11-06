package com.twoonethree.pdfeditor.dialog

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DialogViewModel : ViewModel() {

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    var selectedPdf = mutableStateOf(PdfData("", "", null, 0))

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    //Password Dialogue
    val isPasswordDialogueVisible = mutableStateOf(false)
    var selectedIndex = -1

    val password = mutableStateOf("")
    val errorText = mutableStateOf("")
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
                value == 0 -> errorText.value = "Password is incorrect"
                value == -1 -> errorText.value = "Something went wrong"
            }
        }
    }

    //Delete Dialogue
    val isDeleteDialogVisible = mutableStateOf(false)

    //Rename Dialogue
    val isRenameDialogVisible = mutableStateOf(false)
    val newName = mutableStateOf("")
    val warningText = mutableStateOf("")
    val notAllowedCharacterList = listOf("/", "?", "%", "'\'", ".", " ", )
    fun validateRename(): Boolean {
        if(newName.value.isEmpty())
        {
            warningText.value = "Empty field not allowed"
            return false
        }

        notAllowedCharacterList.forEach{
            if(newName.value.contains(it))
            {
                warningText.value = "Only alphabet, number and underscore allowed"
                return false
            }
        }
        return true
    }



}