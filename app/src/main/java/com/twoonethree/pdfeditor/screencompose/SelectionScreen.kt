package com.twoonethree.pdfeditor.screencompose

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.twoonethree.pdfeditor.Destination
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.utilities.ScreenName
import com.twoonethree.pdfeditor.utilities.StringUtilities

@Composable
fun SelectionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .background(color = colorResource(id = R.color.orange_light)),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .padding(start = 10.dp),
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
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .background(color = Color.White)
        )
        {
            PdfFunctionsListView(navController)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(8.dp)
                .background(
                    color = colorResource(id = R.color.orange_light),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    navController.navigate(Destination.MyCreationScreen.node)
                }
        )
        {
             Text(
                 text = stringResource(R.string.my_creation),
                 textAlign = TextAlign.Center,
                 fontWeight = FontWeight.ExtraBold,
                 color = Color.White,
                 modifier = Modifier
                     .fillMaxSize()
                     .wrapContentHeight(align = Alignment.CenterVertically)
                 )
        }
    }
}

@Composable
fun PdfFunctionsListView(navController: NavHostController) {
    val pdfFunctionArray = stringArrayResource(R.array.pdf_function_list).toList()
    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(pdfFunctionArray) {
            PDFoptionCard(
                it
            ) {
                navController.navigate(getDestinationNode(it))
            }
        }
    }
}

@Composable
fun PDFoptionCard(
    fname: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(130.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(5.dp))
            .padding(4.dp)
            .border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(5.dp))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = fname,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

fun getDestinationNode(node: String): String {
    when(node) {
        "Merge PDF" -> return ScreenName.MERGE_PDF_SCREEN
        "Split PDF" -> return ScreenName.SPLIT_PDF_SCREEN
        "Add Page Number" -> return ScreenName.ADD_PAGE_NUMBER_SCREEN
        "Orientation" -> return ScreenName.ORIENTATION_SCREEN
        "Protect PDF" -> return ScreenName.PASSWORD_PROTECTION_SCREEN
        "Unlock PDF" -> return ScreenName.UNLOCK_PASSWORD_SCREEN
        "Text to PDF" -> return ScreenName.PASSWORD_DIALOG_SCREEN
        else -> return ScreenName.SPLIT_PDF_SCREEN
    }
}

