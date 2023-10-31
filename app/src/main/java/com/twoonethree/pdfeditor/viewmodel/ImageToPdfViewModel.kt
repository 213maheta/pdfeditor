package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class ImageToPdfViewModel:ViewModel() {

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    var uriList = mutableStateListOf<Uri>()

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

    fun imageToPdf(resolver: ContentResolver)
    {
        uriList.let {
            if(it.isEmpty())
            {
                setUiIntent(ScreenCommonEvents.ShowToast("Select atleast one image"))
                return
            }
            PdfUtilities.imageToPdf(
                resolver = resolver,
                uriList = it,
                ::setUiIntent
            )
        }
    }

}