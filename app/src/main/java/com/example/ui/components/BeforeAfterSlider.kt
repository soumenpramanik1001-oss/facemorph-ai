package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import java.io.File
import kotlin.math.roundToInt

@Composable
fun BeforeAfterSlider(
    beforeImagePath: String?,
    afterImagePath: String,
    modifier: Modifier = Modifier,
    beforeLabel: String = "Original Face",
    afterLabel: String = "Morphed AI"
) {
    var splitPosition by remember { mutableFloatStateOf(0.5f) }
    val density = LocalDensity.current

    val beforeBitmap = remember(beforeImagePath) {
        if (!beforeImagePath.isNullOrBlank() && File(beforeImagePath).exists()) {
            BitmapFactory.decodeFile(beforeImagePath)?.asImageBitmap()
        } else null
    }

    val afterBitmap = remember(afterImagePath) {
        if (File(afterImagePath).exists()) {
            BitmapFactory.decodeFile(afterImagePath)?.asImageBitmap()
        } else null
    }

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DeepObsidian)
            .testTag("before_after_slider")
    ) {
        val totalWidth = constraints.maxWidth.toFloat()
        val totalHeight = constraints.maxHeight.toFloat()

        // Canvas draws both layers with split clipping
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newPos = (splitPosition + dragAmount.x / totalWidth).coerceIn(0.05f, 0.95f)
                        splitPosition = newPos
                    }
                }
        ) {
            val dividerX = totalWidth * splitPosition

            // 1. Draw "After" (result) image as the base or right side
            afterBitmap?.let { bmp ->
                drawImage(
                    image = bmp,
                    dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                )
            }

            // 2. Draw "Before" (original) clipped to left of dividerX if present
            beforeBitmap?.let { bmp ->
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = dividerX,
                    bottom = size.height
                ) {
                    drawImage(
                        image = bmp,
                        dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                    )
                }
            }

            // 3. Draw vertical divider bar line
            drawLine(
                color = NeonCyan,
                start = Offset(dividerX, 0f),
                end = Offset(dividerX, size.height),
                strokeWidth = 3.dp.toPx()
            )
        }

        // Overlay Labels
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Before label (left)
            Text(
                text = beforeLabel,
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // After label (right)
            Text(
                text = afterLabel,
                color = NeonCyan,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepObsidian.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Draggable Center Grip Handle Button
        val handleOffsetX = with(density) { ((totalWidth * splitPosition) - 20.dp.toPx()).roundToInt() }
        val handleOffsetY = with(density) { (totalHeight / 2f - 20.dp.toPx()).roundToInt() }

        Box(
            modifier = Modifier
                .offset { IntOffset(handleOffsetX, handleOffsetY) }
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonCyan)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newPos = (splitPosition + dragAmount.x / totalWidth).coerceIn(0.05f, 0.95f)
                        splitPosition = newPos
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                contentDescription = "Slide to compare before and after",
                tint = DeepObsidian,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
