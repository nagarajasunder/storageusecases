package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenactions

import android.database.ContentObserver
import android.net.Uri
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage

interface SharedStorageScreenActions {

    fun captureImageClick()
    fun onStoragePermissionGranted()
    fun updateImageUri(uris:List<MediaStoreImage>)
    fun fetchMediaStoreImages()
    fun onReadPermissionGranted()
    fun updateContentObserver(contentObserver: ContentObserver)
    fun onImageClick(mediaStoreImage: MediaStoreImage)
    fun onImageDismiss()
    fun onImageRename(newName: String)
    fun onImageRenamePermissionGranted()
}