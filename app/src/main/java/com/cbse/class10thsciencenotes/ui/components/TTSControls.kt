package com.cbse.class10thsciencenotes.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TTSFloatingControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    speechRate: Float,
    onSpeechRateChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSpeedControl by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onStop,
                    modifier = Modifier.size(44.dp).background(Color(0xFFFF6B6B).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Stop, "Stop", tint = Color(0xFFFF6B6B), modifier = Modifier.size(22.dp))
                }

                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(56.dp).background(
                        Brush.horizontalGradient(listOf(Color(0xFF6C63FF), Color(0xFF8E84FF))),
                        CircleShape
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(
                    onClick = { showSpeedControl = !showSpeedControl },
                    modifier = Modifier.size(44.dp).background(Color(0xFF6C63FF).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Speed, "Speed", tint = Color(0xFF6C63FF), modifier = Modifier.size(22.dp))
                }
            }

            AnimatedVisibility(visible = showSpeedControl) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Text(
                        "Speed: ${String.format("%.1fx", speechRate)}",
                        fontSize = 11.sp,
                        color = Color(0xFF2D3436),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = onSpeechRateChange,
                        valueRange = 0.5f..2.0f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6C63FF),
                            activeTrackColor = Color(0xFF6C63FF),
                            inactiveTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0.5x", fontSize = 9.sp, color = Color.Gray)
                        Text("1.0x", fontSize = 9.sp, color = Color.Gray)
                        Text("2.0x", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

