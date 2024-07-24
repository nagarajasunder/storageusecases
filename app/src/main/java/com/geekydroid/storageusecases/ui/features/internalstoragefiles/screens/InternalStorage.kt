package com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.geekydroid.storageusecases.core.application.StorageType
import com.geekydroid.storageusecases.core.utils.StorageUtils
import com.geekydroid.storageusecases.core.utils.StorageUtils.convertBytesToMb
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.events.InternalStorageScreenEvent
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.composables.CreateFileInputDialogContent
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.composables.FileCard
import com.geekydroid.storageusecases.ui.features.internalstoragefiles.viewmodels.InternalStorageViewModel

const val internalStorageRoute = "/internalstorage/files"

@Composable
fun InternalStorageScreen(modifier: Modifier = Modifier, navBackStackEntry: NavBackStackEntry) {
    val viewModel: InternalStorageViewModel = hiltViewModel(viewModelStoreOwner = navBackStackEntry)
    Column(modifier = modifier) {
        InternalStorageScreenContent(viewModel = viewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalStorageScreenContent(
    modifier: Modifier = Modifier,
    viewModel: InternalStorageViewModel
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.updateInternalFiles(StorageUtils.getAllInternalFiles(context))
        viewModel.eventFlow.collect { event ->
            when (event) {
                InternalStorageScreenEvent.QueryInternalStorageFreeSpace -> {
                    val availableSpace = StorageUtils.getAvailableInternalStorageInBytes(context)
                    viewModel.updateInternalStorageSpace(availableSpace)
                }


                is InternalStorageScreenEvent.CreateNewFileInInternalStorage -> {
                    StorageUtils.createNewInternalStorageTextFile(context, event.fileContent)
                }

                is InternalStorageScreenEvent.CreateNewFileInCachedStorage -> {
                    StorageUtils.createNewTextFileInCachedStorage(context, event.fileContent)
                }

                InternalStorageScreenEvent.FetchInternalFiles -> {
                    viewModel.updateInternalFiles(StorageUtils.getAllInternalFiles(context))
                }

                is InternalStorageScreenEvent.DeleteInternalFile -> {
                    if (StorageUtils.deleteFile(event.file)) {
                        Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Unable to delete file", Toast.LENGTH_SHORT).show()
                    }
                }

                is InternalStorageScreenEvent.UpdateFileContent -> {
                    StorageUtils.updateFileContent(context,event.editFile,event.fileContent)
                }
            }
        }
    }
    Scaffold(modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCreateFileClick) {
                Image(Icons.Default.Add, contentDescription = "add")
            }
        }) { innerPadding ->

        if (screenState.showCreateFileDialog) {
            ModalBottomSheet(onDismissRequest = viewModel::onCreateFileDialogDismissed) {
                CreateFileInputDialogContent(onFileSave = viewModel::onSaveFileClick,fileContent = screenState.existingFileContent)
            }
        }
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                Button(onClick = {
                    viewModel.onStorageTypeChange(StorageType.STORAGE_TYPE_INTERNAL)
                }) {
                    Text(text = "Internal Storage")
                }
            }
            availableStorageCard(
                availableStorageBytes = screenState.storageSpaceBytes
            )
            items(screenState.internalFiles.size, key = { index ->
                screenState.internalFiles[index].name
            }) { index ->
                FileCard(
                    file = screenState.internalFiles[index],
                    onDeleteFileClick = viewModel::onDeleteFileClick,
                    onEditFileClick = viewModel::onEditFileClick
                )
            }
        }
    }
}


fun LazyListScope.availableStorageCard(
    modifier: Modifier = Modifier,
    availableStorageBytes: Long,
) {
    item {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = "Available Internal Storage - ${convertBytesToMb(availableStorageBytes)} Mbs")
            }
        }
    }
}