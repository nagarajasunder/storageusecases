package com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.screenevents

import android.net.Uri

sealed class SharedStorageDocumentScreenEvents {

    data object LaunchCreateDocumentPicker : SharedStorageDocumentScreenEvents()
    data object LaunchDeleteDocumentPicker : SharedStorageDocumentScreenEvents()
    data object LaunchEditDocumentPicker : SharedStorageDocumentScreenEvents()
    data class EditDocument(val documentUri: Uri, val documentContent: String) : SharedStorageDocumentScreenEvents()
    data object LaunchViewDocumentPicker : SharedStorageDocumentScreenEvents()
}