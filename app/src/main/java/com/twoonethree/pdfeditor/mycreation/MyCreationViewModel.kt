package com.twoonethree.pdfeditor.mycreation

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.FileUtilities
import kotlinx.coroutines.flow.MutableStateFlow


class MyCreationViewModel:ViewModel() {

    val pdfList = mutableStateListOf<PdfData>()
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    var showBottomSheet = mutableStateOf(false)
    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))
    var renameValue = mutableStateOf("")
    fun getAllPdf(contentResolver: ContentResolver)
    {
        val addAllPdf:(pdfList:List<PdfData>) -> Unit = {
            pdfList.clear()
            pdfList.addAll(it)
        }
        FileUtilities.getAllPdf(contentResolver, addAllPdf)
    }

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun rename()
    {
        if(selectedPdf.value.uri == null)
            return
        if(renameValue.value.isEmpty())
        {
            setUiIntent(ScreenCommonEvents.ShowToast("Give proper name"))
            return
        }
        val sameNamePdf = pdfList.firstOrNull {
            it.name == renameValue.value
        }
        if(sameNamePdf == null)
        {
            setUiIntent(ScreenCommonEvents.ShowToast("This name already taken"))
            return
        }
        FileManager.renameFile(selectedPdf.value.uri!!, renameValue.value)
    }

    fun delete()
    {
        selectedPdf.value.uri?.let {
            val isSuccess = FileManager.deleteFile(it)
            if(isSuccess)
            {
                pdfList.remove(selectedPdf.value)
                setUiIntent(ScreenCommonEvents.ShowToast("File deleted successfully"))
                showBottomSheet.value = false
            }
        }
    }
    fun shareIntent(shareUri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, shareUri)
        }
    }
}