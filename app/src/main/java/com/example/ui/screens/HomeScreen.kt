package com.example.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.TemplateCategory
import com.example.data.model.TemplateType
import com.example.ui.FaceMorphViewModel
import com.example.ui.Screen
import com.example.ui.components.AppLanguage
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.GlowButton
import com.example.ui.components.Strings
import com.example.ui.components.TemplateCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentPink
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: FaceMorphViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTemplate by viewModel.selectedTemplate.collectAsState()
    val sourceFaceBitmap by viewModel.sourceFaceBitmap.collectAsState()
    val savedFaces by viewModel.savedFaces.collectAsState()
    val creations by viewModel.creations.collectAsState()

    // Activity launchers for camera selfie & gallery picker
    val takeSelfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.setSourceFaceBitmap(bitmap)
            viewModel.saveFaceToVault("Selfie ${System.currentTimeMillis() % 1000}", bitmap)
        }
    }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setSourceFaceUri(uri)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = Strings.appTitle(language),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("AI 2.5", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = Strings.appSubtitle(language),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Language Toggle Button
                IconButton(
                    onClick = { viewModel.toggleLanguage() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .border(1.dp, BorderGlow, CircleShape)
                        .testTag("language_toggle_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Change Language",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "বাং" else "EN",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero Feature Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("hero_banner_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "AI Face Swap Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        DeepObsidian.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "রিয়েল ফটো ও ভিডিও ফেস চেঞ্জ"
                            else
                                "Real Photo & Video Face Morphing",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == AppLanguage.BENGALI)
                                "মুহূর্তেই যেকোনো মুভি, ভিডিও বা বিয়েতে নিজের চেহারা বসান"
                            else
                                "Swap your face into movies, wedding portraits & viral videos",
                            color = NeonCyan,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Quick Mode Cards (Photo Swap & Video Swap)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo Swap Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.PHOTO_SWAP) }
                        .testTag("quick_photo_swap_card"),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ফটো ফেস চেঞ্জ" else "Photo Face Swap",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "স্টুডিও ও মুভি পোর্ট্রেট" else "Studio & Movie Portraits",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Video Swap Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.VIDEO_SWAP) }
                        .testTag("quick_video_swap_card"),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentPink.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ভিডিও ফেস চেঞ্জ" else "Video Face Swap",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.BENGALI) "মোশন ও ড্যান্স ক্লিপ" else "Motion & Dance Loops",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Source Face Selection Row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.selectFace(language),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (sourceFaceBitmap != null) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "✓ ফেস লোডেড" else "✓ Face Ready",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera Selfie Capture
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard)
                            .border(1.dp, NeonCyan, RoundedCornerShape(16.dp))
                            .clickable { takeSelfieLauncher.launch(null) }
                            .testTag("home_take_selfie_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "ক্যামেরা" else "Camera",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Gallery Picker
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCard)
                            .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
                            .clickable {
                                pickPhotoLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .testTag("home_pick_gallery_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(24.dp))
                            Text(
                                text = if (language == AppLanguage.BENGALI) "গ্যালারি" else "Gallery",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Current Active Face Preview if selected
                    if (sourceFaceBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, NeonCyan, RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                bitmap = sourceFaceBitmap!!.asImageBitmap(),
                                contentDescription = "Active face",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        // Helpful Prompt
                        Box(
                            modifier = Modifier
                                .height(72.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .padding(10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI)
                                    "আপনার একটি স্পষ্ট সেলফি ফটো তুলুন বা সিলেক্ট করুন"
                                else
                                    "Capture or select a clear frontal face photo",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Template Categories & Catalog
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "ট্রেন্ডিং টেমপ্লেটস" else "Trending Templates",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                CategoryFilterRow(
                    categories = TemplateCategory.values().toList(),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    currentLanguage = language
                )
            }
        }

        // Templates List (filtered)
        val filteredTemplates = viewModel.templates.filter {
            selectedCategory == TemplateCategory.ALL ||
                    it.category == selectedCategory ||
                    (selectedCategory == TemplateCategory.PHOTO && it.type == TemplateType.PHOTO) ||
                    (selectedCategory == TemplateCategory.VIDEO && it.type == TemplateType.VIDEO)
        }

        items(filteredTemplates) { template ->
            TemplateCard(
                template = template,
                isSelected = selectedTemplate.id == template.id,
                onSelect = { viewModel.selectTemplate(template) },
                currentLanguage = language
            )
        }

        // Quick Launch CTA Button
        item {
            GlowButton(
                text = if (selectedTemplate.type == TemplateType.VIDEO)
                    Strings.generateVideo(language)
                else
                    Strings.swapNow(language),
                onClick = {
                    if (selectedTemplate.type == TemplateType.VIDEO) {
                        viewModel.navigateTo(Screen.VIDEO_SWAP)
                    } else {
                        viewModel.navigateTo(Screen.PHOTO_SWAP)
                    }
                },
                testTag = "home_cta_launch_button"
            )
        }
    }
}
