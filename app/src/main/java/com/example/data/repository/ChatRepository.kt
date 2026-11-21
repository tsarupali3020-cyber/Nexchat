package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CallRecordEntity
import com.example.data.local.entity.ChatEntity
import com.example.data.local.entity.ContactEntity
import com.example.data.local.entity.LinkedDeviceEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.UserAccountEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(
    private val database: AppDatabase,
    private val aiService: AiAssistantService,
    private val repositoryScope: CoroutineScope
) {
    private val chatDao = database.chatDao()
    private val messageDao = database.messageDao()
    private val userDao = database.userAccountDao()
    private val callDao = database.callDao()
    private val deviceDao = database.linkedDeviceDao()
    private val contactDao = database.contactDao()

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()
    val allCalls: Flow<List<CallRecordEntity>> = callDao.getAllCalls()
    val allDevices: Flow<List<LinkedDeviceEntity>> = deviceDao.getAllDevices()
    val allContacts: Flow<List<ContactEntity>> = contactDao.getAllContacts()
    val userAccount: Flow<UserAccountEntity?> = userDao.getUserAccount()

    init {
        repositoryScope.launch(Dispatchers.IO) {
            checkAndSeedInitialData()
        }
    }

    private suspend fun checkAndSeedInitialData() {
        val existingAccount = userDao.getUserAccountOnce()
        if (existingAccount == null) {
            userDao.insertOrUpdate(UserAccountEntity())
        }

        val existingChats = chatDao.getAllChats().firstOrNull()
        if (existingChats.isNullOrEmpty()) {
            val now = System.currentTimeMillis()

            val aiChat = ChatEntity(
                id = "ai_assistant_chat",
                name = "NexAI Assistant",
                isGroup = false,
                isAiChat = true,
                isPinned = true,
                lastMessageText = "Hello! How can I assist your conversations today?",
                lastMessageTimestamp = now,
                unreadCount = 0,
                isOnline = true,
                lastSeenText = "Always active"
            )

            val sarahChat = ChatEntity(
                id = "chat_sarah",
                name = "Sarah Jenkins",
                isGroup = false,
                lastMessageText = "Can you review the updated wireframes for the project?",
                lastMessageTimestamp = now - 5 * 60 * 1000,
                unreadCount = 1,
                isOnline = true,
                lastSeenText = "online"
            )

            val devGroupChat = ChatEntity(
                id = "chat_dev_group",
                name = "NexChat Core Team",
                isGroup = true,
                members = "Alex, Sarah, David, Maya, Carlos",
                lastMessageText = "David: Deployment v2.4 passed staging tests 🚀",
                lastMessageTimestamp = now - 22 * 60 * 1000,
                unreadCount = 2,
                isOnline = false,
                lastSeenText = "5 participants"
            )

            val emmaChat = ChatEntity(
                id = "chat_emma",
                name = "Emma Watson",
                isGroup = false,
                lastMessageText = "Voice message (0:18)",
                lastMessageTimestamp = now - 2 * 3600 * 1000,
                unreadCount = 0,
                isOnline = false,
                lastSeenText = "today at 1:15 PM"
            )

            chatDao.insertChats(listOf(aiChat, sarahChat, devGroupChat, emmaChat))

            // Seed initial messages
            val aiWelcome = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = aiChat.id,
                senderPhone = "AI",
                senderName = "NexAI",
                isFromMe = false,
                content = "👋 Welcome to NexAI Assistant! Inspired by Meta AI, I am directly integrated into your messaging workflow.\n\nAsk me anything, or tap 'Summarize Chat' in any conversation to get an instant recap of discussions!",
                timestamp = now - 1000,
                status = "READ"
            )

            val sarahMsg1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = sarahChat.id,
                senderPhone = "+1 555-0192",
                senderName = "Sarah Jenkins",
                isFromMe = false,
                content = "Hey Alex! Did you get a chance to test the new end-to-end encryption indicator?",
                timestamp = now - 15 * 60 * 1000,
                status = "READ"
            )

            val sarahReply1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = sarahChat.id,
                senderPhone = "ME",
                senderName = "Alex Morgan",
                isFromMe = true,
                content = "Yes! Verified the 60-digit safety numbers. Looks super clean and secure.",
                timestamp = now - 12 * 60 * 1000,
                status = "READ",
                reactions = "👍:1"
            )

            val sarahMsg2 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = sarahChat.id,
                senderPhone = "+1 555-0192",
                senderName = "Sarah Jenkins",
                isFromMe = false,
                content = "Can you review the updated wireframes for the project?",
                timestamp = now - 5 * 60 * 1000,
                status = "DELIVERED"
            )

            // Dev group messages
            val devMsg1 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = devGroupChat.id,
                senderPhone = "+1 555-0144",
                senderName = "Maya",
                isFromMe = false,
                content = "All unit tests and integration tests are passing with 100% coverage.",
                timestamp = now - 35 * 60 * 1000,
                status = "READ"
            )

            val devMsg2 = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = devGroupChat.id,
                senderPhone = "+1 555-0188",
                senderName = "David",
                isFromMe = false,
                content = "Deployment v2.4 passed staging tests 🚀",
                timestamp = now - 22 * 60 * 1000,
                status = "READ",
                reactions = "🔥:3,❤️:1"
            )

            // Emma messages (including voice message)
            val emmaVoice = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = emmaChat.id,
                senderPhone = "+1 555-0155",
                senderName = "Emma Watson",
                isFromMe = false,
                content = "Voice note",
                messageType = "VOICE",
                mediaDurationSec = 18,
                timestamp = now - 2 * 3600 * 1000,
                status = "READ"
            )

            messageDao.insertMessages(listOf(aiWelcome, sarahMsg1, sarahReply1, sarahMsg2, devMsg1, devMsg2, emmaVoice))

            // Seed calls
            val call1 = CallRecordEntity(
                id = UUID.randomUUID().toString(),
                contactName = "Sarah Jenkins",
                contactPhone = "+1 (555) 019-2834",
                timestamp = now - 45 * 60 * 1000,
                durationSeconds = 252,
                callType = "INCOMING"
            )

            val call2 = CallRecordEntity(
                id = UUID.randomUUID().toString(),
                contactName = "Emma Watson",
                contactPhone = "+1 (555) 015-9921",
                timestamp = now - 4 * 3600 * 1000,
                durationSeconds = 165,
                callType = "OUTGOING"
            )

            val call3 = CallRecordEntity(
                id = UUID.randomUUID().toString(),
                contactName = "Sarah Jenkins",
                contactPhone = "+1 (555) 019-2834",
                timestamp = now - 26 * 3600 * 1000,
                durationSeconds = 0,
                callType = "MISSED"
            )

            callDao.insertCall(call1)
            callDao.insertCall(call2)
            callDao.insertCall(call3)

            // Seed linked devices
            val dev1 = LinkedDeviceEntity(
                id = "dev_chrome_win",
                deviceName = "Chrome (Windows 11)",
                platform = "Web Browser",
                ipAddress = "192.168.1.145",
                location = "New York, USA",
                lastActive = now
            )
            val dev2 = LinkedDeviceEntity(
                id = "dev_mac_safari",
                deviceName = "Safari (macOS Sonoma)",
                platform = "MacBook Pro",
                ipAddress = "192.168.1.189",
                location = "San Francisco, USA",
                lastActive = now - 2 * 3600 * 1000
            )
            deviceDao.insertDevices(listOf(dev1, dev2))

            // Seed contacts
            val c1 = ContactEntity(
                id = "c_sarah",
                name = "Sarah Jenkins",
                phone = "+1 (555) 019-2834",
                statusBio = "Designing delightful mobile experiences ✨",
                isOnline = true
            )
            val c2 = ContactEntity(
                id = "c_emma",
                name = "Emma Watson",
                phone = "+1 (555) 015-9921",
                statusBio = "Available | Keep moving forward 🏃‍♀️",
                isOnline = false
            )
            val c3 = ContactEntity(
                id = "c_david",
                name = "David Chen",
                phone = "+1 (555) 018-8832",
                statusBio = "Writing Kotlin & Cloud architecture",
                isOnline = true
            )
            val c4 = ContactEntity(
                id = "c_maya",
                name = "Maya Patel",
                phone = "+1 (555) 014-4491",
                statusBio = "Coffee & Code ☕",
                isOnline = false
            )
            val c5 = ContactEntity(
                id = "c_carlos",
                name = "Carlos Santana",
                phone = "+1 (555) 017-7722",
                statusBio = "Busy in meetings",
                isOnline = false
            )
            contactDao.insertContacts(listOf(c1, c2, c3, c4, c5))
        }
    }

    fun getMessages(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChat(chatId)

    fun getChat(chatId: String): Flow<ChatEntity?> = chatDao.getChatById(chatId)

    suspend fun sendMessage(
        chatId: String,
        content: String,
        messageType: String = "TEXT",
        mediaUri: String = "",
        mediaDurationSec: Int = 0,
        replyTo: MessageEntity? = null
    ) {
        val now = System.currentTimeMillis()
        val messageId = UUID.randomUUID().toString()

        val newMessage = MessageEntity(
            id = messageId,
            chatId = chatId,
            senderPhone = "ME",
            senderName = "Alex Morgan",
            isFromMe = true,
            content = content,
            messageType = messageType,
            mediaUri = mediaUri,
            mediaDurationSec = mediaDurationSec,
            timestamp = now,
            status = "SENT",
            replyToId = replyTo?.id,
            replyToContent = replyTo?.content,
            replyToSender = replyTo?.senderName
        )

        messageDao.insertMessage(newMessage)
        chatDao.updateLastMessage(
            chatId,
            if (messageType == "VOICE") "Voice message ($mediaDurationSec s)" else content,
            now
        )

        // Progress status: SENT -> DELIVERED -> READ
        repositoryScope.launch(Dispatchers.IO) {
            delay(1000)
            messageDao.updateStatus(messageId, "DELIVERED")
            delay(1500)
            messageDao.updateStatus(messageId, "READ")
        }

        // If AI chat, generate AI response
        if (chatId == "ai_assistant_chat") {
            repositoryScope.launch(Dispatchers.IO) {
                delay(700)
                val aiReplyText = aiService.askAssistant(content)
                val aiMsg = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    senderPhone = "AI",
                    senderName = "NexAI",
                    isFromMe = false,
                    content = aiReplyText,
                    timestamp = System.currentTimeMillis(),
                    status = "READ"
                )
                messageDao.insertMessage(aiMsg)
                chatDao.updateLastMessage(chatId, aiReplyText.take(60), System.currentTimeMillis())
            }
        } else if (chatId == "chat_sarah") {
            // Friendly simulated conversational partner
            repositoryScope.launch(Dispatchers.IO) {
                delay(2500)
                val responses = listOf(
                    "Got it! Thanks for sending that over.",
                    "Sounds wonderful! Let me take a look right now.",
                    "Awesome, that makes total sense.",
                    "Perfect! I will update the team."
                )
                val reply = responses.random()
                val contactMsg = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    senderPhone = "+1 555-0192",
                    senderName = "Sarah Jenkins",
                    isFromMe = false,
                    content = reply,
                    timestamp = System.currentTimeMillis(),
                    status = "READ"
                )
                messageDao.insertMessage(contactMsg)
                chatDao.updateLastMessage(chatId, reply, System.currentTimeMillis())
            }
        }
    }

    suspend fun addReaction(messageId: String, currentReactions: String, emoji: String) {
        val updated = if (currentReactions.contains(emoji)) {
            // Already has this emoji, keep or increment
            currentReactions
        } else if (currentReactions.isBlank()) {
            "$emoji:1"
        } else {
            "$currentReactions,$emoji:1"
        }
        messageDao.updateReactions(messageId, updated)
    }

    suspend fun editMessage(messageId: String, newContent: String) {
        messageDao.editMessage(messageId, newContent)
    }

    suspend fun deleteMessage(messageId: String, forEveryone: Boolean) {
        if (forEveryone) {
            messageDao.markDeleted(messageId)
        } else {
            messageDao.deletePermanently(messageId)
        }
    }

    suspend fun togglePin(chatId: String, currentPinned: Boolean) {
        chatDao.setPinned(chatId, !currentPinned)
    }

    suspend fun toggleArchive(chatId: String, currentArchived: Boolean) {
        chatDao.setArchived(chatId, !currentArchived)
    }

    suspend fun clearChat(chatId: String) {
        messageDao.clearChatMessages(chatId)
    }

    suspend fun deleteChat(chatId: String) {
        chatDao.deleteChatById(chatId)
        messageDao.clearChatMessages(chatId)
    }

    suspend fun createNewChat(name: String, phone: String, isGroup: Boolean): String {
        val chatId = "chat_" + UUID.randomUUID().toString().take(8)
        val now = System.currentTimeMillis()
        val newChat = ChatEntity(
            id = chatId,
            name = name,
            isGroup = isGroup,
            lastMessageText = if (isGroup) "Group created" else "Chat started",
            lastMessageTimestamp = now,
            unreadCount = 0,
            isOnline = true,
            lastSeenText = "online"
        )
        chatDao.insertChat(newChat)
        return chatId
    }

    suspend fun unlinkDevice(deviceId: String) {
        deviceDao.deleteDevice(deviceId)
    }

    suspend fun pairNewDevice(deviceName: String, platform: String) {
        val newDev = LinkedDeviceEntity(
            id = "dev_" + UUID.randomUUID().toString().take(8),
            deviceName = deviceName,
            platform = platform,
            ipAddress = "192.168.1.${(10..250).random()}",
            location = "Local Network (Verified)",
            lastActive = System.currentTimeMillis()
        )
        deviceDao.insertDevice(newDev)
    }

    suspend fun updateAccount(updateFn: (UserAccountEntity) -> UserAccountEntity) {
        val current = userDao.getUserAccountOnce() ?: UserAccountEntity()
        val updated = updateFn(current)
        userDao.insertOrUpdate(updated)
    }

    suspend fun triggerSecurityLockdown(reason: String) {
        deviceDao.clearAllDevices() // Revoke all remote active sessions immediately
        userDao.updateSecurityLock(isLocked = true, reason = reason)
    }

    suspend fun restoreAccountSecurity() {
        userDao.updateSecurityLock(isLocked = false, reason = "")
    }

    suspend fun toggleBlockContact(contactId: String, currentBlocked: Boolean) {
        contactDao.setBlocked(contactId, !currentBlocked)
    }

    suspend fun deleteContact(contactId: String) {
        contactDao.deleteContactById(contactId)
    }

    suspend fun addContact(name: String, phone: String, status: String = "Hey there! I am using NexChat.") {
        val newContact = ContactEntity(
            id = "c_" + UUID.randomUUID().toString().take(8),
            name = name,
            phone = phone,
            statusBio = status,
            isOnline = false
        )
        contactDao.insertContact(newContact)
    }
}
