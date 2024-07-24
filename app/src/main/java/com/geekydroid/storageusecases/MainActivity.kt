package com.geekydroid.storageusecases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geekydroid.storageusecases.ui.features.home.composables.HomeScreen
import com.geekydroid.storageusecases.ui.features.home.composables.homeScreenRoute
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.InternalStorageScreen
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.internalStorageRoute
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.composables.InternalStorageMedia
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.composables.internalStorageMedia
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.composables.SHARED_STORAGE_MEDIA_ROUTE
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.composables.SharedStorageMedia
import com.geekydroid.storageusecases.ui.theme.StorageusecasesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StorageusecasesTheme {
                ScreenContent()
            }
        }
    }
}

@Composable
fun ScreenContent() {

    val navController = rememberNavController()
    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = homeScreenRoute,
            builder = {
                composable(homeScreenRoute) {
                    HomeScreen(
                        onInternalStorageTextFileClick = {
                            navController.navigate(
                                internalStorageRoute
                            )
                        },
                        onInternalStorageMediaFileClick = {
                            navController.navigate(internalStorageMedia)
                        },
                        onSharedStorageMediaClick = {
                            navController.navigate(SHARED_STORAGE_MEDIA_ROUTE)
                        }
                    )
                }
                composable(internalStorageRoute) { navBackStackEntry ->
                    InternalStorageScreen(navBackStackEntry = navBackStackEntry)
                }
                composable(internalStorageMedia) { navBackStackEntry ->
                    InternalStorageMedia(currentNavBackStackEntry = navBackStackEntry)
                }
                composable(SHARED_STORAGE_MEDIA_ROUTE) {navBackStackEntry ->
                    SharedStorageMedia(navBackStackEntry = navBackStackEntry)
                }
            }
        )
    }
}