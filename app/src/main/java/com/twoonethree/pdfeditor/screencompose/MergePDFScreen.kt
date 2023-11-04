package com.twoonethree.pdfeditor.screencompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.viewmodel.MergePdfViewModel
import com.twoonethree.pdfeditor.dialog.DialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergePDFScreen(navController: NavHostController) {

    val vm = viewModel<MergePdfViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.ShowPasswordDialog -> {
                    DialogViewModel.selectedPdf.value = vm.pdfList[vm.lockedIndex]
                    DialogViewModel.isPasswordDialogueVisible.value = true
                    DialogViewModel.selectedIndex = vm.lockedIndex
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
                    vm.lockedIndex = DialogViewModel.selectedIndex
                    vm.pdfList[vm.lockedIndex].totalPageNumber = it.totalPageNumber
                    vm.pdfList[vm.lockedIndex].password = it.password
                    DialogViewModel.isPasswordDialogueVisible.value = false
                }
                is ScreenCommonEvents.ShowProgressBar -> {
                    vm.showProgressBar.value = it.value
                }
                else -> {}
            }
        }
    }

    val pickPdfDocument = pdfLauncherMulti {pdfList: List<PdfData> -> vm.pdfList.addAll(pdfList) }

    val innerContent: @Composable (paddingvalues:PaddingValues) -> Unit = { paddingvalues:PaddingValues->
        Box(
            modifier = Modifier
                .padding(paddingvalues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            ReOrderColumnList(vm.pdfList.toList())
        }
    }

    MyTopAppBar(
        titleId = R.string.pdf_merge,
        backClick = { navController.navigateUp() },
        doneClick = { vm.saveMergedPdf(contentResolver = contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(context.getString(R.string.application_pdf)) },
        innerContent = innerContent,
    )

    when{
        DialogViewModel.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }

    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar(vm.showProgressValue.value)
    }
}

@Composable
fun ReOrderColumnList(
    pdfList: List<PdfData>,
) {
    val vm = viewModel<MergePdfViewModel>()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(pdfList) { index, pdf ->
            ItemPDF(pdf = pdf, vm::removePdf, index)
        }
    }
}
