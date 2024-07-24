package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.viewmodels

import android.database.ContentObserver
import android.net.Uri
import androidx.compose.ui.graphics.ShaderBrush
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.storageusecases.core.utils.StorageUtils
import com.geekydroid.storageusecases.core.utils.StorageUtils.isSdk29andUp
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.composables.SharedStorageMedia
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenactions.SharedStorageScreenActions
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.screenevents.SharedStorageScreenEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedMediaState(
    val images: List<MediaStoreImage>
) {
    companion object {
        val initialState = SharedMediaState(images = emptyList())
    }
}

@HiltViewModel
class SharedStorageMediaViewModel @Inject constructor() : ViewModel(), SharedStorageScreenActions {

    private val eventChannel: Channel<SharedStorageScreenEvents> = Channel()
    val events = eventChannel.receiveAsFlow()
    var contentObserver:ContentObserver? = null
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

    private fun updateState(newState: SharedMediaState): SharedMediaState {
        return _screenState.updateAndGet {
            newState
        }
    }


}