package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.composables

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import coil.compose.rememberAsyncImagePainter
import com.geekydroid.storageusecases.R
import com.geekydroid.storageusecases.core.utils.StorageUtils
import com.geekydroid.storageusecases.core.utils.StorageUtils.isSdk29andUp
import com.geekydroid.storageusecases.core.utils.StorageUtils.registerObserver
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenevents.SharedStorageScreenEvents
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.viewmodels.SharedStorageMediaViewModel
import okio.IOException

const val SHARED_STORAGE_MEDIA_ROUTE = "sharedstoragemedia"
private const val TAG = "SharedStorageMedia"

@SuppressLint("InlinedApi")
@Composable
fun SharedStorageMedia(modifier: Modifier = Modifier, navBackStackEntry: NavBackStackEntry) {

    val context = LocalContext.current
    val viewModel: SharedStorageMediaViewModel = hiltViewModel(navBackStackEntry)
    val screenState = viewModel.screenState.collectAsStateWithLifecycle()
    val takePhoto =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bmp ->
            if (bmp != null) {
                try {
                    if (storeImageInSharedStorage(context, bmp)) {
                        Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Unable to save image!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "SharedStorageMedia: ${e.message}")
                }
            }
        }

    val writePermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allPermissionGranted = true
            result.forEach { (_, result) ->
                if (!result) {
                    allPermissionGranted = false
                    Toast.makeText(
                        context,
                        "Please grant the required permission",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@forEach
                }
            }
            if (allPermissionGranted) {
                viewModel.onStoragePermissionGranted()
            }
        }

    val readPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                viewModel.onReadPermissionGranted()
            } else {
                Toast.makeText(
                    context,
                    "Please grant storage permission to fetch images!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                viewModel.onReadPermissionGranted()
            }
        }
    DisposableEffect(key1 = Unit) {
        if (viewModel.contentObserver == null) {
            val contentUri = isSdk29andUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val contentObserver = context.contentResolver.registerObserver(contentUri) {
                viewModel.fetchMediaStoreImages()
            }
            viewModel.updateContentObserver(contentObserver)
        }
        onDispose {
            viewModel.contentObserver?.let {
                context.contentResolver.unregisterContentObserver(it)
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        /**
         * We need to call this only for the first time, after that subsequent updates are made using
         * contentobserver callback callback
         */
        viewModel.fetchMediaStoreImages()
        viewModel.events.collect { event ->
            when (event) {
                SharedStorageScreenEvents.CaptureImage -> {
                    takePhoto.launch()
                }

                SharedStorageScreenEvents.RequestStoragePermission -> {
                    val result = isSdk29andUp {
                        StorageUtils.checkIfPermissionsAreGranted(context, listOf(Manifest.permission.CAMERA))
                    } ?: StorageUtils.checkIfPermissionsAreGranted(context, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA))
                    if (!result.allPermissionGranted) {
                        writePermissionLauncher.launch(result.deniedPermission)
                    } else {
                        takePhoto.launch()
                    }
                }

                SharedStorageScreenEvents.RequestReadPermission -> {
                    val permission = isSdk29andUp {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } ?: Manifest.permission.READ_EXTERNAL_STORAGE
                    val result = StorageUtils.checkIfPermissionsAreGranted(context, listOf(permission))
                    if (result.allPermissionGranted) {
                        viewModel.onReadPermissionGranted()
                    } else {
                        readPermissionLauncher.launch(permission)
                    }
                }

                SharedStorageScreenEvents.ReadMediaStoreImages -> {
                    viewModel.updateImageUri(StorageUtils.fetchImagesFromSharedStorage(context))
                }
            }
        }
    }

    Scaffold(modifier = modifier, floatingActionButton = {
        FloatingActionButton(onClick = viewModel::captureImageClick) {
            Icon(Icons.Default.CameraAlt, contentDescription = stringResource(id = R.string.camera))
        }
    }) { innerPadding ->
        LazyVerticalStaggeredGrid(
            modifier = modifier.padding(innerPadding),
            columns = StaggeredGridCells.Fixed(2)
        ) {
            items(screenState.value.images.size) { index ->
                val image = screenState.value.images[index]
                Card(modifier = Modifier.padding(4.dp)) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        painter = rememberAsyncImagePainter(model = image.contentUri),
                        contentDescription = stringResource(
                            id = R.string.image
                        ),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = image.displayName, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

private fun storeImageInSharedStorage(context: Context, imageBitmap: Bitmap): Boolean {
    val collectionUri = isSdk29andUp {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "suc_${StorageUtils.getFileTimeStamp()}.jpg")
        put(MediaStore.Images.Media.WIDTH, imageBitmap.width)
        put(MediaStore.Images.Media.HEIGHT, imageBitmap.height)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    val contentResolver = context.contentResolver
    return try {
        contentResolver.insert(collectionUri, contentValues)?.also { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                if (!imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
        } ?: throw IOException("Failed to create a Media store entry")
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}