package com.twoonethree.pdfeditor.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
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
import androidx.compose.ui.text.input.KeyboardType
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
    val pdfData = vm.selectedPdf.value


    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowSnackBar -> {
                    vmCommon.message.value = it.value
                    vmCommon.status = it.color
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }

                is ScreenCommonEvents.GotPassword -> {
                    vm.selectedPdf.value.totalPageNumber = it.totalPageNumber
                    vm.isPasswordDialogueVisible.value = false
                    callback(ScreenCommonEvents.GotPassword(
                        totalPageNumber = it.totalPageNumber,
                        password = it.password
                    ))
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }

                else -> {}
            }
        }
    }

    Dialog(onDismissRequest = {
        vm.errorText.value = ""
        vm.password.value = ""
    }) {
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
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = "This file is protected",
                color = Orange,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
            PasswordTextEdit(vm.password.value) { value: String ->
                vm.password.value = value
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 20.dp, bottom = 20.dp)
            ) {

                ButtonWithIcon(
                    value = "Cancel",
                    onClick = { vm.isPasswordDialogueVisible.value = false },
                    backgroundColor = Color.White,
                    textColor = Orange,
                    iconTint = Color.Black,
                    iconId = Icons.Default.Clear
                )

                ButtonWithIcon(
                    value = "Okay",
                    onClick = { vm.checkPassword(contentResolver = contentResolver) },
                    backgroundColor = Orange,
                    textColor = Color.White,
                    iconTint = Color.White,
                    iconId = Icons.Default.Check
                )
            }

            Text(
                text = vm.errorText.value,
                color = Orange,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
        }
    }
}


@Composable
fun DeleteDialogScreen(callback: () -> Unit) {
    val vm = viewModel<DialogViewModel>()

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
                    onClick = { vm.isDeleteDialogVisible.value = false },
                    backgroundColor = Color.White,
                    textColor = Orange,
                    iconTint = Color.Black,
                    iconId = Icons.Default.Clear
                )

                ButtonWithIcon(
                    value = "Delete",
                    onClick = {
                        callback()
                        vm.isDeleteDialogVisible.value = false
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
fun RenameDialogScreen(filename: String, callback: (String) -> Job, validateSameName: (String) -> Boolean) {

    val vm = viewModel<DialogViewModel>()
    LaunchedEffect(key1 = Unit)
    {
        vm.newName.value = StringUtilities.removeExtention(filename)
    }

    Dialog(onDismissRequest = {
        vm.warningText.value = ""
    }) {
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
                    onClick = { vm.isRenameDialogVisible.value = false },
                    backgroundColor = Color.White,
                    textColor = Orange,
                    iconTint = Color.Black,
                    iconId = Icons.Default.Clear
                )

                ButtonWithIcon(
                    value = "Rename",
                    onClick = {
                        if(!vm.validateRename())
                            return@ButtonWithIcon
                        if(!validateSameName(vm.newName.value))
                            return@ButtonWithIcon
                        callback(StringUtilities.addExtention(vm.newName.value))
                        vm.isRenameDialogVisible.value = false
                    },
                    backgroundColor = Orange,
                    textColor = Color.White,
                    iconTint = Color.White,
                    iconId = Icons.Default.Create
                )
            }

            Text(
                text = vm.warningText.value,
                color = Orange,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}


