package com.geekydroid.storageusecases.ui.features.internalstoragefiles.actions

import com.geekydroid.storageusecases.core.application.StorageType
import java.io.File

interface InternalStorageActions {

    fun updateInternalStorageSpace(availableBytes:Long)
    fun onStorageTypeChange(storageType:StorageType)
    fun onCreateFileClick()
    fun onSaveFileClick(fileContent:String)
    fun onCreateFileDialogDismissed()
    fun updateInternalFiles(list:List<File>)
    fun onDeleteFileClick(file:File)
    fun onEditFileClick(file:File)
}