package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.OrientationViewModel
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel

@Composable
fun OrientationScreen(navController: NavController) {
    val vm = viewModel<OrientationViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onOrientationClick = {value:Int -> vm.currentOrientation.value = value}
    val isSelected :(Int)->Boolean = { value: Int -> vm.currentOrientation.value == value}


    val pickPdfDocument = pdfLauncherOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        vm.getOrientation(contentResolver, pdf.uri)
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
                    vm.changeOrientation(resolver = contentResolver, vm.selectedPdf.value.uri, it.pdfReaderOuter)
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
                        SetOrientation(isSelected, onOrientationClick)
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.change_orientation,
        backClick = { navController.navigateUp() },
        doneClick = { vm.changeOrientation(resolver = contentResolver, vm.selectedPdf.value.uri, null) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetOrientation(isSelected: (Int) -> Boolean, onOrientationClick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.Center,
        modifier = Modifier
        .fillMaxWidth()
    ) {
        OrientationCard(0, isSelected, onOrientationClick)
        OrientationCard(90, isSelected, onOrientationClick)
        OrientationCard(180, isSelected, onOrientationClick)
        OrientationCard(270, isSelected, onOrientationClick)
    }
}

@Composable
fun OrientationCard(angle: Int, isSelected: (Int) -> Boolean, onOrientationClick: (Int) -> Unit)
{
    val selected = isSelected(angle)
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(8.dp)
            .background(
                color = if (selected) colorResource(id = R.color.orange) else colorResource(id = R.color.white),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 4.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onOrientationClick(angle)
            }
    ){
        Text(
            text = angle.toString(),
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