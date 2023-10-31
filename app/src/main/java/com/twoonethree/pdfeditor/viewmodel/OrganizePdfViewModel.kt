package com.twoonethree.pdfeditor.viewmodel

import android.content.ContentResolver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.twoonethree.pdfeditor.events.ScreenCommonEvents
import com.twoonethree.pdfeditor.model.PdfData
import com.twoonethree.pdfeditor.pdfutilities.PdfUtilities
import kotlinx.coroutines.flow.MutableStateFlow

class OrganizePdfViewModel() : ViewModel() {

    var selectedPdf = mutableStateOf(PdfData("", "" , null, 0))
    val uiIntent = MutableStateFlow<ScreenCommonEvents>(ScreenCommonEvents.EMPTY)
    val pageNumberList = mutableStateListOf<Int>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)

    fun setUiIntent(value: ScreenCommonEvents) {
        uiIntent.value = value
    }

    fun removeSelectedPdf(value:PdfData)
    {
        selectedPdf.value = PdfData("", "" , null,  0)
    }

    fun reOrderPdf(resolver:ContentResolver)
    {
        selectedPdf.value.uri?.let {
            if(selectedPdf.value.totalPageNumber == 0)
            {
                setUiIntent(ScreenCommonEvents.ShowPasswordDialog)
                return
            }
            PdfUtilities.reOrderPdf(
                resolver = resolver,
                uri = it,
                password = selectedPdf.value.password,
                pageNumberList.toList(),
                ::setUiIntent
            )
        }?: kotlin.run {
            setUiIntent(ScreenCommonEvents.ShowToast("Select file first"))
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