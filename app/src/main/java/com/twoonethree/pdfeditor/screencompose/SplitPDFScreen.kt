package com.twoonethree.pdfeditor.screencompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel
import com.twoonethree.pdfeditor.viewmodel.SplitPDFViewModel

@Composable
fun SplitPDFScreen(navController: NavHostController) {
    val vm = viewModel<SplitPDFViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()

    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver

    val pickPdfDocument = pdfLauncher { pdf ->
        vm.selectedPdf.value = pdf
        vm.splitPointList.clear()
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
                        SplitPointsNumber()
                    }
                }
            }
        }

    MyTopAppBar(
        titleId = R.string.pdf_split,
        backClick = { navController.navigateUp() },
        doneClick = { vm.saveSplitPdf(contentResolver = contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(context.getString(R.string.application_pdf)) },
        innerContent = innerContent,
    )

    when{
        DialogViewModel.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }

    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar()
    }

    ShowSnackBar()
}

@Composable
fun SplitPointsNumber() {
    val vm = viewModel<SplitPDFViewModel>()
    val onDone = vm::addSplitPoints
    val onDelete = vm::removeSplitPoints
    val onRefresh = vm::removeAllSplitPoints

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NumberBox(onDone, onRefresh)
        SplitsPointsList(vm.splitPointList.toList(), onDelete)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberBox(onDone: (String) -> Unit, onRefresh: () -> Unit) {
    val text = remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    )
    {
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            label = { Text("Add split points") },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = colorResource(id = R.color.orange),
                    shape = RoundedCornerShape(5.dp)
                )
                .weight(0.8f)
        )
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.refresh),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    onRefresh()
                    text.value = ""
                }
                .weight(0.2f)
        )
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.done),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    onDone(text.value)
                    text.value = ""
                }
                .weight(0.2f)
        )
    }
}

@Composable
fun SplitsPointsList(splitPointList: List<Int>, onDelete: (value: Int) -> Unit) {
    val list = splitPointList.sortedBy { it }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        items(list) { int ->
            ItemSpliPoints(int, onDelete)
        }
    }
}

@Composable
fun ItemSpliPoints(value: Int, onDelete: (value: Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    )
    {
        Text(
            text = value.toString(),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(0.8f)
        )
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    onDelete(value)
                }
                .weight(0.2f)
        )
    }
}

