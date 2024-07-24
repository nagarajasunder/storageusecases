package com.geekydroid.storageusecases.ui.features.internalstoragefiles.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.storageusecases.core.application.StorageType
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.actions.InternalStorageActions
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.events.InternalStorageScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class InternalStorageState(
    val storageSpaceBytes: Long,
    val showCreateFileDialog: Boolean,
    val existingFileContent: String,
    val editFile: File?,
    val internalFiles: List<File>
) {
    companion object {
        val initialState = InternalStorageState(
            storageSpaceBytes = 0,
            showCreateFileDialog = false,
            existingFileContent = "",
            editFile = null,
            internalFiles = emptyList()
        )
    }
}

@HiltViewModel
class InternalStorageViewModel @Inject constructor() : ViewModel(), InternalStorageActions {

    private val _screenState: MutableStateFlow<InternalStorageState> =
        MutableStateFlow(InternalStorageState.initialState)
    val screenState: StateFlow<InternalStorageState> = _screenState

    private val eventChannel: Channel<InternalStorageScreenEvent> = Channel()
    val eventFlow = eventChannel.receiveAsFlow()


    override fun updateInternalStorageSpace(availableBytes: Long) {
        updateState(_screenState.value.copy(storageSpaceBytes = availableBytes))
    }


    override fun onStorageTypeChange(storageType: StorageType) {
        viewModelScope.launch {
            eventChannel.send(InternalStorageScreenEvent.QueryInternalStorageFreeSpace)
        }


    }

    override fun onCreateFileClick() {
        updateState(_screenState.value.copy(showCreateFileDialog = true))
    }

    override fun onSaveFileClick(fileContent: String) {
        viewModelScope.launch {
            updateState(_screenState.value.copy(showCreateFileDialog = false))
            if (_screenState.value.editFile == null) {
                eventChannel.send(
                    InternalStorageScreenEvent.CreateNewFileInInternalStorage(
                        fileContent
                    )
                )
            } else {
                eventChannel.send(
                    InternalStorageScreenEvent.UpdateFileContent(
                        _screenState.value.editFile!!,
                        fileContent
                    )
                )
                updateState(_screenState.value.copy(editFile = null, existingFileContent = ""))
            }
            eventChannel.send(InternalStorageScreenEvent.FetchInternalFiles)
        }
    }

    override fun onCreateFileDialogDismissed() {
        updateState(_screenState.value.copy(showCreateFileDialog = false))
    }

    override fun updateInternalFiles(list: List<File>) {
        updateState(_screenState.value.copy(internalFiles = list))
    }

    override fun onDeleteFileClick(file: File) {
        viewModelScope.launch {
            eventChannel.send(InternalStorageScreenEvent.DeleteInternalFile(file))
            eventChannel.send(InternalStorageScreenEvent.FetchInternalFiles)
        }
    }

    override fun onEditFileClick(file: File) {
        viewModelScope.launch {
            updateState(
                _screenState.value.copy(
                    showCreateFileDialog = true,
                    editFile = file,
                    existingFileContent = file.readText()
                )
            )
        }
    }


    private fun updateState(newState: InternalStorageState): InternalStorageState =
        _screenState.updateAndGet {
            newState
        }

}