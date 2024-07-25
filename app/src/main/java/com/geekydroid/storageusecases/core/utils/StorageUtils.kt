package com.geekydroid.storageusecases.core.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObservable
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.geekydroid.storageusecases.BuildConfig
import com.geekydroid.storageusecases.core.utils.StorageUtils.registerObserver
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.pow

private const val TAG = "StorageUtils"

object StorageUtils {

    fun getAvailableInternalStorageInBytes(context: Context): Long {

        val storageManager = context.getSystemService<StorageManager>()!!
        val filesDir = context.filesDir
        val appSpecificInternalFilesDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long =
            storageManager.getAllocatableBytes(appSpecificInternalFilesDirUuid)
        return availableBytes
    }


    fun createNewInternalStorageTextFile(context: Context, fileContent: String) {
        val fileName = "file_${getFileTimeStamp()}.txt"
        val newFile = File(context.filesDir, fileName)
        if (!newFile.exists()) {
            val isCreated = newFile.createNewFile()
            if (isCreated) {
                Toast.makeText(context, "New File created ${newFile.name}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContent.toByteArray())
        }
    }

    fun getAllInternalFiles(context: Context): List<File> {
        val dir = context.filesDir
        if (dir.exists()) {
            return dir.listFiles()?.toList() ?: emptyList()
        }
        return emptyList()
    }

    fun getFileTimeStamp(): String {
        val localDate = LocalDateTime.now()
        return localDate.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    fun createNewTextFileInCachedStorage(context: Context, fileContent: String) {
        val fileName = "cached_file_${getFileTimeStamp()}"
        val newFile = File.createTempFile(fileName, ".txt", context.cacheDir)
        context.openFileOutput(newFile.name, Context.MODE_PRIVATE).use {
            it.write(fileContent.toByteArray())
        }
    }

    fun updateFileContent(context: Context, file: File, newContent: String) {
        context.openFileOutput(file.name, Context.MODE_PRIVATE).use {
            it.write(newContent.toByteArray())
        }
    }

    fun convertBytesToMb(bytes: Long): Long {
        return bytes / (10.0.pow(6)).toLong()
    }

    fun deleteFile(file: File): Boolean {
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    fun createImageFile(context: Context): Uri {
        val fileName = "image_${getFileTimeStamp()}"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        return FileProvider.getUriForFile(
            context.applicationContext,
            "${BuildConfig.APPLICATION_ID}.contentprovider",
            file
        )
    }

    fun getAllImageFiles(context: Context): List<File> {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dir?.exists() == true) {
            Log.d(TAG, "getAllImageFiles: ${dir.listFiles()?.size}")
            return dir.listFiles()?.toList() ?: emptyList()
        } else {
            Log.d(TAG, "getAllImageFiles: Directory doesn't exist")
        }
        return emptyList()
    }

    suspend fun fetchImagesFromSharedStorage(context: Context): List<MediaStoreImage> {
        val result = mutableListOf<MediaStoreImage>()
        withContext(Dispatchers.IO) {
            val collectionUri = isSdk29andUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            val query = context.contentResolver.query(
                collectionUri,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
                    val displayName = cursor.getString(displayNameColumn)

                    val contentUri = ContentUris.withAppendedId(collectionUri, id)
                    val image = MediaStoreImage(
                        id = id,
                        displayName = displayName,
                        dateAdded = dateAdded,
                        contentUri = contentUri
                    )
                    result += image
                }
            }
            Log.d(TAG, "fetchImagesFromSharedStorage: ${result.size}")
        }
        return result
    }

    inline fun <T> isSdk29andUp(onSdk29andUp: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onSdk29andUp()
        } else {
            null
        }
    }

    inline fun <T> isSdk33andUp(onSdk32andUp: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onSdk32andUp()
        } else {
            null
        }
    }

    data class PermissionResult(
        val allPermissionGranted: Boolean,
        val deniedPermission: Array<String>
    )

    fun checkIfPermissionsAreGranted(
        context: Context,
        permissions: List<String>
    ): PermissionResult {
        var allPermissionGranted = true
        val missingPermission = mutableListOf<String>()
        permissions.forEach { permission ->
            val result = ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
            if (!result) {
                allPermissionGranted = false
                missingPermission += permission
            }
        }
        val result = PermissionResult(
            allPermissionGranted = allPermissionGranted,
            deniedPermission = missingPermission.toTypedArray()
        )
        return result
    }

    fun ContentResolver.registerObserver(
        uri: Uri,
        observer: (selfChange: Boolean) -> Unit
    ): ContentObserver {
        val contentObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }
        }
        registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }

    fun renameSharedMedia(context: Context, mediaId: Long, contentUri: Uri, newName: String): Int {
        try {
            val contentResolver = context.contentResolver
            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(mediaId.toString())

            val updatedImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, newName)
            }
            val numImagesUpdated =
                contentResolver.update(contentUri, updatedImageDetails, selection, selectionArgs)
            return numImagesUpdated
        } catch (securityException: SecurityException) {
            throw securityException
        }
    }

    fun deletedSharedMediaFile(context: Context, mediaId:Long,contentUri: Uri) : Int {
        try {
            val contentResolver = context.contentResolver
            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(mediaId.toString())
            val numImageDeleted = contentResolver.delete(contentUri,selection,selectionArgs)
            return numImageDeleted
        } catch (securityException:SecurityException) {
            throw securityException
        }
    }

}