package com.geekydroid.storageusecases.ui.features.internalstoragemedia.screenevents

import java.io.File

sealed class InternalStorageMediaScreenEvents {

    data object RequestCameraPermission : InternalStorageMediaScreenEvents()
    data object FetchAllImageFiles : InternalStorageMediaScreenEvents()
    data class DeleteImageFile(val file:File) : InternalStorageMediaScreenEvents()
    data object CaptureImage : InternalStorageMediaScreenEvents()
}