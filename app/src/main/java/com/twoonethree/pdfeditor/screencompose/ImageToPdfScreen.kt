package com.twoonethree.pdfeditor.screencompose

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.PasswordDialogScreen
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.viewmodel.ImageToPdfViewModel
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel

@Composable
fun ImageToPdfScreen(navController: NavController)
{
    val vm = viewModel<ImageToPdfViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()
    val vmDialog = viewModel<DialogViewModel>()

    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onDelete = vm::removePage
    val changeOrder = vm::changePosition

    val imagePicker = ImageLauncher {
        if(it.isEmpty())
            return@ImageLauncher
        vm.uriList.clear()
        vm.uriList.addAll(it)
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
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }
                is ScreenCommonEvents.GotPassword -> {
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
                vm.uriList.let {
                    ImagePreviewLazyColumn(uriList = it, onDelete, changeOrder)
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
        vmDialog.isPasswordDialogueVisible.value -> PasswordDialogScreen(vm::setUiIntent)
    }

    AnimatedVisibility(visible = vm.showProgressBar.value) {
        CircularProgressBar()
    }

    ShowSnackBar()
}

@Composable
fun ImagePreviewLazyColumn(
    uriList: List<Uri>,
    onDelete: (value: Uri) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
)
{
   LazyColumn(modifier = Modifier
       .fillMaxSize()
   ){
       itemsIndexed(uriList){index, value ->
           ImagePreview(index, value, onDelete, changeOrder)
       }
   }
}

@Composable
fun ImagePreview(
    index: Int,
    value: Uri,
    onDelete: (value: Uri) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
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
        ImageBottomBar(index = index, value =value , onDelete = onDelete , changeOrder = changeOrder)
    }
}

@Composable
fun ImageBottomBar(
    index: Int,
    value: Uri,
    onDelete: (value: Uri) -> Unit,
    changeOrder: (index1: Int, index2: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 2.dp)
            .background(
                color = Color.Gray,
                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
            )
            .padding(10.dp)
    )
    {

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