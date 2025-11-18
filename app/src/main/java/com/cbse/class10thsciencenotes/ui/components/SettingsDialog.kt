package com.cbse.class10thsciencenotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SettingsDialog(
    initialFontSize: Float,
    onDismiss: () -> Unit,
    onFontSizeChange: (Float) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(initialFontSize) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Adjust Text Size",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Shows a sample of the text size
                Text(
                    text = "Aa",
                    fontSize = sliderPosition.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 15f..21f, // From 12sp to 20sp // 8 possible sizes (12, 13, 14...20)
                    onValueChangeFinished = {
                        onFontSizeChange(sliderPosition)
                    }
                )
            }
        }
    }
}