package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models

import android.net.Uri
import java.util.Date

data class MediaStoreImage (
    val id: Long,
    val displayName: String,
    val dateAdded: Date,
    val contentUri: Uri
)