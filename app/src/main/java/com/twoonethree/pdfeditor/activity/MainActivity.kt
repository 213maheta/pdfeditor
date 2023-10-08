package com.twoonethree.pdfeditor.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.twoonethree.pdfeditor.screencompose.AppDirectorySetup
import com.twoonethree.pdfeditor.screencompose.Navingation
import com.twoonethree.pdfeditor.screencompose.isStorageAccessable
import com.twoonethree.pdfeditor.utilities.FileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        var keepSplashOnScreen = true
        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            keepSplashOnScreen = false
            cancel()
        }
        super.onCreate(savedInstanceState)
        setContent {
            Navingation()
            AppDirectorySetup()
        }
    }
}