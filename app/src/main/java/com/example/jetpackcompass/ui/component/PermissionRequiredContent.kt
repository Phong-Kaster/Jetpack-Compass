package com.example.jetpackcompass.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PermissionRequiredContent(
    enable: Boolean = true,
) {
    if (!enable) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {

        Text(
            text = "Location permission is required to use the compass",
            modifier = Modifier
                .padding(16.dp),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }

}


@Preview
@Composable
private fun PermissionRequiredContentPreview() {
    MaterialTheme {
        Surface {
            PermissionRequiredContent()
        }
    }
}

