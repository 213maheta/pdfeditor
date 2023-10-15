package com.twoonethree.pdfeditor.events

import com.itextpdf.kernel.pdf.PdfReader


sealed class ScreenCommonEvents{
    data class ShowToast(val message:String):ScreenCommonEvents()
    data object ShowPasswordDialog:ScreenCommonEvents()
    data class GotProtectedPdf(val pdfReaderOuter:PdfReader):ScreenCommonEvents()
    data class GotTotalPageNumber(val totalPageNumber: Int):ScreenCommonEvents()

    data class GotPassword(val totalPageNumber: Int):ScreenCommonEvents()

    data object EMPTY:ScreenCommonEvents()
}

sealed class AddPageNumberSelection(val corner:String){
    data object TOP_LEFT:AddPageNumberSelection("Top-Left")
    data object TOP_RIGHT:AddPageNumberSelection("Top-Right")
    data object BOTTOM_LEFT:AddPageNumberSelection("Bottom-Left")
    data object BOTTOM_RIGHT:AddPageNumberSelection("Bottom-Right")
    data object EMPTY:AddPageNumberSelection("Empty")
}
