package com.twoonethree.pdfeditor

import com.twoonethree.pdfeditor.utilities.ScreenName

sealed class Destination(val node: String) {
    data object SplashScreen : Destination(node = ScreenName.SPLASH_SCREEN)
    data object SelectionScreen : Destination(node = ScreenName.SELECTION_SCREEN)
    data object MergePDFScreen : Destination(node = ScreenName.MERGE_PDF_SCREEN)
    data object SplitPDFScreen : Destination(node = ScreenName.SPLIT_PDF_SCREEN)
    data object MyCreationScreen : Destination(node = ScreenName.MY_CREATION_SCREEN)
    data object PdfViewerScreen : Destination(node = ScreenName.PDF_VIEWER_SCREEN)
    data object AddPageNumberScreen : Destination(node = ScreenName.ADD_PAGE_NUMBER_SCREEN)
    data object OrientationScreen : Destination(node = ScreenName.ORIENTATION_SCREEN)
    data object PasswordProtectionScreen : Destination(node = ScreenName.PASSWORD_PROTECTION_SCREEN)
}
