package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderPhone: String,
    val senderName: String,
    val isFromMe: Boolean,
    val content: String,
    val messageType: String = "TEXT", // TEXT, IMAGE, VOICE, DOCUMENT
    val mediaUri: String = "",
    val mediaDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "DELIVERED", // PENDING, SENT, DELIVERED, READ
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val replyToId: String? = null,
    val replyToContent: String? = null,
    val replyToSender: String? = null,
    val reactions: String = "" // e.g. "❤️:1,👍:2"
)
