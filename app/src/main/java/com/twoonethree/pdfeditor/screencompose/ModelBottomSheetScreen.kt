package com.twoonethree.pdfeditor.screencompose

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.dialog.DeleteDialogScreen
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.mycreation.MyCreationViewModel
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.dialog.DialogViewModel
import com.twoonethree.pdfeditor.dialog.RenameDialogScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelBottomSheetScreen()
{
    val context = LocalContext.current
    val vm = viewModel<MyCreationViewModel>()
    val vmDialog = viewModel<DialogViewModel>()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val onShareClick = {
        vm.selectedPdf.value.uri?.toFile()?.let {
            val shareUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                it
            )
            context.startActivity(Intent.createChooser(vm.shareIntent(shareUri), "Share"));
        }?: run {

        }
    }

    if (vm.showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                vm.showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            ItemPdf(pdfData = vm.selectedPdf.value)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 6.dp)
                .background(color = colorResource(id = R.color.black))
            )

            IconRow(Icons.Default.Delete, R.string.delete, { vmDialog.isDeleteDialogVisible.value = true})
            IconRow(Icons.Default.Share, R.string.share, onShareClick)
            IconRow(Icons.Default.Person, R.string.rename, { vmDialog.isRenameDialogVisible.value = true})

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
            )
        }
    }

    when{
        vmDialog.isDeleteDialogVisible.value -> DeleteDialogScreen(vm::delete)
    }

    when{
        vmDialog.isRenameDialogVisible.value -> RenameDialogScreen(vm.selectedPdf.value.name, vm::rename)
    }
}

@Composable
fun IconRow(icon: ImageVector, nameId: Int, function: () -> Unit)
{
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                function()
            }
    )
    {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = nameId),
            modifier = Modifier
                .padding(start = 20.dp)
        )
        Text(
            text = stringResource(id = nameId),
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )
    }
}

@Composable
fun ItemPdf(pdfData: PdfData) {
    val resolver = LocalContext.current.contentResolver

    val imageBitmap = remember<MutableState<ImageBitmap?>> {
        mutableStateOf(ImageBitmap(1, 1))
    }

    LaunchedEffect(key1 = Unit) {
        pdfData.uri?.let {
            Log.e("TAG", "ItemPdf: ${pdfData.uri}", )
            imageBitmap.value = PdfUtilities.getPdfThumbnail(resolver, it)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        imageBitmap.value?.let {
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
    }
}

