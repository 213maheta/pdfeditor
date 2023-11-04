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
                setUiIntent(ScreenCommonEvents.ShowToast("Select atleast one image"))
                return@launch
            }
            setUiIntent(ScreenCommonEvents.ShowProgressBar(true))
            val isSuccess =PdfUtilities.imageToPdf(
                resolver = resolver,
                uriList = it,
            )
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowToast("Image to pdf conversion successfully\""))
                false -> setUiIntent(ScreenCommonEvents.ShowToast("Something gone wrong"))
            }
            setUiIntent(ScreenCommonEvents.ShowProgressBar(false))
        }
    }

}