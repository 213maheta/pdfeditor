package com.twoonethree.pdfeditor.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.screencompose.GetPassword
import com.twoonethree.pdfeditor.screencompose.TextWithBorder
import com.twoonethree.pdfeditor.screencompose.myToast

@Composable
fun PasswordDialogScreen(callback: (ScreenCommonEvents) -> Unit) {
    val vm = viewModel<DialogViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val pdfData = DialogViewModel.selectedPdf.value


    LaunchedEffect(key1 = Unit) {
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
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
            GetPassword(vm.password.value) { value: String ->
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
                            vm.checkPassword(
                                contentResolver = contentResolver,
                                callback
                            )
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
            Text(
                text = "Are you sure want to delete this?",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, top = 20.dp, bottom = 20.dp)
            ) {
                TextWithBorder(value = "Yes", onClick = {
                    callback()
                    DialogViewModel.isDeleteDialogVisible.value = false}, endMargin = 20.dp
                )

                TextWithBorder(
                    value = "Cancel", onClick = {
                        DialogViewModel.isDeleteDialogVisible.value = false}, endMargin = 10.dp
                )
            }
        }
    }
}


@Composable
fun RenameDialogScreen(callback: () -> Unit) {

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

