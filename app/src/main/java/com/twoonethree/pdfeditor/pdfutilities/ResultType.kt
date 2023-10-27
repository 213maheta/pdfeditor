package com.twoonethree.pdfeditor.pdfutilities

sealed class ResultType()
{
    data object SUCCESS:ResultType()
    data object BADPASSWORD:ResultType()
    data class FAILURE(val message:String):ResultType()
}
