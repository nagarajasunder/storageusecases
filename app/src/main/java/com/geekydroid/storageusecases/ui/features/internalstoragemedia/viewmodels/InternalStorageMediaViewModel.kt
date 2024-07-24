package com.geekydroid.storageusecases.ui.features.internalstoragemedia.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.screenactions.InternalStorageMediaScreenActions
import com.geekydroid.storageusecases.ui.features.internalstoragemedia.screenevents.InternalStorageMediaScreenEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "InternalStorageMediaVie"

data class InternalStorageMediaScreenState(
    val mediaFiles: List<File>,
    val selectedFile: File?,
    val showImageInfo: Boolean
) {
    companion object {
        val initialState =
            InternalStorageMediaScreenState(
                mediaFiles = listOf(),
                selectedFile = null,
                showImageInfo = false
            )
    }
}

@HiltViewModel
class InternalStorageMediaViewModel @Inject constructor() : ViewModel(),
    InternalStorageMediaScreenActions {

    private val eventChannel: Channel<InternalStorageMediaScreenEvents> = Channel()
    val events = eventChannel.receiveAsFlow()
        .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000))

    private val _screenState: MutableStateFlow<InternalStorageMediaScreenState> =
        MutableStateFlow(InternalStorageMediaScreenState.initialState)
    val screenState: StateFlow<InternalStorageMediaScreenState> = _screenState

    override fun onCaptureImageClick() {
        viewModelScope.launch {
            eventChannel.send(InternalStorageMediaScreenEvents.RequestCameraPermission)
        }
    }

    override fun onDeleteImageClick(file: File) {
        viewModelScope.launch {
            eventChannel.send(InternalStorageMediaScreenEvents.DeleteImageFile(file))
        }
    }

    override fun onCameraPermissionGranted() {
        viewModelScope.launch {
            eventChannel.send(InternalStorageMediaScreenEvents.CaptureImage)
        }
    }

    override fun onCameraPermissionDenied() {

    }

    override fun updateAvailableImageFiles(files: List<File>) {
        files.forEach {
            Log.d(TAG, "updateAvailableImageFiles: ${it.path}")
        }
        updateState(_screenState.value.copy(mediaFiles = files))
    }

    override fun onImageClick(file: File) {
        updateState(_screenState.value.copy(selectedFile = file, showImageInfo = true))
    }

    override fun onImageDismiss() {
        updateState(_screenState.value.copy(selectedFile = null, showImageInfo = false))
    }

    fun fetchAllImages() {
        viewModelScope.launch {
            eventChannel.send(InternalStorageMediaScreenEvents.FetchAllImageFiles)
        }
    }

    private fun updateState(newState: InternalStorageMediaScreenState): InternalStorageMediaScreenState {
        return _screenState.updateAndGet {
            newState
        }
    }

}