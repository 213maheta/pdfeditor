package com.twoonethree.pdfeditor.mycreation

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.FileUtilities
import com.twoonethree.pdfeditor.utilities.PdfUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MyCreationViewModel:ViewModel() {

    val pdfList = mutableStateListOf<PdfData>()
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    var showBottomSheet = mutableStateOf(false)
    var selectedPdf = mutableStateOf(PdfData("", "" , null,  0))
    val showProgressBar = mutableStateOf(false)

    fun getAllPdf(contentResolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        showProgressBar.value = true

        val data = FileUtilities.getAllPdf(contentResolver)
        pdfList.clear()
        pdfList.addAll(data)
        showProgressBar.value = false
    }

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun rename(newName:String) = viewModelScope.launch(Dispatchers.Default)
    {
        if(selectedPdf.value.uri == null)
            return@launch
        val isSuccess = FileManager.renameFile(selectedPdf.value.uri!!, newName)
        when(isSuccess)
        {
            true -> {
                pdfList.firstOrNull {
                    selectedPdf.value.name == it.name
                }?.also {
                    val newUri = selectedPdf.value.uri.toString().replace(selectedPdf.value.name, newName)
                    val newPdfData = it.copy(name = newName, uri = newUri.toUri())
                    val index = pdfList.indexOf(selectedPdf.value)
                    pdfList.set(index, newPdfData)
                    setUiIntent(ScreenCommonEvents.ShowSnackBar("File renamed successfully", Green))
                    showBottomSheet.value = false
                }
            }
            false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
        }
    }

    fun validateSameName(newName:String): Boolean {
        val sameNamePdf = pdfList.firstOrNull {
            it.name == newName
        }
        return sameNamePdf == null
    }

    fun delete()
    {
        selectedPdf.value.uri?.let {
            val isSuccess = FileManager.deleteFile(it)
            when(isSuccess)
            {
                true->{
                    pdfList.remove(selectedPdf.value)
                    setUiIntent(ScreenCommonEvents.ShowSnackBar("File deleted successfully", Green))
                    showBottomSheet.value = false
                }
                else->setUiIntent(ScreenCommonEvents.ShowSnackBar("Something went wrong", Orange))
            }
        }
    }

    suspend fun getThumbNail(contentResolver: ContentResolver, uri: Uri): File? = withContext(Dispatchers.IO)
    {
        return@withContext PdfUtilities.cachedThumbnail(contentResolver, uri)
    }
    fun shareIntent(shareUri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, shareUri)
        }
    }
}