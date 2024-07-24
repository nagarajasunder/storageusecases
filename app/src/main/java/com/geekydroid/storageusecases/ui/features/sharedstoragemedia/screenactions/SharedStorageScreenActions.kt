package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenactions

import android.database.ContentObserver
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage

interface SharedStorageScreenActions {

    fun captureImageClick()
    fun onStoragePermissionGranted()
    fun updateImageUri(uris:List<MediaStoreImage>)
    fun fetchMediaStoreImages()
    fun onReadPermissionGranted()
    fun updateContentObserver(contentObserver: ContentObserver)
}