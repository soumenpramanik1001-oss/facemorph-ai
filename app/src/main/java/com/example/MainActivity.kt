package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FaceMorphViewModel
import com.example.ui.Screen
import com.example.ui.components.LocalAppLanguage
import com.example.ui.components.Strings
import com.example.ui.screens.CreationsScreen
import com.example.ui.screens.FaceVaultScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoSwapScreen
import com.example.ui.screens.VideoSwapScreen
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.FaceMorphTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FaceMorphTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: FaceMorphViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()

    CompositionLocalProvider(LocalAppLanguage provides language) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.navigationBars,
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceCard,
                    contentColor = TextSecondary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .border(1.dp, BorderGlow, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .testTag("main_bottom_nav_bar")
                ) {
                    // Home Tab
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.navHome(language),
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == Screen.HOME) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepObsidian,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    // Photo Swap Tab
                    NavigationBarItem(
                        selected = currentScreen == Screen.PHOTO_SWAP,
                        onClick = { viewModel.navigateTo(Screen.PHOTO_SWAP) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Photo Face Swap",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.navPhoto(language),
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == Screen.PHOTO_SWAP) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepObsidian,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_photo")
                    )

                    // Video Swap Tab
                    NavigationBarItem(
                        selected = currentScreen == Screen.VIDEO_SWAP,
                        onClick = { viewModel.navigateTo(Screen.VIDEO_SWAP) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Face Swap",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.navVideo(language),
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == Screen.VIDEO_SWAP) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepObsidian,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_video")
                    )

                    // Face Vault Tab
                    NavigationBarItem(
                        selected = currentScreen == Screen.FACE_VAULT,
                        onClick = { viewModel.navigateTo(Screen.FACE_VAULT) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Face Vault",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.navVault(language),
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == Screen.FACE_VAULT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepObsidian,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_vault")
                    )

                    // Creations Tab
                    NavigationBarItem(
                        selected = currentScreen == Screen.CREATIONS,
                        onClick = { viewModel.navigateTo(Screen.CREATIONS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Creations",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = Strings.navCreations(language),
                                fontSize = 10.sp,
                                fontWeight = if (currentScreen == Screen.CREATIONS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepObsidian,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_creations")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(DeepObsidian)
            ) {
                when (currentScreen) {
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.PHOTO_SWAP -> PhotoSwapScreen(viewModel = viewModel)
                    Screen.VIDEO_SWAP -> VideoSwapScreen(viewModel = viewModel)
                    Screen.FACE_VAULT -> FaceVaultScreen(viewModel = viewModel)
                    Screen.CREATIONS -> CreationsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
