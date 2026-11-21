package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CallDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.ContactDao
import com.example.data.local.dao.LinkedDeviceDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.UserAccountDao
import com.example.data.local.entity.CallRecordEntity
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.ContactEntity
import com.example.data.local.entity.LinkedDeviceEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.UserAccountEntity

@Database(
    entities = [
        UserAccountEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        CallRecordEntity::class,
        LinkedDeviceEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun callDao(): CallDao
    abstract fun linkedDeviceDao(): LinkedDeviceDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexchat_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
