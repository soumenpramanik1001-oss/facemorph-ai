package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val isVideo: Boolean,
    val resultImagePath: String,
    val originalFacePath: String?,
    val templateId: String,
    val templateName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val videoFramesCount: Int = 1,
    val skinToneUsed: Float = 0.85f
)

@Entity(tableName = "saved_faces")
data class SavedFaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imagePath: String,
    val createdAt: Long = System.currentTimeMillis()
)
