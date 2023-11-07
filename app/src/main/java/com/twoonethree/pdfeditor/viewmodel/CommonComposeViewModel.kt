package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.CachedManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CommonComposeViewModel: ViewModel() {

    val message = mutableStateOf("")
    var status = Green
    suspend fun getThumbNail(contentResolver: ContentResolver, uri: Uri): File? = withContext(Dispatchers.IO)
    {
        return@withContext PdfUtilities.cachedThumbnail(contentResolver, uri)
    }
}