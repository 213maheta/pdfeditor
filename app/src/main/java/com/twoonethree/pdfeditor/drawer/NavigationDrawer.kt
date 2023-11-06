package com.twoonethree.pdfeditor.drawer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.twoonethree.pdfeditor.BuildConfig
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.ui.theme.Orange
import com.twoonethree.pdfeditor.viewmodel.NavigationDrawerViewModel


@Composable
fun NavigationDrawer(columnScope: ColumnScope) {

    val vm = viewModel<NavigationDrawerViewModel>()
    val context = LocalContext.current

    columnScope.apply {
        AsyncImage(
            model = R.drawable.template_splash,
            contentDescription = "logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
            )
        Text(
            text = "Pdf Editor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            color = Orange,
            modifier = Modifier
                .fillMaxWidth()
        )
        Divider(modifier = Modifier.padding(vertical = 10.dp))
        DrawerItem("Share") {
            vm.shareApp()?.let {
                startActivity(context, it, null)
            }
        }
        DrawerItem("Rate us"){
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")), null)
        }
        DrawerItem("Privacy policy"){
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy?hl=en-US")), null)

        }
        DrawerItem("Contact us"){

        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        Text(
            text = "Version: ${BuildConfig.VERSION_NAME}",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            color = Orange,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun DrawerItem(value:String, onClick:()->Unit)
{
    Text(
        text = value,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        fontFamily = FontFamily.SansSerif,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(top = 15.dp, bottom = 15.dp, start = 30.dp)

    )
}