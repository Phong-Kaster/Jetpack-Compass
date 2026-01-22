package com.example.jetpackcompass.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ButtonDefaults

@Composable
fun GpsDisabledContent(
    enable: Boolean = true,
    onOpenLocationSettings: () -> Unit
) {
    if (!enable) return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Please enable GPS",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onOpenLocationSettings,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3), // Blue
                contentColor = Color.White
            )
        ) {
            Text(text = "Open Settings")
        }
    }
}

@Preview
@Composable
private fun GpsDisabledContentPreview() {
    GpsDisabledContent(
        onOpenLocationSettings = {}
    )
}
