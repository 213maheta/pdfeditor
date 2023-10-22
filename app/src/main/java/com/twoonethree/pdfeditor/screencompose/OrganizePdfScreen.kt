package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.OrganizePdfViewModel
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel

@Composable
fun OrganizePdfScreen(navController: NavController)
{
    val vm = viewModel<OrganizePdfViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onDelete = vm::removePage
    val changeOrder = vm::changePosition

    val pickPdfDocument = pdfLauncherOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        vm.setPageNumberList()
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
                    PasswordDialogViewModel.selectedPdf.value = vm.selectedPdf.value
                    PasswordDialogViewModel.isVisible.value = true
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
                    vm.selectedPdf.value.totalPageNumber = it.totalPageNumber
                    vm.selectedPdf.value.password = it.password
                    vm.setPageNumberList()
                    PasswordDialogViewModel.isVisible.value = false
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
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
                        PageNumberList(vm.pageNumberList.toList(), onDelete, changeOrder)
                    }
                }
                ReOrderableList(onDelete, changeOrder)
            }
        }

    MyTopAppBar(
        titleId = R.string.organize_pdf,
        backClick = { navController.navigateUp() },
        doneClick = { vm.reOrderPdf(resolver = contentResolver) },
        floatBtnClick = { pickPdfDocument.launch(arrayOf(context.getString(R.string.application_pdf))) },
        innerContent = innerContent,
    )

    when{
        PasswordDialogViewModel.isVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }
}

@Composable
fun PageNumberList(
    PageNumberList: List<Int>,
    onDelete: (value: Int) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(PageNumberList) { index, value ->
            ItemPageNumer(index, value, onDelete, changeOrder)
        }
    }
}

@Composable
fun ItemPageNumer(
    index: Int,
    value: Int,
    onDelete: (value: Int) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
) {

    val offset = remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        offset.value += offsetChange
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .graphicsLayer {
                translationY = offset.value.y
            }
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(10.dp)
            )
            .transformable(state = state)
            .padding(10.dp)
    )
    {
        Text(
            text = value.toString(),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(0.4f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = stringResource(R.string.up),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    changeOrder(index, index-1)
                }
                .weight(0.2f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.down),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    changeOrder(index, index+1)
                }
                .weight(0.2f)
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