package com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.composables

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geekydroid.storageusecases.core.utils.StorageUtils
import com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.screenevents.SharedStorageDocumentScreenEvents
import com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.viewmodels.SharedStorageDocumentViewModel

const val sharedStorageDocumentScreenRoute = "sharedstoragedocuments"

@Composable
fun SharedStorageDocumentScreen() {
    val viewModel: SharedStorageDocumentViewModel = hiltViewModel()
    SharedStorageDocumentScreenContent(viewModel = viewModel)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharedStorageDocumentScreenContent(
    modifier: Modifier = Modifier,
    viewModel: SharedStorageDocumentViewModel
) {
    val context = LocalContext.current
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    var activityResultLauncherCallback by remember {
        mutableStateOf<((Uri?) -> Unit)>({})
    }
    val createDocumentLauncher =
        rememberLauncherForActivityResult(contract = CreateDocument("text/plain")) { fileUri ->
            activityResultLauncherCallback(fileUri)
            activityResultLauncherCallback = {}
        }
    val documentOperationLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { fileUri ->
            activityResultLauncherCallback(fileUri)
        }
    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SharedStorageDocumentScreenEvents.LaunchCreateDocumentPicker -> {
                    activityResultLauncherCallback = { fileUri ->
                        if (fileUri != null) {
                            StorageUtils.createOrEditTextDocument(
                                context,
                                fileUri,
                                content = "Created at ${System.currentTimeMillis()}"
                            )
                            Toast.makeText(context, "File created successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to create new file. Uri is null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    createDocumentLauncher.launch(StorageUtils.getDocumentName(".txt"))
                }

                SharedStorageDocumentScreenEvents.LaunchDeleteDocumentPicker -> {
                    activityResultLauncherCallback = { fileUri ->
                        if (fileUri != null) {
                            StorageUtils.deleteDocument(context, fileUri)
                            Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to delete file. Uri is null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    documentOperationLauncher.launch(arrayOf("text/plain"))

                }

                is SharedStorageDocumentScreenEvents.EditDocument -> {
                    StorageUtils.createOrEditTextDocument(
                        context,
                        event.documentUri,
                        event.documentContent
                    )
                }

                SharedStorageDocumentScreenEvents.LaunchEditDocumentPicker -> {
                    activityResultLauncherCallback = { fileUri ->
                        if (fileUri != null) {
                            val documentContent = StorageUtils.getDocumentContent(context, fileUri)
                            viewModel.updateEditDocumentUri(fileUri)
                            viewModel.updateDocumentContent(documentContent)
                            viewModel.showEditDocumentContent()
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to edit document. URI is null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    documentOperationLauncher.launch(arrayOf("text/plain"))
                }

                SharedStorageDocumentScreenEvents.LaunchViewDocumentPicker -> {
                    activityResultLauncherCallback = { fileUri ->
                        if (fileUri != null) {
                            val documentContent = StorageUtils.getDocumentContent(context, fileUri)
                            viewModel.updateDocumentContent(documentContent)
                            viewModel.showDocument()
                        } else {
                            Toast.makeText(
                                context,
                                "Unable to get content from the document",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    documentOperationLauncher.launch(arrayOf("text/plain"))
                }
            }
        }
    }
    LazyColumn(modifier = modifier.padding(top = 24.dp, start = 16.dp)) {
        item {
            Text(
                text = "Shared Storage Document Operations",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::onCreateDocumentClick) {
                    Text(text = "Create New Document")
                }
                Button(onClick = viewModel::onViewDocumentClick) {
                    Text(text = "View Document")
                }
                Button(onClick = viewModel::onEditDocumentClick) {
                    Text(text = "Edit Document")
                }
                Button(onClick = viewModel::onDeleteDocumentClick) {
                    Text(text = "Delete Document")
                }

            }
        }
        item {
            AnimatedVisibility(
                visible = screenState.showEditDocumentContent,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                DocumentEditorField(
                    initialValue = screenState.documentContent,
                    isReadOnly = screenState.isReadOnly
                ) { documentContent ->
                    viewModel.updateDocumentContent(documentContent)
                    viewModel.onEditDocumentConfirmed()
                }
            }
        }
    }
}