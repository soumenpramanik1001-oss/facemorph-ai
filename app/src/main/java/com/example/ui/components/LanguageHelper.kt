package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage {
    BENGALI,
    ENGLISH
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.BENGALI }

object Strings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "FaceMorph AI"
        AppLanguage.ENGLISH -> "FaceMorph AI"
    }

    fun appSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "রিয়েল ফটো ও ভিডিও ফেস চেঞ্জ স্টুডিও"
        AppLanguage.ENGLISH -> "Real Photo & Video Face Swap Studio"
    }

    fun navHome(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "হোম"
        AppLanguage.ENGLISH -> "Home"
    }

    fun navPhoto(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফটো চেঞ্জ"
        AppLanguage.ENGLISH -> "Photo Swap"
    }

    fun navVideo(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ভিডিও চেঞ্জ"
        AppLanguage.ENGLISH -> "Video Swap"
    }

    fun navVault(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফেস ভল্ট"
        AppLanguage.ENGLISH -> "Face Vault"
    }

    fun navCreations(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ক্রিয়েশনস"
        AppLanguage.ENGLISH -> "Creations"
    }

    fun selectFace(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "আপনার মুখ / সেলফি সিলেক্ট করুন"
        AppLanguage.ENGLISH -> "Select Source Face / Selfie"
    }

    fun takeSelfie(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ক্যামেরা দিয়ে সেলফি"
        AppLanguage.ENGLISH -> "Capture Camera Selfie"
    }

    fun pickGallery(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "গ্যালারি থেকে ফটো"
        AppLanguage.ENGLISH -> "Pick from Gallery"
    }

    fun swapNow(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ম্যাজিক ফেস চেঞ্জ করুন ✨"
        AppLanguage.ENGLISH -> "Magic Face Swap Now ✨"
    }

    fun generateVideo(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ভিডিও ফেস চেঞ্জ তৈরি করুন 🎬"
        AppLanguage.ENGLISH -> "Generate Video Face Swap 🎬"
    }

    fun processingPhoto(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফেসিয়াল ল্যান্ডমার্ক ম্যাচ ও ব্লেন্ডিং হচ্ছে..."
        AppLanguage.ENGLISH -> "Aligning facial landmarks & deep blending..."
    }

    fun processingVideo(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "মোশন কি-ফ্রেম ও ফেস ট্র্যাক সিন্থেসিস হচ্ছে..."
        AppLanguage.ENGLISH -> "Generating motion keyframes & face tracking..."
    }

    fun tuneSettings(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফেস ব্লেন্ডিং টিউনিং"
        AppLanguage.ENGLISH -> "Face Blend Tuning"
    }

    fun skinTone(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "স্কিন টোন মিলানো"
        AppLanguage.ENGLISH -> "Skin Tone Match"
    }

    fun edgeSoftness(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "এজ স্মুথনেস"
        AppLanguage.ENGLISH -> "Edge Feathering"
    }

    fun aiGeminiMode(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "Gemini 2.5 আল্ট্রা মোড"
        AppLanguage.ENGLISH -> "Gemini 2.5 Ultra AI Mode"
    }

    fun beforeAfterTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "আসল ও পরিবর্তিত চেহারার তুলনা"
        AppLanguage.ENGLISH -> "Original vs Morphed Face Comparison"
    }

    fun saveToVault(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফেস ভল্টে সেভ রাখুন"
        AppLanguage.ENGLISH -> "Save Face to Vault"
    }

    fun shareCreation(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "শেয়ার করুন"
        AppLanguage.ENGLISH -> "Share"
    }

    fun savedSuccess(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "সফলভাবে সেভ হয়েছে!"
        AppLanguage.ENGLISH -> "Saved successfully!"
    }

    fun noCreationsYet(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "এখনও কোনো ফেস চেঞ্জ তৈরি করা হয়নি"
        AppLanguage.ENGLISH -> "No creations yet. Swap a face to start!"
    }
}
