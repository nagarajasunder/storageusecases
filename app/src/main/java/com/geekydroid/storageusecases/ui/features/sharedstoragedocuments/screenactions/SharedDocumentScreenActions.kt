package com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.screenactions

import android.net.Uri

interface SharedDocumentScreenActions {

    fun onCreateDocumentClick()
    fun onViewDocumentClick()
    fun onEditDocumentClick()
    fun onDeleteDocumentClick()
    fun updateDocumentContent(documentContent:String)
    fun updateEditDocumentUri(uri: Uri)
    fun onEditDocumentConfirmed()
    fun showEditDocumentContent()
    fun showDocument()
}