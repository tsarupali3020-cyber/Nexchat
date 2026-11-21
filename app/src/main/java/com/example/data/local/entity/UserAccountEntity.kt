package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: String = "primary_account",
    val phoneNumber: String = "+1 (555) 234-5678",
    val countryCode: String = "+1",
    val username: String = "Alex Morgan",
    val statusBio: String = "Available | Hey there! I am using NexChat.",
    val avatarUrl: String = "",
    val isLoggedIn: Boolean = true,
    val isTwoFactorEnabled: Boolean = false,
    val twoFactorPin: String = "",
    val isBiometricEnabled: Boolean = false,
    val lastSeenVisibility: String = "EVERYONE", // EVERYONE, CONTACTS, NOBODY
    val profilePhotoVisibility: String = "EVERYONE",
    val readReceiptsEnabled: Boolean = true,
    val isSecurityLocked: Boolean = false,
    val securityLockReason: String = ""
)
