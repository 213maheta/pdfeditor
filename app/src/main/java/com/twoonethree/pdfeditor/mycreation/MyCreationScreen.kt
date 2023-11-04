package com.twoonethree.pdfeditor.mycreation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.twoonethree.pdfeditor.Destination
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.screencompose.ModelBottomSheetScreen
import com.twoonethree.pdfeditor.screencompose.myToast
import com.twoonethree.pdfeditor.utilities.StringUtilities
import java.io.File

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

    val onMenuClick = {pdf:PdfData ->
        vm.selectedPdf.value = pdf
        vm.showBottomSheet.value = true
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
    CreatedPdfList(vm.pdfList.toList(), onItemClick, onMenuClick)
    ModelBottomSheetScreen()
}

@Composable
fun CreatedPdfList(
    pdfList: List<PdfData>,
    onItemClick: (String) -> Unit,
    onMenuClick: (PdfData) -> Unit
) {
    LazyColumn()
    {
        items(pdfList) {
            ItemPdf(it, onItemClick, onMenuClick)
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
fun ItemPdf(pdfData: PdfData, onItemClick: (String) -> Unit, onMenuClick: (PdfData) -> Unit) {
    val resolver = LocalContext.current.contentResolver
    val vm = viewModel<MyCreationViewModel>()
    val thumbnailPath = remember {
        mutableStateOf<File?>(null)
    }

    LaunchedEffect(key1 = pdfData) {
        pdfData.uri?.let {
            thumbnailPath.value = vm.getThumbNail(resolver, it)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onItemClick(pdfData.uri.toString())
            }
            .padding(10.dp)
            .fillMaxWidth()
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
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_icon),
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
                    onMenuClick(pdfData)
                }
        )
    }
}