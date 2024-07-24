package com.geekydroid.storageusecases.ui.features.internalstoragemedia.screenactions

import java.io.File

interface InternalStorageMediaScreenActions {

    fun onCaptureImageClick()
    fun onDeleteImageClick(file:File)
    fun onCameraPermissionGranted()
    fun onCameraPermissionDenied()
    fun updateAvailableImageFiles(files:List<File>)
    fun onImageClick(file:File)
    fun onImageDismiss()
}