package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.AddPageNumberSelection
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.AddPageNumberViewModel
import com.twoonethree.pdfeditor.dialog.DialogViewModel


@Composable
fun AddPageNumberScreen(navController: NavController)
{
    val vm = viewModel<AddPageNumberViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onCornerClick = {value:AddPageNumberSelection -> vm.selectedCorner.value = value}
    val isSelected :(AddPageNumberSelection) -> Boolean= {value:AddPageNumberSelection -> vm.selectedCorner.value == value}

    val pickPdfDocument = pdfLauncherOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        vm.totalPageNumber = pdf.totalPageNumber
        vm.splitPointList.clear()
        true
    }

    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.ShowPasswordDialog -> {
                    DialogViewModel.selectedPdf.value = vm.selectedPdf.value
                    DialogViewModel.isPasswordDialogueVisible.value = true
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
                    vm.selectedPdf.value.totalPageNumber = it.totalPageNumber
                    vm.selectedPdf.value.password = it.password
                    DialogViewModel.isPasswordDialogueVisible.value = false
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
                        CornerSelection(onCornerClick, isSelected)
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.add_page_number,
        backClick = { navController.navigateUp() },
        doneClick = { vm.addPageNumber(resolver = contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )

    when{
        DialogViewModel.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }
}

@Composable
fun CornerSelection(
    onCornerClick: (AddPageNumberSelection) -> Unit,
    isSelected: (AddPageNumberSelection) -> Boolean
)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()

        ){
            CornerCard(AddPageNumberSelection.TOP_LEFT, onCornerClick, 0.5f, isSelected)
            CornerCard(AddPageNumberSelection.TOP_RIGHT, onCornerClick, 1f, isSelected)
        }
        Row(modifier = Modifier
            .fillMaxWidth()

        ){
            CornerCard(AddPageNumberSelection.BOTTOM_LEFT, onCornerClick, 0.5f, isSelected)
            CornerCard(AddPageNumberSelection.BOTTOM_RIGHT, onCornerClick, 1f, isSelected)
        }

    }
}

@Composable
fun CornerCard(
    corner: AddPageNumberSelection,
    onCornerClick: (AddPageNumberSelection) -> Unit,
    size: Float,
    isSelected: (AddPageNumberSelection) -> Boolean
)
{
    val selected = isSelected(corner)
    Box(
        modifier = Modifier
            .fillMaxWidth(size)
            .height(100.dp)
            .padding(8.dp)
            .background(
                color = if(selected) colorResource(id = R.color.orange) else colorResource(id = R.color.white),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 4.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onCornerClick(corner)
            }
    ){
        Text(
            text = corner.corner,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if(selected) colorResource(id = R.color.white) else colorResource(id = R.color.orange),
            modifier = Modifier
                .height(100.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.Center)
        )
    }
}