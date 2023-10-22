package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.Destination
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.utilities.StringUtilities
import com.twoonethree.pdfeditor.viewmodel.MyCreationViewModel
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel

@Composable
fun MyCreationScreen(navController: NavHostController) {

    val vm = viewModel<MyCreationViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val onItemClick = { value: String ->
        navController.navigate(
            Destination.PdfViewerScreen.node + "/" + StringUtilities.removeSlash(value)
        )
    }

    LaunchedEffect(key1 = Unit) {
        vm.getAllPdf(contentResolver = contentResolver)
        vm.uiIntent.collect {
            when (it) {
                is ScreenCommonEvents.ShowToast -> {
                    myToast(context, it.message)
                    vm.setUiIntent(ScreenCommonEvents.EMPTY)
                }

                else -> {}
            }
        }
    }

    val innerContent: @Composable (paddingvalues: PaddingValues) -> Unit =
        { paddingvalues: PaddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingvalues)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                CreatedPdfList(vm.pdfList.toList(), onItemClick)
            }
        }

    MyTopAppBar(
        titleId = R.string.my_creation,
        backClick = { navController.navigateUp() },
        doneClick = { },
        floatBtnClick = { },
        innerContent = innerContent,
    )
}

@Composable
fun CreatedPdfList(pdfList: List<PdfData>, onItemClick: (String) -> Unit) {
    LazyColumn()
    {
        items(pdfList) {
            ItemPdfGridCell(it, onItemClick)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(horizontal = 6.dp)
                    .background(color = colorResource(id = R.color.grey_light))
            )
        }
    }
}

@Composable
fun ItemPdfGridCell(pdfData: PdfData, onItemClick: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onItemClick(pdfData.uri.toString())
            }
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        pdfData.thumbnail?.let {
            Image(
                bitmap = it,
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
                Image(
                    painter = painterResource(id = R.drawable.splash_icon),
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

            )
        }

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = stringResource(R.string.menu),
            modifier = Modifier
                .weight(0.1f)
                .clickable {

                }
        )
    }
}