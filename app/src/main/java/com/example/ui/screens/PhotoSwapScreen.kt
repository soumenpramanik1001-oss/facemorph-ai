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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.TemplateType
import com.example.ui.FaceMorphViewModel
import com.example.ui.ProcessingUiState
import com.example.ui.Screen
import com.example.ui.components.AppLanguage
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.FaceTuningControls
import com.example.ui.components.GlowButton
import com.example.ui.components.Strings
import com.example.ui.theme.AccentGold
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
import java.io.File

@Composable
fun PhotoSwapScreen(
    viewModel: FaceMorphViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val sourceFaceBitmap by viewModel.sourceFaceBitmap.collectAsState()
    val selectedTemplate by viewModel.selectedTemplate.collectAsState()
    val settings by viewModel.swapSettings.collectAsState()
    val processingState by viewModel.processingState.collectAsState()

    // Android Back Handler returns to Home screen
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

    val photoTemplates = viewModel.templates.filter { it.type == TemplateType.PHOTO }

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
                        .testTag("photo_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "রিয়েল ফটো ফেস চেঞ্জ" else "Real Photo Face Swap",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == AppLanguage.BENGALI) "মুখের ত্বক ও আলো নিখুঁতভাবে মেলানো" else "Seamless facial contour & skin blending",
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
                            color = NeonCyan,
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
                                "অনুগ্রহ করে কিছুক্ষণ অপেক্ষা করুন..."
                            else
                                "Please wait a moment...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            is ProcessingUiState.Success -> {
                // Result View with interactive Before/After Slider!
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
                                    text = if (language == AppLanguage.BENGALI) "ফেস চেঞ্জ সফল হয়েছে!" else "Face Swap Complete!",
                                    color = SuccessGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "${state.result.processingTimeMs} ms",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Interactive Comparison Slider
                        BeforeAfterSlider(
                            beforeImagePath = state.result.originalFacePath,
                            afterImagePath = state.result.resultBitmapPath,
                            beforeLabel = if (language == AppLanguage.BENGALI) "আসল মুখ" else "Original Face",
                            afterLabel = if (language == AppLanguage.BENGALI) "ফেস চেঞ্জ" else "Morphed AI",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        )

                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "তুলনা করতে মাঝে ড্র্যাগ করুন ↔️"
                            else
                                "Drag the divider line left and right to compare before & after ↔️",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        // Action Buttons: Share, Try Another
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Share Button
                            GlowButton(
                                text = Strings.shareCreation(language),
                                onClick = {
                                    val file = File(state.result.resultBitmapPath)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Look at my AI Face Swap generated with FaceMorph AI!")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Face Swap"))
                                },
                                icon = Icons.Default.Share,
                                modifier = Modifier.weight(1f),
                                testTag = "share_result_button"
                            )

                            // Reset Button
                            IconButton(
                                onClick = { viewModel.resetProcessing() },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                                    .testTag("reset_result_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Try another", tint = NeonCyan)
                            }
                        }
                    }
                }
            }

            else -> {
                // Step 1: Source Face
                item {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "১. সেলফি বা মুখের ছবি দিন" else "1. Choose Your Face",
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
                        // Camera capture
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                            .clickable { cameraLauncher.launch(null) }
                            .testTag("photo_take_camera_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                                Text(
                                    text = if (language == AppLanguage.BENGALI) "ক্যামেরা" else "Camera",
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Gallery pick
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
                                .testTag("photo_pick_gallery_button"),
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

                        // Preview of chosen face
                        if (sourceFaceBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(2.dp, NeonCyan, RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    bitmap = sourceFaceBitmap!!.asImageBitmap(),
                                    contentDescription = "Selected source face",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "👈 সেলফি তুলুন বা গ্যালারি থেকে ছবি বেছে নিন"
                                else
                                    "👈 Snap a selfie or select a photo",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Step 2: Target Template
                item {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "২. টার্গেট স্টুডিও টেমপ্লেট" else "2. Select Target Template",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(photoTemplates) { template ->
                            val isSelected = selectedTemplate.id == template.id
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else BorderGlow,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.selectTemplate(template) }
                                    .testTag("photo_template_${template.id}")
                            ) {
                                AsyncImage(
                                    model = template.previewRes,
                                    contentDescription = template.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.7f))
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

                // Step 3: Face Tuning Controls
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

                // CTA Button
                item {
                    GlowButton(
                        text = Strings.swapNow(language),
                        onClick = { viewModel.startPhotoSwap() },
                        enabled = sourceFaceBitmap != null,
                        testTag = "execute_photo_swap_button"
                    )
                }
            }
        }
    }
}
