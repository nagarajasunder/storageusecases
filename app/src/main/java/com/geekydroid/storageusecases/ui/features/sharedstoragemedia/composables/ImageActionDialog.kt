package com.geekydroid.storageusecases.ui.features.sharedstoragemedia.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.geekydroid.storageusecases.R
import com.geekydroid.storageusecases.ui.features.sharedstoragemedia.models.MediaStoreImage

@Composable
fun ImageActionDialog(
    modifier: Modifier = Modifier,
    mediaStoreImage: MediaStoreImage,
    onDismiss:() -> Unit,
    onRenameClick: (String) -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        var imageName by remember {
            mutableStateOf(mediaStoreImage.displayName)
        }

        Card(
            modifier = modifier,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                painter = rememberAsyncImagePainter(model = mediaStoreImage.contentUri),
                contentDescription = stringResource(
                    id = R.string.image
                ),
                contentScale = ContentScale.Crop
            )
            TextField(value = imageName, onValueChange = { newValue ->
                imageName = newValue
            })
            TextButton(onClick = { onRenameClick(imageName) }, enabled = imageName.isNotEmpty()) {
                Text(text = stringResource(id = R.string.rename))
            }
        }
    }
}