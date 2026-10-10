package com.example.data.model

import androidx.annotation.DrawableRes

enum class TemplateType {
    PHOTO,
    VIDEO
}

enum class TemplateCategory(val titleEn: String, val titleBn: String) {
    ALL("All", "সবগুলো"),
    PHOTO("📸 Photos", "📸 ফটো"),
    VIDEO("🎥 Videos", "🎥 ভিডিও"),
    MOVIE("🎬 Movie Star", "🎬 মুভি স্টার"),
    ROYAL("👑 Royal & Wedding", "👑 রাজকীয় ও বিয়ে"),
    CYBER("⚡ Cyber & Hero", "⚡ সাইবার ও হিরো"),
    FASHION("✨ Fashion", "✨ ফ্যাশন")
}

data class FaceTemplate(
    val id: String,
    val title: String,
    val titleBn: String,
    val category: TemplateCategory,
    val type: TemplateType,
    @param:DrawableRes val previewRes: Int,
    val description: String,
    val descriptionBn: String,
    val tags: List<String> = emptyList(),
    val durationSeconds: Float = 3.5f,
    val motionType: String = "Slow Mo Pan"
)

data class FaceSwapSettings(
    val skinToneBlend: Float = 0.85f,
    val edgeSoftness: Float = 0.45f,
    val matchExpression: Boolean = true,
    val matchLighting: Boolean = true,
    val hdEnhancement: Boolean = true,
    val useCloudAi: Boolean = false
)

data class FaceSwapResult(
    val resultBitmapPath: String,
    val originalFacePath: String?,
    val templateId: String,
    val templateTitle: String,
    val isVideo: Boolean,
    val videoFramesCount: Int = 1,
    val processingTimeMs: Long = 0L,
    val note: String = ""
)

data class UserFace(
    val id: Long = 0,
    val name: String,
    val imagePath: String,
    val createdAt: Long = System.currentTimeMillis()
)
