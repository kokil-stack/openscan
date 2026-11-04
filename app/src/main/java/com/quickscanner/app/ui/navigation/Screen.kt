package com.quickscanner.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DocumentViewer : Screen("viewer/{docId}") {
        fun createRoute(docId: Long) = "viewer/$docId"
    }
    object IdCardScanner : Screen("id_card_scanner")
    object OcrResult : Screen("ocr/{docId}") {
        fun createRoute(docId: Long) = "ocr/$docId"
    }
}
