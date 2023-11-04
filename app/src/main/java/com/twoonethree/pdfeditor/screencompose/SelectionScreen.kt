package com.twoonethree.pdfeditor.screencompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.mycreation.MyCreationScreen
import com.twoonethree.pdfeditor.utilities.ScreenName

@Composable
fun SelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.075f)
                .background(color = colorResource(id = R.color.orange_light)),
        )
        {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.CenterStart),
                tint = Color.White
            )
            Text(
                text = stringResource(id = R.string.pdf_tools),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.Center),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.175f)
                .background(color = Color.White)
        )
        {
            PdfFunctionsList(navController)
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.75f))
        {
            MyCreationScreen(navController = navController)
        }

    }
}

@Composable
fun PdfFunctionsList(navController: NavHostController) {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            PDFoptionCard("Merge PDF", R.drawable.ic_merge_pdf,{ navController.navigate(ScreenName.MERGE_PDF_SCREEN) })
        }
        item {
            PDFoptionCard("Split PDF", R.drawable.ic_split_pdf,{ navController.navigate(ScreenName.SPLIT_PDF_SCREEN) })
        }
        item {
            PDFoptionCard(
                "Image to PDF",
                R.drawable.ic_imageto_pdf,
                { navController.navigate(ScreenName.IMAGE_TO_PDF_SCREEN) })

        }
        item {
            PDFoptionCard(
                "Add Page Number",
                R.drawable.ic_addpagenumber_pdf,
                { navController.navigate(ScreenName.ADD_PAGE_NUMBER_SCREEN) })
        }
        item {
            PDFoptionCard(
                "Organize PDF",
                R.drawable.ic_organise_pdf,
                { navController.navigate(ScreenName.ORGANIZE_PDF_SCREEN) })
        }
        item {
            PDFoptionCard(
                "Lock PDF",
                R.drawable.ic_lock_pdf,
                { navController.navigate(ScreenName.LOCK_PDF_SCREEN) })
        }
        item {
            PDFoptionCard(
                "Unlock PDF",
                R.drawable.ic_unlock_pdf,
                { navController.navigate(ScreenName.UNLOCK_PASSWORD_SCREEN) })
        }
        item {
            PDFoptionCard(
                "Orientation",
                R.drawable.ic_rotate_pdf,
                { navController.navigate(ScreenName.ROTATE_PDF_SCREEN) })
        }

    }
}

@Composable
fun PDFoptionCard(
    fname: String,
    drawableId: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "",
            modifier = Modifier
                .width(40.dp)
                .weight(0.8f)
        )
        Text(
            text = fname,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(0.2f)
        )
    }
}

