package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenevents

import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage

sealed class SharedStorageScreenEvents {

    data object CaptureImage : SharedStorageScreenEvents()
    data object RequestStoragePermission : SharedStorageScreenEvents()
    data object RequestReadPermission : SharedStorageScreenEvents()
    data object ReadMediaStoreImages : SharedStorageScreenEvents()
    data class RenameImage(val image:MediaStoreImage,val newName:String) : SharedStorageScreenEvents()
    data class DeleteImage(val image:MediaStoreImage) : SharedStorageScreenEvents()
}