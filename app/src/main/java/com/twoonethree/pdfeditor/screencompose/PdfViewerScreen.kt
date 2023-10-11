package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.utilities.StringUtilities
import com.twoonethree.pdfeditor.viewmodel.PasswordDialogViewModel
import com.twoonethree.pdfeditor.viewmodel.PdfViewerViewModel

@Composable
fun PdfViewerScreen(navController: NavHostController, selectedFile: String?)
{
    val vm = viewModel<PdfViewerViewModel>()
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp * 4

    LaunchedEffect(key1 = Unit){
        selectedFile?.let {
            val uri = StringUtilities.addSlash(it).toUri()
            vm.getAllPage(contentResolver, uri, screenWidth)
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
                PdfPageListView(vm.pdfPageList.toList())
            }
        }

    MyTopAppBar(
        titleId = R.string.pdf_viewer,
        backClick = { navController.navigateUp() },
        doneClick = { },
        floatBtnClick = { },
        innerContent = innerContent,
    )
}

@Composable
fun PdfPageListView(bitmapList: List<ImageBitmap>)
{
    val listState = rememberLazyListState()
    val scale = remember { mutableStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale.value *= zoomChange
        offset.value += offsetChange
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(bottom = 5.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                translationX = offset.value.x
            }
            .transformable(state = state)
            .fillMaxSize()
        )
    {
        items(bitmapList){
            ItemPage(it)

        }
    }

}

@Composable
fun ItemPage(imageBitmap: ImageBitmap)
{
    Image(
        bitmap = imageBitmap,
        contentDescription = "Pdf page",
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.grey_light),
                shape = RoundedCornerShape(4.dp)
            )
        )
}