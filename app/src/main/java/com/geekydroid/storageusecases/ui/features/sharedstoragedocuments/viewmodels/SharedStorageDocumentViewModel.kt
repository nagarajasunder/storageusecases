package com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.screenactions.SharedDocumentScreenActions
import com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.screenevents.SharedStorageDocumentScreenEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedStorageDocumentScreenState(
    val documentUri: Uri?,
    val documentContent: String,
    val isReadOnly: Boolean,
    val showEditDocumentContent: Boolean,
) {
    companion object {
        val initialState =
            SharedStorageDocumentScreenState(
                documentUri = null,
                documentContent = "",
                isReadOnly = false,
                showEditDocumentContent = false
            )
    }
}

@HiltViewModel
class SharedStorageDocumentViewModel @Inject constructor() : ViewModel(),
    SharedDocumentScreenActions {

    private val _screenState: MutableStateFlow<SharedStorageDocumentScreenState> =
        MutableStateFlow(SharedStorageDocumentScreenState.initialState)
    val screenState: StateFlow<SharedStorageDocumentScreenState> = _screenState

    private val eventChannel: Channel<SharedStorageDocumentScreenEvents> = Channel()
    val events = eventChannel.receiveAsFlow()

    override fun onCreateDocumentClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageDocumentScreenEvents.LaunchCreateDocumentPicker)
        }
    }

    override fun onViewDocumentClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageDocumentScreenEvents.LaunchViewDocumentPicker)
        }
    }

    override fun onEditDocumentClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageDocumentScreenEvents.LaunchEditDocumentPicker)
        }
    }

    override fun onDeleteDocumentClick() {
        viewModelScope.launch {
            eventChannel.send(SharedStorageDocumentScreenEvents.LaunchDeleteDocumentPicker)
        }
    }

    override fun updateDocumentContent(documentContent: String) {
        updateState(_screenState.value.copy(documentContent = documentContent))
    }

    override fun updateEditDocumentUri(uri: Uri) {
        updateState(_screenState.value.copy(documentUri = uri))
    }

    override fun onEditDocumentConfirmed() {
        viewModelScope.launch {
            eventChannel.send(
                SharedStorageDocumentScreenEvents.EditDocument(
                    _screenState.value.documentUri!!,
                    _screenState.value.documentContent
                )
            )
            updateState(
                _screenState.value.copy(
                    showEditDocumentContent = false,
                    documentContent = "",
                    documentUri = null
                )
            )
        }
    }

    override fun showEditDocumentContent() {
        updateState(_screenState.value.copy(showEditDocumentContent = true, isReadOnly = false))
    }

    override fun showDocument() {
        viewModelScope.launch {
            updateState(_screenState.value.copy(isReadOnly = true, showEditDocumentContent = true))
        }
    }


    private fun updateState(newState: SharedStorageDocumentScreenState): SharedStorageDocumentScreenState {
        return _screenState.updateAndGet {
            newState
        }
    }
}