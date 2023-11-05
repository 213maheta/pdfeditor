package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import com.twoonethree.pdfeditor.ui.theme.Blue
import com.twoonethree.pdfeditor.ui.theme.Green
import com.twoonethree.pdfeditor.ui.theme.Orange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrganizePdfViewModel() : ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, 0))
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val pageNumberList = mutableStateListOf<Int>()
    val showProgressBar = mutableStateOf(false)


    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun reOrderPdf(resolver:ContentResolver) = viewModelScope.launch(Dispatchers.Default)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return@launch
            }
            showProgressBar.value = true
            val isSuccess = PdfUtilities.reOrderPdf(
                resolver = resolver,
                uri = it,
                password = selectedPdf.value.password,
                pageNumberList.toList(),
            )
            when(isSuccess)
            {
                true -> setUiIntent(ScreenCommonEvents.ShowSnackBar("PDF reorder successfully", Green))
                false -> setUiIntent(ScreenCommonEvents.ShowSnackBar("Something gone wrong", Orange))
            }
            showProgressBar.value = false

        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowSnackBar("Select file first", Blue))
        }

    }

    fun setPageNumberList()
    {
        pageNumberList.clear()
        for(i in 1 until selectedPdf.value.totalPageNumber+1)
        {
            pageNumberList.add(i)
        }
    }

    fun removePage(value:Int)
    {
        pageNumberList.remove(value)
    }

    fun changePosition(index1: Int, index2: Int) {
        if (index2 >= 0 && index2 < pageNumberList.size) {

            val temp = pageNumberList[index1]
            pageNumberList[index1] = pageNumberList[index2]
            pageNumberList[index2] = temp
        }
    }

}