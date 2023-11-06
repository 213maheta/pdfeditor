package com.twoonethree.pdfeditor.dialog

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.screencompose.ButtonWithIcon
import com.twoonethree.pdfeditor.screencompose.PasswordTextEdit
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.utilities.StringUtilities
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel
import kotlinx.coroutines.Job

@Composable
fun PasswordDialogScreen(callback: (ScreenCommonEvents) -> Unit) {
    val vm = viewModel<DialogViewModel>()
    val vmCommon = viewModel<CommonComposeViewModel>()
    val contentResolver = LocalContext.current.contentResolver
    val pdfData = DialogViewModel.selectedPdf.value


    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowSnackBar -> {
                    vmCommon.message.value = it.value
                    vmCommon.status = it.color
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }

                is ScreenCommonEvents.GotPassword -> {
                    DialogViewModel.selectedPdf.value.totalPageNumber = it.totalPageNumber
                    DialogViewModel.isPasswordDialogueVisible.value = false
                }

                else -> {}
            }
        }
    }

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp),
            shape = RoundedCornerShape(16.dp),

            )
        {
            Text(
                text = pdfData.name,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
            )
            Text(
                text = "This file is protected",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
            )
            PasswordTextEdit(vm.password.value) { value: String ->
                vm.password.value = value
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 20.dp, bottom = 30.dp)
            ) {
                Text(
                    text = "OK",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 40.dp)
                        .clickable {
                            vm.checkPassword(contentResolver = contentResolver)
                        }
                )
                Text(
                    text = "Cancel",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable {
                            DialogViewModel.isPasswordDialogueVisible.value = false
                        }
                )
            }
        }
    }
}

@Composable
fun DeleteDialogScreen(callback: () -> Unit) {

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(5.dp),

            )
        {

            AsyncImage(model = R.drawable.template_delete, contentDescription = "Image")

            Text(
                text = "Delete 1 File permanently",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 20.dp, bottom = 20.dp)
            ) {

                ButtonWithIcon(
                    value = "Cancel",
                    onClick = { DialogViewModel.isDeleteDialogVisible.value = false },
                    backgroundColor = Color.White,
                    textColor = Orange,
                    iconTint = Color.Black,
                    iconId = Icons.Default.Clear
                )

                ButtonWithIcon(
                    value = "Delete",
                    onClick = {
                        callback()
                        DialogViewModel.isDeleteDialogVisible.value = false
                    },
                    backgroundColor = Orange,
                    textColor = Color.White,
                    iconTint = Color.White,
                    iconId = Icons.Default.Delete
                )
            }
        }
    }
}


@Composable
fun RenameDialogScreen(filename: String, callback: (String) -> Job) {

    val vm = viewModel<DialogViewModel>()
    LaunchedEffect(key1 = Unit)
    {
        vm.newName.value = StringUtilities.removeExtention(filename)
    }

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(5.dp),
        )
        {

            AsyncImage(model = R.drawable.template_rename, contentDescription = "Image")

            Text(
                text = "Rename File",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .fillMaxWidth()
            )
            MyTextEdit()
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 20.dp, bottom = 20.dp)
            ) {

                ButtonWithIcon(
                    value = "Cancel",
                    onClick = { DialogViewModel.isRenameDialogVisible.value = false },
                    backgroundColor = Color.White,
                    textColor = Orange,
                    iconTint = Color.Black,
                    iconId = Icons.Default.Clear
                )

                ButtonWithIcon(
                    value = "Rename",
                    onClick = {
                        callback(StringUtilities.addExtention(vm.newName.value))
                        DialogViewModel.isRenameDialogVisible.value = false
                    },
                    backgroundColor = Orange,
                    textColor = Color.White,
                    iconTint = Color.White,
                    iconId = Icons.Default.Delete
                )
            }
        }
    }
}


@Composable
fun SuccessDialog()
{
    val vm = viewModel<DialogViewModel>()

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(5.dp),
        )
        {


        }
    }
}


@Composable
fun MyTextEdit() {
    val vm = viewModel<DialogViewModel>()
    TextField(
        value = vm.newName.value,
        onValueChange = { vm.newName.value = it },
        placeholder = { Text(stringResource(R.string.enter_file_name)) },
        colors = TextFieldDefaults.colors(
            cursorColor = colorResource(id = R.color.orange),
            focusedIndicatorColor = colorResource(id = R.color.orange),
            unfocusedIndicatorColor = colorResource(id = R.color.orange),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}


