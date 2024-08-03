package com.geekydroid.storageusecases.ui.features.sharedstoragedocuments.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEditorField(
    modifier: Modifier = Modifier,
    initialValue:String = "",
    isReadOnly:Boolean,
    onSaveClick:(String) -> Unit
) {
    var value: String by remember {
        mutableStateOf(initialValue)
    }

    Column(modifier = modifier.padding(8.dp)) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.End),
            visible = !isReadOnly) {
            TextButton(
                onClick = { onSaveClick(value) }) {
                Text(text = "Save")
            }
        }
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            enabled = !isReadOnly,
            onValueChange = { newValue ->
                value = newValue
            },
            singleLine = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                )
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun DocumentEditorFieldPreview(modifier: Modifier = Modifier) {
    var value by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxSize()) {
        DocumentEditorField(
            initialValue = "Hello world",
            isReadOnly = true) {
            value = it
        }
    }
}

