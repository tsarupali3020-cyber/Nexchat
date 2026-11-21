package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val avatarUrl: String = "",
    val statusBio: String = "Hey there! I am using NexChat.",
    val isBlocked: Boolean = false,
    val isOnline: Boolean = false,
    val lastSeen: String = "today at 10:45 AM"
)
