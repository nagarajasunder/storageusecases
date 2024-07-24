package com.geekydroid.storageusecases.ui.features.internalstoragemedia.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.geekydroid.storageusecases.R
import java.io.File

@Composable
fun ViewImageCard(modifier: Modifier = Modifier,image:File,onDismiss: () -> Unit) {

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier.fillMaxWidth()) {
            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    model = ImageRequest.Builder(LocalContext.current).data(image).build(),
                    contentDescription = stringResource(id = R.string.image_info),
                    imageLoader = ImageLoader(LocalContext.current),
                    contentScale = ContentScale.Crop
                )
                Text(text = image.path,modifier = Modifier.padding(8.dp))
            }
        }
    }
}