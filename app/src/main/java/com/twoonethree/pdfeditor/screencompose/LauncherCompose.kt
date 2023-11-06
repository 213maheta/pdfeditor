package com.twoonethree.pdfeditor.screencompose

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun pdfPicker(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<String, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                scope.launch(Dispatchers.Default) {
                    val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                    onSuccess(pdf)
                }
            }
        }
    )
}

@Composable
fun pdfPickerOpenDocument(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                scope.launch(Dispatchers.Default) {
                    val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                    onSuccess(pdf)
                }
            }
        }
    )
}

@Composable
fun pdfPickerMulti(onSuccess: (List<PdfData>) -> Boolean): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    val contentResolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uriList ->
            scope.launch(Dispatchers.Default) {
                uriList.let {
                    val pdfDataList = mutableListOf<PdfData>()
                    it.forEach {
                        val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                        pdfDataList.add(pdf)
                    }
                    onSuccess(pdfDataList)
                }
            }
        }
    )
}


@Composable
fun imageMultiPicker(onSuccess: (List<Uri>) -> Unit): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = {
            onSuccess(it)
        }
    )
}

@Composable
fun imagePicker(onSuccess: (Uri) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                onSuccess(it)
            }
        }
    )
}
