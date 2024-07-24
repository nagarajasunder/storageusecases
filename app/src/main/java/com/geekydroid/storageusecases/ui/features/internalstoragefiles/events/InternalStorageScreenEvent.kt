package com.geekydroid.storageusecases.ui.features.internalstoragefiles.events

import java.io.File

sealed class InternalStorageScreenEvent {

    data object QueryInternalStorageFreeSpace : InternalStorageScreenEvent()
    data class CreateNewFileInInternalStorage(val fileContent:String) : InternalStorageScreenEvent()
    data class UpdateFileContent(val editFile:File,val fileContent:String) : InternalStorageScreenEvent()
    data class CreateNewFileInCachedStorage(val fileContent:String) : InternalStorageScreenEvent()
    data object FetchInternalFiles:InternalStorageScreenEvent()
    data class DeleteInternalFile(val file:File): InternalStorageScreenEvent()
}