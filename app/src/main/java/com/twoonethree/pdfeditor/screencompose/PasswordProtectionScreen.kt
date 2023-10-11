package com.twoonethree.pdfeditor.screencompose

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
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel
import com.twoonethree.pdfeditor.viewmodel.PasswordProtectionViewModel

@Composable
fun PasswordProtectionScreen(navController: NavController)
{
    val vm = viewModel<PasswordProtectionViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    val pickPdfDocument = pdfLauncherOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        true
    }

    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.ShowPasswordDialog ->{
                    PasswordDialogViewModel.selectedPdf.value = vm.selectedPdf.value
                    PasswordDialogViewModel.isVisible.value = true
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotProtectedPdf ->{
                    vm.setPassword(contentResolver, vm.selectedPdf.value.uri, it.pdfReaderOuter)
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
                        GetPassword(vm.password.value) { value: String ->
                            vm.password.value = value
                        }
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.password_protection,
        backClick = { navController.navigateUp() },
        doneClick = { vm.setPassword(contentResolver, vm.selectedPdf.value.uri, null) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )

    when{
        PasswordDialogViewModel.isVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }
}

