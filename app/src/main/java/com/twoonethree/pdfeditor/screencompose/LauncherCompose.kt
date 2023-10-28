package com.twoonethree.pdfeditor.screencompose

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileUtilities

@Composable
fun pdfLauncher(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<String, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                onSuccess(pdf)
            }
        }
    )
}

@Composable
fun pdfLauncherOpenDocument(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                onSuccess(pdf)
            }
        }
    )
}

@Composable
fun pdfLauncherMulti(onSuccess: (List<PdfData>) -> Boolean): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    val contentResolver = LocalContext.current.contentResolver
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uriList ->
            uriList.let {
                val pdfDataList = mutableListOf<PdfData>()
                it.forEach {
                    val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                    pdfDataList.add(pdf)
                }
                onSuccess(pdfDataList)
            }
        }
    )
}


@Composable
fun ImageLauncher(onSuccess: (Uri?) -> Unit): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            onSuccess(it)
        }
    )
}
