package com.geekydroid.storageusecases.ui.features.internalstoragemedia.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(modifier: Modifier = Modifier, file: File,onImageClick: (file:File) -> Unit,onLongClick:(file:File) -> Unit) {
    Card(modifier = modifier.size(180.dp).padding(4.dp)
        .combinedClickable(
            onClick = {onImageClick(file)},
            onLongClick = {onLongClick(file)}
        )) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(file).build(),
                contentDescription = null,
                imageLoader = ImageLoader(LocalContext.current),
                contentScale = ContentScale.Crop
            )
        }
    }
}