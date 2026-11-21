package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linked_devices")
data class LinkedDeviceEntity(
    @PrimaryKey val id: String,
    val deviceName: String,
    val platform: String, // "Chrome (Windows)", "Safari (macOS)", "WhatsApp Desktop"
    val ipAddress: String,
    val location: String,
    val lastActive: Long = System.currentTimeMillis()
)
