package com.twoonethree.pdfeditor.screencompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.twoonethree.lazylist.reorder.reOrderableItem
import com.twoonethree.lazylist.reorder.reOrderableList
import com.twoonethree.lazylist.reorder.swap
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.OrganizePdfViewModel
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel

@Composable
fun OrganizePdfScreen(navController: NavController)
{
    val vm = viewModel<OrganizePdfViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()
    val vmDialog = viewModel<DialogViewModel>()


    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onDelete = vm::removePage
    val changeOrder = vm::changePosition

    val pickPdfDocument = pdfPickerOpenDocument { pdf ->
        vm.selectedPdf.value = pdf
        vm.setPageNumberList()
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
                    vm.setPageNumberList()
                    vmDialog.isPasswordDialogueVisible.value = false
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
                        PageNumberList(vm.pageNumberList, onDelete, changeOrder)
                    }
                }
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
        vmDialog.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }
    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar()
    }

    ShowSnackBar()
}

@Composable
fun PageNumberList(
    pageNumberList: SnapshotStateList<Int>,
    onDelete: (value: Int) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
) {

    val listState = rememberLazyListState()

    LazyColumn(state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .reOrderableList(listState) { selected: Int, hover: Int ->
                pageNumberList.swap(selected, hover)
            }
    ) {
        itemsIndexed(pageNumberList) { index, value ->
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .reOrderableItem(index)
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = colorResource(id = R.color.grey_light))
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
                    changeOrder(index, index - 1)
                }
                .weight(0.2f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.down),
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable {
                    changeOrder(index, index + 1)
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

