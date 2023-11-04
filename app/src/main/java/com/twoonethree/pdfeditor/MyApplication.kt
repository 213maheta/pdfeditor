package com.twoonethree.pdfeditor

import android.app.Application
import com.twoonethree.pdfeditor.utilities.CachedManager

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        CachedManager.init(context = this)
    }
}