package com.twoonethree.pdfeditor.screencompose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.twoonethree.pdfeditor.Destination
import com.twoonethree.pdfeditor.R
import com.twoonethree.pdfeditor.mycreation.MyCreationScreen
import kotlinx.coroutines.delay

@Composable
fun Navingation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.SelectionScreen.node) {
        composable(Destination.SplashScreen.node) {
            SplashScreen(navController)
        }
        composable(Destination.SelectionScreen.node) {
            SelectionScreen(navController)
        }
        composable(Destination.MergePDFScreen.node) {
            MergePDFScreen(navController)
        }
        composable(Destination.SplitPDFScreen.node) {
            SplitPDFScreen(navController)
        }
        composable(Destination.MyCreationScreen.node) {
            MyCreationScreen(navController)
        }
        composable(Destination.PdfViewerScreen.node+"/{filepath}") {
            val selectedFile = it.arguments?.getString("filepath")
            PdfViewerScreen(navController, selectedFile)
        }
        composable(Destination.AddPageNumberScreen.node) {
            AddPageNumberScreen(navController)
        }
        composable(Destination.OrientationScreen.node) {
            OrientationScreen(navController)
        }
        composable(Destination.PasswordProtectionScreen.node) {
            PasswordProtectionScreen(navController)
        }
        composable(Destination.UnlockPasswordScreen.node) {
            UnlockPdfScreen(navController)
        }
        composable(Destination.OrganizePdfScreen.node) {
            OrganizePdfScreen(navController)
        }
        composable(Destination.ImageToPdfScreen.node) {
            ImageToPdfScreen(navController)
        }
    }
}


@Composable
fun SplashScreen(navController: NavHostController) {
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        )
        {
            Image(
                painter = painterResource(id = R.drawable.splash_icon),
                contentDescription = ""
            )
        }
    }

    LaunchedEffect(key1 = Unit)
    {
        delay(3000)
        navController.navigate(Destination.SelectionScreen.node)
    }
}





