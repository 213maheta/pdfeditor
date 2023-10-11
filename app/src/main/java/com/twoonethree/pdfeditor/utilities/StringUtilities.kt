package com.twoonethree.pdfeditor.utilities

object ScreenName {
    const val SPLASH_SCREEN = "splash_screen"
    const val SELECTION_SCREEN = "selection_screen"
    const val MERGE_PDF_SCREEN = "merge_pdf_screen"
    const val SPLIT_PDF_SCREEN = "split_pdf_screen"
    const val MY_CREATION_SCREEN = "my_creation_screen"
    const val PDF_VIEWER_SCREEN = "pdf_viewer_screen"
    const val ADD_PAGE_NUMBER_SCREEN = "add_page_number_screen"
    const val ORIENTATION_SCREEN = "orientation_screen"
    const val PASSWORD_PROTECTION_SCREEN = "password_protection_screen"
    const val UNLOCK_PASSWORD_SCREEN = "unlock_password_screen"
    const val PASSWORD_DIALOG_SCREEN = "password_dialog_screen"
}

object StringUtilities {

    fun removeSlash(value:String): String {
        return value.replace("/", "`")
    }

    fun addSlash(value:String): String {
        return value.replace("`", "/")
    }
}
