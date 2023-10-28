package com.twoonethree.pdfeditor.screencompose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.ImageToPdfViewModel
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel

@Composable
fun ImageToPdfScreen(navController: NavController)
{
    val vm = viewModel<ImageToPdfViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    val imagePicker = ImageLauncher { uri ->
        vm.selectedImageUri.value = uri
    }

    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.ShowPasswordDialog -> {
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
                    PasswordDialogViewModel.isVisible.value = false
                }
                else -> {}
            }
        }
    }

    val innerContent: @Composable (paddingValues: PaddingValues) -> Unit =
        { paddingValues: PaddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                vm.selectedImageUri.value.let {
                    it?.let {
                        ImagePreview(uri = it)
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.image_to_pdf,
        backClick = { navController.navigateUp() },
        doneClick = { vm.imageToPdf(resolver = contentResolver) },
        floatBtnClick = { imagePicker.launch("image/*") },
        innerContent = innerContent,
    )

    when{
        PasswordDialogViewModel.isVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }
}

@Composable
fun ImagePreview(uri: Uri)
{
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
        .border(width = 1.dp, color = colorResource(id = R.color.orange))
    ){
        Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = "Selected Image",
            modifier = Modifier
                .fillMaxSize()
                .border(width = 1.dp, color = colorResource(id = R.color.black))
            )
    }
}