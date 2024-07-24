package com.geekydroid.storageusecases.ui.features.internalstoragemedia.composables

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.geekydroid.storageusecases.core.utils.StorageUtils
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.screenevents.InternalStorageMediaScreenEvents
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.viewmodels.InternalStorageMediaViewModel

const val internalStorageMedia = "/internalstorage/media"

@Composable
fun InternalStorageMedia(
    modifier: Modifier = Modifier,
    currentNavBackStackEntry: NavBackStackEntry
) {

    val context = LocalContext.current
    val viewModel: InternalStorageMediaViewModel = hiltViewModel(currentNavBackStackEntry)
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                viewModel.onCameraPermissionGranted()
            } else {
                viewModel.onCameraPermissionDenied()
            }
        }
    val imageCaptureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                viewModel.fetchAllImages()
            }
        }
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAllImages()
        viewModel.events.collect { event ->
            when (event) {
                is InternalStorageMediaScreenEvents.DeleteImageFile -> {
                    StorageUtils.deleteFile(event.file)
                    viewModel.fetchAllImages()
                }

                InternalStorageMediaScreenEvents.FetchAllImageFiles -> {
                    viewModel.updateAvailableImageFiles(StorageUtils.getAllImageFiles(context))
                }

                InternalStorageMediaScreenEvents.RequestCameraPermission -> {
                    Toast.makeText(context, "Requesting camera permission", Toast.LENGTH_SHORT)
                        .show()
                    if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.onCameraPermissionGranted()
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }

                InternalStorageMediaScreenEvents.CaptureImage -> {
                    Toast.makeText(context, "Launching camera", Toast.LENGTH_SHORT).show()
                    val imageUri = StorageUtils.createImageFile(context)
                    imageCaptureLauncher.launch(imageUri)
                }

            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCaptureImageClick) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
            }
        }
    ) { innerPadding ->
        if (screenState.showImageInfo) {
            ViewImageCard(image = screenState.selectedFile!!, onDismiss = viewModel::onImageDismiss)
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.padding(innerPadding),
        ) {
            items(screenState.mediaFiles.size) { index ->
                ImageCard(
                    file = screenState.mediaFiles[index],
                    onImageClick = viewModel::onImageClick,
                    onLongClick = viewModel::onDeleteImageClick
                )
            }
        }
    }
}

