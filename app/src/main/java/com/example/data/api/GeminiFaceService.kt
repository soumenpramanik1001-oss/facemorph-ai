package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiFaceService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyAvailable(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Throwable) {
            false
        }
    }

    /**
     * Calls Gemini 2.5 Flash Image to execute a multimodal face transfer/swap.
     * Takes source face bitmap and target template bitmap, returning the AI-generated swapped bitmap.
     */
    suspend fun generateFaceSwapWithAi(
        sourceFace: Bitmap,
        targetTemplate: Bitmap,
        customPromptNotes: String = ""
    ): Bitmap? = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        try {
            val sourceBase64 = sourceFace.toBase64()
            val targetBase64 = targetTemplate.toBase64()

            val promptText = "Realistic face swap task: Replace the face in the second image (Image 2 - target body/scene) " +
                    "with the exact facial identity, eyes, nose, lips, and facial bone structure of the person from the first image (Image 1 - source face). " +
                    "Preserve the target body's lighting, neck angle, skin tone gradient, hair boundary, and cinematic background. " +
                    "Ensure seamless edge blending with photorealistic high definition finish. $customPromptNotes"

            // Construct JSON request
            val partsArray = JSONArray().apply {
                // Instruction prompt
                put(JSONObject().put("text", promptText))
                // Source face image
                put(JSONObject().put("inlineData", JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", sourceBase64)
                }))
                // Target template image
                put(JSONObject().put("inlineData", JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", targetBase64)
                }))
            }

            val contentsArray = JSONArray().apply {
                put(JSONObject().put("parts", partsArray))
            }

            val generationConfig = JSONObject().apply {
                put("responseModalities", JSONArray().apply {
                    put("TEXT")
                    put("IMAGE")
                })
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", generationConfig)
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext null

            if (!response.isSuccessful) {
                return@withContext null
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null

            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val inlineData = part.optJSONObject("inlineData")
                if (inlineData != null) {
                    val base64Data = inlineData.optString("data")
                    if (base64Data.isNotBlank()) {
                        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                        return@withContext BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Bitmap.toBase64(maxDimension: Int = 800): String {
        val scaled = if (width > maxDimension || height > maxDimension) {
            val ratio = width.toFloat() / height.toFloat()
            val newW = if (ratio > 1f) maxDimension else (maxDimension * ratio).toInt()
            val newH = if (ratio > 1f) (maxDimension / ratio).toInt() else maxDimension
            Bitmap.createScaledBitmap(this, newW.coerceAtLeast(10), newH.coerceAtLeast(10), true)
        } else {
            this
        }
        val outputStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
