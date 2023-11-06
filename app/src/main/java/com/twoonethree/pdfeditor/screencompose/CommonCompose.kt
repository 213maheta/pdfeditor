package com.twoonethree.pdfeditor.screencompose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.viewmodel.CommonComposeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    titleId: Int,
    backClick: () -> Boolean,
    doneClick: () -> Unit,
    floatBtnClick: () -> Unit,
    innerContent: @Composable (paddingvalues: PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                backClick()
                            },
                        tint = Color.White,

                        )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.done),
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .clickable {
                                doneClick()
                            },
                        tint = Color.White,
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(id = R.color.orange_light))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Pick PDF", color = Color.White) },
                icon = { Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Back",
                    tint = Color.White
                ) },
                onClick = {
                    floatBtnClick()
                },
                containerColor = Orange
            )
        },
    ) { contentPadding ->
        innerContent(contentPadding)
    }
}

@Composable
fun ItemPDF(pdfData: PdfData, removePdf: (PdfData) -> Unit, index: Int = 0) {
    val resolver = LocalContext.current.contentResolver
    val vmCommon = viewModel<CommonComposeViewModel>()
    val vmDialog = viewModel<DialogViewModel>()

    val thumbnailPath = remember {
        mutableStateOf<File?>(null)
    }

    LaunchedEffect(key1 = pdfData) {
        pdfData.uri?.let {
            thumbnailPath.value = vmCommon.getThumbNail(resolver, it)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 2.dp)
            .border(
                width = 0.5.dp,
                color = colorResource(id = R.color.orange_light),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(2.dp)
            .background(
                color = colorResource(id = R.color.grey_light),
                shape = RoundedCornerShape(4.dp),
            )
    ) {
        thumbnailPath.value?.let {
            AsyncImage(
                model = it,
                contentDescription = "",
                modifier = Modifier
                    .weight(0.1f)
                    .size(50.dp)
                    .padding(2.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.grey),
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        } ?: kotlin.run {
            Box(
                modifier = Modifier
                    .weight(0.1f)
                    .size(50.dp)
                    .padding(2.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.grey),
                        shape = RoundedCornerShape(5.dp)
                    )
            )
            {
                AsyncImage(
                    model  = R.drawable.ic_default_pdf,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(0.8f)
        ) {
            Text(
                text = pdfData.name,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 20.dp, top = 2.dp, bottom = 2.dp)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp)
            )
            {
                Text(
                    text = "PDF",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
                Text(
                    text = pdfData.size,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
                Text(
                    text = pdfData.date ?: "",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
                Text(
                    text = pdfData.totalPageNumber.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                )
            }
        }
        if (pdfData.totalPageNumber == 0) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.lock),
                modifier = Modifier
                    .weight(0.1f)
                    .clickable {
                        vmDialog.selectedPdf.value = pdfData
                        vmDialog.isPasswordDialogueVisible.value = true
                        vmDialog.selectedIndex = index
                    }
            )
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.Black,
            modifier = Modifier
                .weight(0.1f)
                .clickable {
                    removePdf(pdfData)
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextEdit(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(stringResource(R.string.enter_password)) },
        colors = TextFieldDefaults.colors(
            cursorColor = colorResource(id = R.color.orange),
            focusedIndicatorColor = colorResource(id = R.color.orange),
            unfocusedIndicatorColor = colorResource(id = R.color.orange),
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun TextWithBorder(value: String, onClick: () -> Unit, endMargin: Dp) {
    Text(
        text = value,
        color = colorResource(id = R.color.orange),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(end = endMargin)
            .width(60.dp)
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onClick()
            }
            .padding(4.dp)
    )
}

@Composable
fun ButtonWithIcon(
    value: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    iconTint:Color,
    iconId: ImageVector
)
{
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        .width(100.dp)
        .border(
            width = 1.dp,
            color = colorResource(id = R.color.orange),
            shape = RoundedCornerShape(8.dp))
        .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
        .clickable {
            onClick()
        }
        .padding(4.dp))
    {
        Icon(imageVector = iconId, contentDescription = "Delete", tint = iconTint)
        Text(
            text = value,
            color = textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CircularProgressBar(progress: Float? = null) {
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable { }
        .graphicsLayer {
            alpha = 0.6f
        }
        .background(color = colorResource(id = R.color.grey_light))

    )
    {
        progress?.let {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(36.dp)
                    .align(Alignment.Center),
                color = colorResource(id = R.color.orange),
                trackColor = colorResource(id = R.color.grey),
                progress = progress
            )
        } ?: kotlin.run {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(36.dp)
                    .align(Alignment.Center),
                color = colorResource(id = R.color.orange),
                trackColor = colorResource(id = R.color.grey),
            )
        }
    }
}


fun myToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Composable
fun ShowSnackBar() {

    val vm = viewModel<CommonComposeViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = vm.message.value)
    {
        if (vm.message.value.isNotEmpty()) {
            snackbarHostState.showSnackbar(vm.message.value)
            vm.message.value = ""
        }
    }

    SnackbarHost(hostState = snackbarHostState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        )
        {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .padding(vertical = 30.dp)
                    .graphicsLayer {
                        shadowElevation = 5f
                    }
                    .background(color = vm.status)
                    .padding(vertical = 10.dp)
                    .align(Alignment.BottomCenter),
                text = vm.message.value,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}