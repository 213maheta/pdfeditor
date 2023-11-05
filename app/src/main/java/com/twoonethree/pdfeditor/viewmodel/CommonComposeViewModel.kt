package com.twoonethree.pdfeditor.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange

class CommonComposeViewModel: ViewModel() {

    val message = mutableStateOf("")
    var status = Green

}