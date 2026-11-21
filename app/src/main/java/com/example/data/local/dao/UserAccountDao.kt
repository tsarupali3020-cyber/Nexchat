package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE id = 'primary_account' LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 'primary_account' LIMIT 1")
    suspend fun getUserAccountOnce(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: UserAccountEntity)

    @Query("UPDATE user_account SET isSecurityLocked = :isLocked, securityLockReason = :reason WHERE id = 'primary_account'")
    suspend fun updateSecurityLock(isLocked: Boolean, reason: String)
}
