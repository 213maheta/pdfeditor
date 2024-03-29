package com.twoonethree.pdfeditor.screencompose

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.viewmodel.AddWaterMarkViewModel
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel

@Composable
fun AddWaterMarkScreen(navController:NavController)
{
    val vm = viewModel<AddWaterMarkViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()
    val vmDialog = viewModel<DialogViewModel>()

    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver


    val pickPdfDocument = pdfPickerOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        true
    }

    val pickImage = imagePicker{ pdf ->
        vm.waterMarkUri.value = pdf
        true
    }

    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowSnackBar -> {
                    vmCommon.message.value = it.value
                    vmCommon.status = it.color
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.ShowPasswordDialog -> {
                    vmDialog.selectedPdf.value = vm.selectedPdf.value
                    vmDialog.isPasswordDialogueVisible.value = true
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
                    vm.selectedPdf.value.totalPageNumber = it.totalPageNumber
                    vm.selectedPdf.value.password = it.password
                    vmDialog.isPasswordDialogueVisible.value = false
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
                vm.selectedPdf.value.let { pdfData ->
                    pdfData.uri?.let {
                        ItemPDF(pdfData, vm::removeSelectedPdf)
                        Box(modifier = Modifier.fillMaxWidth())
                        {
                            Button(
                                onClick = { pickImage.launch("image/*")},
                                modifier = Modifier.align(Alignment.Center),
                                colors = ButtonDefaults.buttonColors(containerColor = Orange)
                            )
                            {
                                Text(text = "Pick Image")
                            }
                        }
                        vm.waterMarkUri.value?.let {
                            WaterMarkPreview(value = it)
                        }
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.add_water_mark,
        backClick = { navController.navigateUp() },
        doneClick = { vm.addWaterMark(resolver = contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )

    when{
        vmDialog.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }

    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar(vm.showProgressValue.value)
    }

    ShowSnackBar()
}

@Composable
fun WaterMarkPreview(
    value: Uri,
)
{
    Column {
        AsyncImage(
            model = value,
            contentDescription = "Selected Image",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp, top = 4.dp)
                .border(width = 1.dp, color = colorResource(id = R.color.black))

        )
    }
}