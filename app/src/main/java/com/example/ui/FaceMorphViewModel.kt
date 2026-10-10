package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CreationEntity
import com.example.data.db.SavedFaceEntity
import com.example.data.engine.FaceEngine
import com.example.data.model.FaceSwapResult
import com.example.data.model.FaceSwapSettings
import com.example.data.model.FaceTemplate
import com.example.data.model.TemplateCategory
import com.example.data.model.TemplateType
import com.example.data.repository.FaceSwapRepository
import com.example.ui.components.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    PHOTO_SWAP,
    VIDEO_SWAP,
    FACE_VAULT,
    CREATIONS
}

sealed class ProcessingUiState {
    object Idle : ProcessingUiState()
    data class Processing(val stepMessage: String) : ProcessingUiState()
    data class Success(val result: FaceSwapResult, val videoFrames: List<String> = emptyList()) : ProcessingUiState()
    data class Error(val message: String) : ProcessingUiState()
}

class FaceMorphViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FaceSwapRepository(application.applicationContext)

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.BENGALI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val templates: List<FaceTemplate> = repository.defaultTemplates

    private val _selectedTemplate = MutableStateFlow<FaceTemplate>(templates.first())
    val selectedTemplate: StateFlow<FaceTemplate> = _selectedTemplate.asStateFlow()

    private val _selectedCategory = MutableStateFlow(TemplateCategory.ALL)
    val selectedCategory: StateFlow<TemplateCategory> = _selectedCategory.asStateFlow()

    private val _sourceFaceBitmap = MutableStateFlow<Bitmap?>(null)
    val sourceFaceBitmap: StateFlow<Bitmap?> = _sourceFaceBitmap.asStateFlow()

    private val _sourceFaceUri = MutableStateFlow<Uri?>(null)
    val sourceFaceUri: StateFlow<Uri?> = _sourceFaceUri.asStateFlow()

    private val _swapSettings = MutableStateFlow(FaceSwapSettings())
    val swapSettings: StateFlow<FaceSwapSettings> = _swapSettings.asStateFlow()

    private val _processingState = MutableStateFlow<ProcessingUiState>(ProcessingUiState.Idle)
    val processingState: StateFlow<ProcessingUiState> = _processingState.asStateFlow()

    // Room Flows
    val creations: StateFlow<List<CreationEntity>> = repository.creationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedFaces: StateFlow<List<SavedFaceEntity>> = repository.savedFacesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.BENGALI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.BENGALI
        }
    }

    fun selectCategory(category: TemplateCategory) {
        _selectedCategory.value = category
    }

    fun selectTemplate(template: FaceTemplate) {
        _selectedTemplate.value = template
        if (template.type == TemplateType.VIDEO && _currentScreen.value != Screen.VIDEO_SWAP) {
            _currentScreen.value = Screen.VIDEO_SWAP
        } else if (template.type == TemplateType.PHOTO && _currentScreen.value != Screen.PHOTO_SWAP) {
            _currentScreen.value = Screen.PHOTO_SWAP
        }
    }

    fun setSourceFaceBitmap(bitmap: Bitmap) {
        _sourceFaceBitmap.value = bitmap
    }

    fun setSourceFaceUri(uri: Uri) {
        _sourceFaceUri.value = uri
        viewModelScope.launch {
            val bmp = FaceEngine.loadBitmapFromUri(getApplication(), uri)
            _sourceFaceBitmap.value = bmp
        }
    }

    fun updateSettings(newSettings: FaceSwapSettings) {
        _swapSettings.value = newSettings
    }

    fun resetProcessing() {
        _processingState.value = ProcessingUiState.Idle
    }

    fun startPhotoSwap() {
        val src = _sourceFaceBitmap.value
        val template = _selectedTemplate.value
        if (src == null) {
            _processingState.value = ProcessingUiState.Error("Please choose or capture a face first!")
            return
        }

        viewModelScope.launch {
            try {
                _processingState.value = ProcessingUiState.Processing(
                    if (_currentLanguage.value == AppLanguage.BENGALI)
                        "মুখের ল্যান্ডমার্ক ডিটেকশন ও স্কিন টোন মেলানো হচ্ছে..."
                    else
                        "Detecting landmarks & aligning skin tone..."
                )

                val result = repository.performPhotoFaceSwap(src, template, _swapSettings.value)
                _processingState.value = ProcessingUiState.Success(result)
            } catch (e: Exception) {
                _processingState.value = ProcessingUiState.Error("Face swap error: ${e.localizedMessage}")
            }
        }
    }

    fun startVideoSwap() {
        val src = _sourceFaceBitmap.value
        val template = _selectedTemplate.value
        if (src == null) {
            _processingState.value = ProcessingUiState.Error("Please choose or capture a face first!")
            return
        }

        viewModelScope.launch {
            try {
                _processingState.value = ProcessingUiState.Processing(
                    if (_currentLanguage.value == AppLanguage.BENGALI)
                        "ভিডিও মোশন কি-ফ্রেম ও ফেস ট্র্যাক সিন্থেসিস হচ্ছে..."
                    else
                        "Rendering video motion frames & tracking..."
                )

                val (result, frames) = repository.performVideoFaceSwap(src, template, _swapSettings.value, frameCount = 12)
                _processingState.value = ProcessingUiState.Success(result, frames)
            } catch (e: Exception) {
                _processingState.value = ProcessingUiState.Error("Video swap error: ${e.localizedMessage}")
            }
        }
    }

    fun saveFaceToVault(name: String, bitmap: Bitmap) {
        viewModelScope.launch {
            repository.saveFaceToVault(name, bitmap)
        }
    }

    fun deleteCreation(id: Long) {
        viewModelScope.launch {
            repository.deleteCreation(id)
        }
    }

    fun deleteSavedFace(entity: SavedFaceEntity) {
        viewModelScope.launch {
            repository.deleteSavedFace(entity)
        }
    }
}
