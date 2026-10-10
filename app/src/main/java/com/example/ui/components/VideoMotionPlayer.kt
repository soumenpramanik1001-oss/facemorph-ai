package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VideoMotionPlayer(
    framePaths: List<String>,
    modifier: Modifier = Modifier,
    initialFps: Int = 12
) {
    if (framePaths.isEmpty()) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Text("No video frames available", color = TextSecondary)
        }
        return
    }

    var isPlaying by remember { mutableStateOf(true) }
    var currentFrameIndex by remember { mutableIntStateOf(0) }
    var fps by remember { mutableFloatStateOf(initialFps.toFloat()) }
    var isPingPong by remember { mutableStateOf(true) }
    var directionForward by remember { mutableStateOf(true) }

    // Playback loop
    LaunchedEffect(isPlaying, fps, framePaths, isPingPong) {
        if (!isPlaying || framePaths.isEmpty()) return@LaunchedEffect
        val intervalMs = (1000f / fps.coerceIn(4f, 30f)).toLong()

        while (isPlaying) {
            delay(intervalMs)
            if (isPingPong) {
                if (directionForward) {
                    if (currentFrameIndex >= framePaths.size - 1) {
                        directionForward = false
                        currentFrameIndex = (framePaths.size - 2).coerceAtLeast(0)
                    } else {
                        currentFrameIndex++
                    }
                } else {
                    if (currentFrameIndex <= 0) {
                        directionForward = true
                        currentFrameIndex = 1.coerceAtMost(framePaths.size - 1)
                    } else {
                        currentFrameIndex--
                    }
                }
            } else {
                currentFrameIndex = (currentFrameIndex + 1) % framePaths.size
            }
        }
    }

    val currentBitmap = remember(currentFrameIndex, framePaths) {
        val path = framePaths.getOrNull(currentFrameIndex)
        if (path != null && File(path).exists()) {
            BitmapFactory.decodeFile(path)?.asImageBitmap()
        } else null
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .padding(12.dp)
            .testTag("video_motion_player")
    ) {
        // Video Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(DeepObsidian),
            contentAlignment = Alignment.Center
        ) {
            if (currentBitmap != null) {
                Image(
                    bitmap = currentBitmap,
                    contentDescription = "Video frame $currentFrameIndex",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Top Video Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MOTION LOOP",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                Text(
                    text = "Frame ${currentFrameIndex + 1}/${framePaths.size}",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrub Bar
        Slider(
            value = currentFrameIndex.toFloat(),
            onValueChange = {
                isPlaying = false
                currentFrameIndex = it.toInt().coerceIn(0, framePaths.size - 1)
            },
            valueRange = 0f..(framePaths.size - 1).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = DeepObsidian
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .testTag("video_scrub_slider")
        )

        // Player Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Play / Pause
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                        .testTag("video_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause video" else "Play video",
                        tint = DeepObsidian
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Ping-Pong / Repeat Mode
                IconButton(
                    onClick = { isPingPong = !isPingPong },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPingPong) NeonPurple.copy(alpha = 0.3f) else DeepObsidian)
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "Toggle Ping-Pong loop",
                        tint = if (isPingPong) NeonCyan else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Speed / FPS Indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${fps.toInt()} FPS",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )

                IconButton(
                    onClick = {
                        fps = when {
                            fps <= 8f -> 12f
                            fps <= 14f -> 20f
                            else -> 6f
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Change playback speed",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
