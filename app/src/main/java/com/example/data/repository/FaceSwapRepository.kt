package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.R
import com.example.data.api.GeminiFaceService
import com.example.data.db.AppDatabase
import com.example.data.db.CreationEntity
import com.example.data.db.SavedFaceEntity
import com.example.data.engine.FaceEngine
import com.example.data.model.FaceSwapResult
import com.example.data.model.FaceSwapSettings
import com.example.data.model.FaceTemplate
import com.example.data.model.TemplateCategory
import com.example.data.model.TemplateType
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream

class FaceSwapRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val creationDao = db.creationDao()
    private val savedFaceDao = db.savedFaceDao()

    val creationsFlow: Flow<List<CreationEntity>> = creationDao.getAllCreations()
    val savedFacesFlow: Flow<List<SavedFaceEntity>> = savedFaceDao.getAllFaces()

    val defaultTemplates: List<FaceTemplate> = listOf(
        FaceTemplate(
            id = "template_movie_01",
            title = "Secret Agent Action Star",
            titleBn = "সিক্রেট এজেন্ট অ্যাকশন স্টার",
            category = TemplateCategory.MOVIE,
            type = TemplateType.PHOTO,
            previewRes = R.drawable.img_template_movie,
            description = "Dramatic tuxedo agent pose under cinema spotlights",
            descriptionBn = "সিনেমা স্পটলাইটের নিচে বন্ড-স্টাইল টাক্সিডো পোজ",
            tags = listOf("Action", "Hollywood", "Hero")
        ),
        FaceTemplate(
            id = "template_royal_02",
            title = "Royal Heritage Bengali Bride",
            titleBn = "রাজকীয় বাঙালি বেনারসি বধূ",
            category = TemplateCategory.ROYAL,
            type = TemplateType.PHOTO,
            previewRes = R.drawable.img_template_royal,
            description = "Opulent crimson silk & royal heritage jewelry",
            descriptionBn = "ঐতিহ্যবাহী লাল বেনারসি ও রাজকীয় সোনার গয়না",
            tags = listOf("Wedding", "Royal", "Bengali")
        ),
        FaceTemplate(
            id = "template_cyber_video_03",
            title = "Cyberpunk Neon Warrior (Video)",
            titleBn = "সাইবারপাঙ্ক নিয়ন ওয়ারিয়র (ভিডিও)",
            category = TemplateCategory.CYBER,
            type = TemplateType.VIDEO,
            previewRes = R.drawable.img_hero_banner,
            description = "Dynamic 3.5s slow-motion head tilt with pulsing neon lighting",
            descriptionBn = "৩.৫ সেকেন্ড স্লো-মোশন হেড টিল্ট ও গ্লোয়িং নিয়ন লাইট",
            tags = listOf("Video", "Sci-Fi", "Neon"),
            durationSeconds = 3.5f,
            motionType = "Cinematic Pan"
        ),
        FaceTemplate(
            id = "template_red_carpet_video_04",
            title = "Cannes Red Carpet Celebrity (Video)",
            titleBn = "রেড কার্পেট সেলিব্রিটি ওয়াক (ভিডিও)",
            category = TemplateCategory.MOVIE,
            type = TemplateType.VIDEO,
            previewRes = R.drawable.img_template_movie,
            description = "Camera flash paparazzi walking motion loop",
            descriptionBn = "পাপারাজ্জি ক্যামেরার ফ্ল্যাশ ও সেলিব্রিটি ওয়াক",
            tags = listOf("Video", "Glamour", "Red Carpet"),
            durationSeconds = 4.0f,
            motionType = "Red Carpet Walk"
        ),
        FaceTemplate(
            id = "template_fashion_studio_05",
            title = "Vogue High-Fashion Model",
            titleBn = "ভোগ হাই-ফ্যাশন মডেল পোর্ট্রেট",
            category = TemplateCategory.FASHION,
            type = TemplateType.PHOTO,
            previewRes = R.drawable.app_icon_fg,
            description = "Minimalist editorial magazine cover lighting",
            descriptionBn = "ম্যাগাজিন কভারের মতো এডিটরিয়াল স্টুডিও লাইট",
            tags = listOf("Fashion", "Magazine", "Glamour")
        )
    )

    suspend fun performPhotoFaceSwap(
        sourceBitmap: Bitmap,
        targetTemplate: FaceTemplate,
        settings: FaceSwapSettings
    ): FaceSwapResult {
        val startTime = System.currentTimeMillis()

        // 1. Load target template bitmap
        val targetBitmap = FaceEngine.loadBitmapFromResource(context, targetTemplate.previewRes)

        // 2. Attempt Gemini AI swap if enabled or requested
        var resultBitmap: Bitmap? = null
        var note = "On-Device Neural Engine"

        if (settings.useCloudAi && GeminiFaceService.isApiKeyAvailable()) {
            resultBitmap = GeminiFaceService.generateFaceSwapWithAi(sourceBitmap, targetBitmap)
            if (resultBitmap != null) {
                note = "Gemini 2.5 Flash Image AI"
            }
        }

        // 3. Fallback to on-device algorithm
        if (resultBitmap == null) {
            resultBitmap = FaceEngine.blendFaces(sourceBitmap, targetBitmap, settings)
        }

        // 4. Save result and source to files
        val resultPath = FaceEngine.saveBitmapToFile(context, resultBitmap, "swap_${targetTemplate.id}")
        val sourcePath = FaceEngine.saveBitmapToFile(context, sourceBitmap, "src_face")

        // 5. Insert into Room database
        val creationEntity = CreationEntity(
            title = targetTemplate.title,
            isVideo = false,
            resultImagePath = resultPath,
            originalFacePath = sourcePath,
            templateId = targetTemplate.id,
            templateName = targetTemplate.title,
            videoFramesCount = 1,
            skinToneUsed = settings.skinToneBlend
        )
        creationDao.insertCreation(creationEntity)

        return FaceSwapResult(
            resultBitmapPath = resultPath,
            originalFacePath = sourcePath,
            templateId = targetTemplate.id,
            templateTitle = targetTemplate.title,
            isVideo = false,
            videoFramesCount = 1,
            processingTimeMs = System.currentTimeMillis() - startTime,
            note = note
        )
    }

    suspend fun performVideoFaceSwap(
        sourceBitmap: Bitmap,
        targetTemplate: FaceTemplate,
        settings: FaceSwapSettings,
        frameCount: Int = 12
    ): Pair<FaceSwapResult, List<String>> {
        val startTime = System.currentTimeMillis()
        val targetBitmap = FaceEngine.loadBitmapFromResource(context, targetTemplate.previewRes)

        // Generate motion frames
        val motionFrames = FaceEngine.generateVideoMotionFrames(
            sourceBitmap,
            targetBitmap,
            settings,
            frameCount
        )

        // Save each frame to internal storage
        val framesDir = File(context.filesDir, "video_frames_${System.currentTimeMillis()}")
        if (!framesDir.exists()) framesDir.mkdirs()

        val framePaths = mutableListOf<String>()
        motionFrames.forEachIndexed { index, frame ->
            val frameFile = File(framesDir, "frame_%03d.jpg".format(index))
            FileOutputStream(frameFile).use { out ->
                frame.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            framePaths.add(frameFile.absolutePath)
        }

        val primaryResultPath = framePaths.firstOrNull() ?: ""
        val sourcePath = FaceEngine.saveBitmapToFile(context, sourceBitmap, "src_face_vid")

        val creationEntity = CreationEntity(
            title = "${targetTemplate.title} (Motion Loop)",
            isVideo = true,
            resultImagePath = primaryResultPath,
            originalFacePath = sourcePath,
            templateId = targetTemplate.id,
            templateName = targetTemplate.title,
            videoFramesCount = framePaths.size,
            skinToneUsed = settings.skinToneBlend
        )
        creationDao.insertCreation(creationEntity)

        val swapResult = FaceSwapResult(
            resultBitmapPath = primaryResultPath,
            originalFacePath = sourcePath,
            templateId = targetTemplate.id,
            templateTitle = targetTemplate.title,
            isVideo = true,
            videoFramesCount = framePaths.size,
            processingTimeMs = System.currentTimeMillis() - startTime,
            note = "Motion Keyframe Synthesis (${framePaths.size} frames)"
        )

        return Pair(swapResult, framePaths)
    }

    suspend fun saveFaceToVault(name: String, bitmap: Bitmap): Long {
        val path = FaceEngine.saveBitmapToFile(context, bitmap, "vault_face")
        return savedFaceDao.insertFace(
            SavedFaceEntity(
                name = name,
                imagePath = path
            )
        )
    }

    suspend fun deleteCreation(id: Long) {
        creationDao.deleteById(id)
    }

    suspend fun deleteSavedFace(entity: SavedFaceEntity) {
        savedFaceDao.deleteFace(entity)
    }
}
