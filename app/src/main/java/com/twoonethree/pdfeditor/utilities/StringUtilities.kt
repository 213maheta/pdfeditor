package com.twoonethree.pdfeditor.utilities

object ScreenName {
    const val SPLASH_SCREEN = "splash_screen"
    const val SELECTION_SCREEN = "selection_screen"
    const val MERGE_PDF_SCREEN = "merge_pdf_screen"
    const val SPLIT_PDF_SCREEN = "split_pdf_screen"
    const val MY_CREATION_SCREEN = "my_creation_screen"
    const val PDF_VIEWER_SCREEN = "pdf_viewer_screen"
    const val ADD_PAGE_NUMBER_SCREEN = "add_page_number_screen"
    const val ROTATE_PDF_SCREEN = "rotate_pdf_screen"
    const val LOCK_PDF_SCREEN = "lock_pdf_screen"
    const val UNLOCK_PASSWORD_SCREEN = "unlock_password_screen"
    const val ORGANIZE_PDF_SCREEN = "organize_pdf_screen"
    const val IMAGE_TO_PDF_SCREEN = "image_to_pdf_screen"
    const val ADD_WATERMARK_SCREEN = "add_watermark_screen"
}

object StringUtilities {
    fun removeSlash(value:String): String {
        return value.replace("/", "`")
    }
    fun addSlash(value:String): String {
        return value.replace("`", "/")
    }
    fun removeExtention(value:String): String {
        return if(value.isNotEmpty()) value.substring(0, value.length - 4) else ""
    }

    fun addExtention(value:String): String
    {
        return "$value.pdf"
    }
}


