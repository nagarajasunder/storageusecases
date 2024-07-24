package com.geekydroid.storageusecases.ui.features.internalstoragefiles.screens.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun FileCard(modifier: Modifier = Modifier, file: File, onEditFileClick:(File) -> Unit, onDeleteFileClick: (File) -> Unit) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Path ${file.path}")
                Text(text = file.readText())
            }
            Column {
                IconButton(onClick = {
                    onEditFileClick(file)
                }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                IconButton(onClick = {
                    onDeleteFileClick(file)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}