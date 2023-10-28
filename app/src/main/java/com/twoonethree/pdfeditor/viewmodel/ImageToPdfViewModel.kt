package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class ImageToPdfViewModel:ViewModel() {

    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)

    var selectedImageUri = mutableStateOf<Uri?>("".toUri())

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun imageToPdf(resolver: ContentResolver)
    {
        selectedImageUri.value?.let {
            PdfUtilities.imageToPdf(
                resolver = resolver,
                uri = it,
                ::setUiIntent
            )
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select image first"))
        }
    }

}