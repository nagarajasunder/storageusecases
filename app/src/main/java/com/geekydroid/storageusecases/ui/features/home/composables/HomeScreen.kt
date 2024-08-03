package com.geekydroid.storageusecases.ui.features.home.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

const val homeScreenRoute = "/home"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onInternalStorageTextFileClick: () -> Unit,
    onInternalStorageMediaFileClick: () -> Unit,
    onSharedStorageMediaClick: () -> Unit,
    onSharedStorageDocumentClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        LazyColumn {
            item {
                Card(
                    onClick = onInternalStorageTextFileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Internal Storage Text Files Operations",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            item {
                Card(
                    onClick = onInternalStorageMediaFileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Internal Storage Media Files Operations",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            item {
                Card(
                    onClick = onSharedStorageMediaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Shared Storage Media Operations",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    onClick = onSharedStorageDocumentClick
                ) {
                    Text(
                        "Shared Storage Document Operations",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

