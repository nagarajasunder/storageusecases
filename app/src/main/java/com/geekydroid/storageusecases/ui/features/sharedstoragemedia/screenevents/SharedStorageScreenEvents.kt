package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenevents

sealed class SharedStorageScreenEvents {

    data object CaptureImage : SharedStorageScreenEvents()
    data object RequestStoragePermission : SharedStorageScreenEvents()
    data object RequestReadPermission : SharedStorageScreenEvents()
    data object ReadMediaStoreImages : SharedStorageScreenEvents()
}