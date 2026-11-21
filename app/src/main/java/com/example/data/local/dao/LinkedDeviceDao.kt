package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.LinkedDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkedDeviceDao {
    @Query("SELECT * FROM linked_devices ORDER BY lastActive DESC")
    fun getAllDevices(): Flow<List<LinkedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: LinkedDeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<LinkedDeviceEntity>)

    @Query("DELETE FROM linked_devices WHERE id = :deviceId")
    suspend fun deleteDevice(deviceId: String)

    @Query("DELETE FROM linked_devices")
    suspend fun clearAllDevices()
}
