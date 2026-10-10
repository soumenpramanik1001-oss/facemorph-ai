package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TemplateType
import com.example.ui.FaceMorphViewModel
import com.example.ui.ProcessingUiState
import com.example.ui.Screen
import com.example.ui.components.AppLanguage
import com.example.ui.components.FaceTuningControls
import com.example.ui.components.GlowButton
import com.example.ui.components.Strings
import com.example.ui.components.VideoMotionPlayer
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentPink
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoSwapScreen(
    viewModel: FaceMorphViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val sourceFaceBitmap by viewModel.sourceFaceBitmap.collectAsState()
    val selectedTemplate by viewModel.selectedTemplate.collectAsState()
    val settings by viewModel.swapSettings.collectAsState()
    val processingState by viewModel.processingState.collectAsState()

    BackHandler {
        if (processingState is ProcessingUiState.Success) {
            viewModel.resetProcessing()
        } else {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.setSourceFaceBitmap(bitmap)
            viewModel.saveFaceToVault("Selfie ${System.currentTimeMillis() % 1000}", bitmap)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSourceFaceUri(uri)
        }
    }

    val videoTemplates = viewModel.templates.filter { it.type == TemplateType.VIDEO }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (processingState is ProcessingUiState.Success) {
                            viewModel.resetProcessing()
                        } else {
                            viewModel.navigateTo(Screen.HOME)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .testTag("video_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "ভিডিও ফেস চেঞ্জ" else "Video Face Swap",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AccentPink.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("MOTION AI", color = AccentPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ভিডিও মোশনে মসৃণ ফেস ট্র্যাকিং ও সিন্থেসিস" else "Smooth head tracking & keyframe motion loops",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        when (val state = processingState) {
            is ProcessingUiState.Processing -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceCard),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = AccentPink,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = state.stepMessage,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "১২টি মোশন ফ্রেম রেন্ডার ও ফেস অ্যালাইনমেন্ট হচ্ছে..."
                            else
                                "Rendering 12 motion frames & face alignment...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            is ProcessingUiState.Success -> {
                // Video Motion Player View
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ভিডিও ফেস চেঞ্জ সম্পন্ন!" else "Video Face Swap Ready!",
                                    color = SuccessGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${state.videoFrames.size} Frames",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Custom High-Performance Animated Video Player
                        VideoMotionPlayer(
                            framePaths = state.videoFrames,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )

                        // Action Buttons: Share, Reset
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlowButton(
                                text = Strings.shareCreation(language),
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out my Video Face Swap created with FaceMorph AI!")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Video Face Swap"))
                                },
                                icon = Icons.Default.Share,
                                modifier = Modifier.weight(1f),
                                testTag = "share_video_button"
                            )

                            IconButton(
                                onClick = { viewModel.resetProcessing() },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                                    .testTag("reset_video_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Try another video", tint = NeonCyan)
                            }
                        }
                    }
                }
            }

            else -> {
                // Step 1: Select Face
                item {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "১. ভিডিওতে যে মুখটি বসাবেন" else "1. Face To Place In Video",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, AccentPink, RoundedCornerShape(16.dp))
                                .clickable { cameraLauncher.launch(null) }
                                .testTag("video_take_camera_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AccentPink, modifier = Modifier.size(28.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ক্যামেরা" else "Camera",
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                                .clickable {
                                    galleryLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }
                                .testTag("video_pick_gallery_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(28.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "গ্যালারি" else "Gallery",
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (sourceFaceBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(2.dp, AccentPink, RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    bitmap = sourceFaceBitmap!!.asImageBitmap(),
                                    contentDescription = "Selected face",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "👈 সেলফি তুলুন বা গ্যালারি থেকে ছবি নিন"
                                else
                                    "👈 Snap a selfie or select a face",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Step 2: Choose Video Template Scene
                item {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "২. ভিডিও সিন বা ক্লিপ বেছে নিন" else "2. Select Video Scene Loop",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(videoTemplates) { template ->
                            val isSelected = selectedTemplate.id == template.id
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) AccentPink else BorderGlow,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.selectTemplate(template) }
                                    .testTag("video_template_${template.id}")
                            ) {
                                AsyncImage(
                                    model = template.previewRes,
                                    contentDescription = template.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .align(Alignment.TopStart)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AccentPink, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("${template.durationSeconds}s", color = Color.White, fontSize = 9.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BENGALI) template.titleBn else template.title,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Step 3: Tuning controls
                item {
                    FaceTuningControls(
                        settings = settings,
                        onSettingsChanged = { viewModel.updateSettings(it) },
                        currentLanguage = language
                    )
                }

                if (state is ProcessingUiState.Error) {
                    item {
                        Text(
                            text = state.message,
                            color = ErrorRed,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // CTA Generate Video Button
                item {
                    GlowButton(
                        text = Strings.generateVideo(language),
                        onClick = { viewModel.startVideoSwap() },
                        enabled = sourceFaceBitmap != null,
                        icon = Icons.Default.Videocam,
                        testTag = "execute_video_swap_button"
                    )
                }
            }
        }
    }
}
