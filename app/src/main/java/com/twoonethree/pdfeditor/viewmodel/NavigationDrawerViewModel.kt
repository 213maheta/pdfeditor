package com.twoonethree.pdfeditor.viewmodel

import android.R
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.BuildConfig


class NavigationDrawerViewModel: ViewModel() {

    fun shareApp(): Intent? {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            type = "text/plain"
        }
        return Intent.createChooser(sendIntent, "Share through")

    }

}