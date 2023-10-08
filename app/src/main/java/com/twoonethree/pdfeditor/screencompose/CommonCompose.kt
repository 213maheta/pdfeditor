package com.twoonethree.pdfeditor.screencompose

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.FileUtilities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    titleId: Int,
    backClick: () -> Boolean,
    doneClick: () -> Unit,
    floatBtnClick: () -> Unit,
    innerContent: @Composable (paddingvalues: PaddingValues) -> Unit
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        color = Color.White
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
            FloatingActionButton(
                onClick = {
                    floatBtnClick()
                },
                shape = CircleShape,
                containerColor = colorResource(id = R.color.orange_light)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    ) { contentPadding ->
        innerContent(contentPadding)
    }
}

@Composable
fun pdfLauncher(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<String, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                onSuccess(pdf)
            }
        }
    )
}

@Composable
fun pdfLauncherOpenDocument(onSuccess: (PdfData) -> Boolean): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val contentResolver = LocalContext.current.contentResolver
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                onSuccess(pdf)
            }
        }
    )
}

@Composable
fun pdfLauncherMulti(onSuccess: (List<PdfData>) -> Boolean): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    val contentResolver = LocalContext.current.contentResolver
     return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
         onResult = { uriList ->
             uriList.let {
                 val pdfDataList = mutableListOf<PdfData>()
                 it.forEach{
                     val pdf = FileUtilities.getFileData(resolver = contentResolver, uri = it)
                     pdfDataList.add(pdf)
                 }
                 onSuccess(pdfDataList)
             }
         }
    )
}

@Composable
fun ItemPDF(pdf: PdfData, removePdf: (PdfData) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp)
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Image(bitmap = pdf.thumbnail!!,
            contentDescription = "",
            modifier = Modifier
                .size(80.dp)
                .padding(start = 4.dp)
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.grey),
                    shape = RoundedCornerShape(5.dp)
                )
        )
        Row(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Column (
                modifier = Modifier
                    .height(IntrinsicSize.Max)
            ){
                Text(
                    text = pdf.name,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(40.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                    ,
                    textAlign = TextAlign.Center
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(40.dp)
                        .fillMaxWidth()
                ){
                    Text(
                        text = pdf.size,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(0.5f)
                    )
                    Text(
                        text = pdf.totalPageNumber.toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(0.3f)
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black,
                        modifier = Modifier
                            .weight(0.2f)
                            .clickable {
                                removePdf(pdf)
                            }
                    )
                }
            }
        }
    }
}

fun myToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}