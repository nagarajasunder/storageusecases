package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.viewmodels

import android.database.ContentObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenactions.SharedStorageScreenActions
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenevents.SharedStorageScreenEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedMediaState(
    val images: List<MediaStoreImage>,
    val showImageDialog: Boolean,
    val selectedFile: MediaStoreImage?
) {
    companion object {
        val initialState =
            SharedMediaState(images = emptyList(), showImageDialog = false, selectedFile = null)
    }
}

@HiltViewModel
class SharedStorageMediaViewModel @Inject constructor() : ViewModel(), SharedStorageScreenActions {

    private val eventChannel: Channel<SharedStorageScreenEvents> = Channel()
    val events = eventChannel.receiveAsFlow()
    var contentObserver: ContentObserver? = null
        private set

    private val _screenState: MutableStateFlow<SharedMediaState> =
        MutableStateFlow(SharedMediaState.initialState)
    val screenState: StateFlow<SharedMediaState> = _screenState

    override fun captureImageClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.RequestStoragePermission)
        }
    }

    override fun onStoragePermissionGranted() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.CaptureImage)
        }
    }


    override fun updateImageUri(uris: List<MediaStoreImage>) {
        updateState(_screenState.value.copy(images = uris))
    }

    override fun fetchMediaStoreImages() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.RequestReadPermission)
        }
    }

    override fun onReadPermissionGranted() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.ReadMediaStoreImages)
        }
    }

    override fun updateContentObserver(contentObserver: ContentObserver) {
        this.contentObserver = contentObserver
    }

    override fun onImageClick(mediaStoreImage: MediaStoreImage) {
        updateState(_screenState.value.copy(showImageDialog = true, selectedFile = mediaStoreImage))
    }

    override fun onImageDismiss() {
        updateState(_screenState.value.copy(showImageDialog = false))
    }

    override fun onImageRename(newName: String) {
        viewModelScope.launch {
            val updatedFile = screenState.value.selectedFile!!.copy(displayName = newName)
            eventChannel.send(
                SharedStorageScreenEvents.RenameImage(
                    updatedFile,
                    newName
                )
            )
            updateState(_screenState.value.copy(selectedFile = updatedFile, showImageDialog = false))
        }
    }

    /**
     * This is not the ideal way, but since the intentSenderRequest's result always returns a null intent I have implemented this way
     */

    override fun onImageRenamePermissionGranted() {
        viewModelScope.launch {
            eventChannel.send(
                SharedStorageScreenEvents.RenameImage(
                    screenState.value.selectedFile!!,
                    screenState.value.selectedFile!!.displayName
                )
            )
            updateState(_screenState.value.copy(selectedFile = null))
        }
    }

    override fun onDeleteImageClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.DeleteImage(_screenState.value.selectedFile!!))
            updateState(_screenState.value.copy(showImageDialog = false))
        }
    }

    override fun onDeletePermissionGranted() {
       viewModelScope.launch {
           eventChannel.send(SharedStorageScreenEvents.DeleteImage(_screenState.value.selectedFile!!))
           updateState(_screenState.value.copy(selectedFile = null))
       }
    }

    override fun onRenamePermissionGrantedForSdkBelow28() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageScreenEvents.RenameImage(_screenState.value.selectedFile!!,_screenState.value.selectedFile!!.displayName))
            updateState(_screenState.value.copy(selectedFile = null))
        }
    }


    private fun updateState(newState: SharedMediaState): SharedMediaState {
        return _screenState.updateAndGet {
            newState
        }
    }


}