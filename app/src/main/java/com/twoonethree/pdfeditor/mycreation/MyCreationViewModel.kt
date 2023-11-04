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
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.utilities.CachedManager
import com.twoonethree.pdfeditor.utilities.FileManager
import com.twoonethree.pdfeditor.utilities.FileUtilities
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
    fun getAllPdf(contentResolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
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

    fun rename(newName:String) = viewModelScope.launch(Dispatchers.Default)
    {
        if(selectedPdf.value.uri == null)
            return@launch
        if(newName.isEmpty())
        {
            setUiIntent(ScreenCommonEvents.ShowToast("Give proper name"))
            return@launch
        }
        val sameNamePdf = pdfList.firstOrNull {
            it.name == newName
        }
        if(sameNamePdf != null)
        {
            setUiIntent(ScreenCommonEvents.ShowToast("This name already taken"))
            return@launch
        }
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
                    setUiIntent(ScreenCommonEvents.ShowToast("File renamed successfully"))
                    showBottomSheet.value = false
                }
            }
            false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
        }
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

    suspend fun getThumbNail(contentResolver: ContentResolver, uri: Uri): File? = withContext(Dispatchers.Default)
    {
        val value = CachedManager.isFileExist(uri)
        if(value == null)
        {
            return@withContext PdfUtilities.cachedThumbnail(contentResolver, uri)
        }
        return@withContext value
    }
    fun shareIntent(shareUri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, shareUri)
        }
    }
}