package com.quickscanner.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quickscanner.app.ui.screens.DocumentViewerScreen
import com.quickscanner.app.ui.screens.HomeScreen
import com.quickscanner.app.ui.screens.IdCardScannerScreen
import com.quickscanner.app.ui.screens.OcrResultScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    onLaunchScanner: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onScanDocumentClick = onLaunchScanner,
                onScanIdCardClick = {
                    navController.navigate(Screen.IdCardScanner.route)
                },
                onOpenDocument = { docId ->
                    navController.navigate(Screen.DocumentViewer.createRoute(docId))
                }
            )
        }

        composable(
            route = Screen.DocumentViewer.route,
            arguments = listOf(navArgument("docId") { type = NavType.LongType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
            DocumentViewerScreen(
                docId = docId,
                onBack = { navController.popBackStack() },
                onOpenOcr = { id ->
                    navController.navigate(Screen.OcrResult.createRoute(id))
                }
            )
        }

        composable(Screen.IdCardScanner.route) {
            IdCardScannerScreen(
                onBack = { navController.popBackStack() },
                onComplete = { docId ->
                    navController.popBackStack()
                    navController.navigate(Screen.DocumentViewer.createRoute(docId))
                }
            )
        }

        composable(
            route = Screen.OcrResult.route,
            arguments = listOf(navArgument("docId") { type = NavType.LongType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
            OcrResultScreen(
                docId = docId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

