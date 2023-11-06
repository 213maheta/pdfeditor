package com.twoonethree.pdfeditor.screencompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel
import com.twoonethree.pdfeditor.viewmodel.LockPdfViewModel

@Composable
fun LockPdfScreen(navController: NavController)
{
    val vm = viewModel<LockPdfViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()
    val vmDialog = viewModel<DialogViewModel>()


    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    val pickPdfDocument = pdfLauncherOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
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
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                vm.selectedPdf.value.let { pdfData ->
                    pdfData.uri?.let {
                        ItemPDF(pdfData, vm::removeSelectedPdf)
                        PasswordTextEdit(vm.password.value) { value: String ->
                            vm.password.value = value
                        }
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.lock_pdf,
        backClick = { navController.navigateUp() },
        doneClick = { vm.setPassword(contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )

    when{
        vmDialog.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }

    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar()
    }

    ShowSnackBar()
}

