package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ImageToPdfViewModel:ViewModel() {

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    var uriList = mutableStateListOf<Uri>()

    val showProgressBar = mutableStateOf(false)


    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removePage(value:Uri)
    {
        uriList.remove(value)
    }

    fun changePosition(index1: Int, index2: Int) {
        if (index2 >= 0 && index2 < uriList.size) {

            val temp = uriList[index1]
            uriList[index1] = uriList[index2]
            uriList[index2] = temp
        }
    }

    fun imageToPdf(resolver: ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        uriList.let {
            if(it.isEmpty())
            {
                setUiIntent(ScreenCommonEvents.ShowSnackBar("Select atleast one image", Blue))
                return@launch
            }
            showProgressBar.value = true
            val isSuccess =PdfUtilities.imageToPdf(
                resolver = resolver,
                uriList = it,
                FileManager.createPdfFile()
            )
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Image to pdf conversion successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
            showProgressBar.value = false
        }
    }

}