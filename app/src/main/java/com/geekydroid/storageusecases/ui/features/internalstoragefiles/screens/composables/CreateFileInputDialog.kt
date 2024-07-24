package com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CreateFileInputDialogContent(
    modifier: Modifier = Modifier,
    fileContent: String,
    onFileSave: (String) -> Unit
) {

    var fileContent by remember {
        mutableStateOf(fileContent)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create New File")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = fileContent, onValueChange = {
                fileContent = it
            })
        Button(onClick = { onFileSave(fileContent) }, enabled = fileContent.trim().isNotEmpty()) {
            Text(text = "Save")
        }
    }
}