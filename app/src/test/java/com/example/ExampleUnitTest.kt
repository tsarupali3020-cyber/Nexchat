package com.example

import com.example.data.local.entity.MessageEntity
import com.example.data.repository.AiAssistantService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun aiAssistantService_answersGreeting() = runBlocking {
        val service = AiAssistantService()
        val reply = service.askAssistant("Hello there!")
        assertTrue(reply.contains("NexAI Assistant"))
    }

    @Test
    fun aiAssistantService_rewritesMessage() = runBlocking {
        val service = AiAssistantService()
        val polished = service.rewriteMessage("can we meet tomorrow", "professional")
        assertTrue(polished.contains("Good day"))
    }

    @Test
    fun aiAssistantService_summarizesMessages() = runBlocking {
        val service = AiAssistantService()
        val messages = listOf(
            MessageEntity(
                id = "1",
                chatId = "c1",
                senderPhone = "+1",
                senderName = "Sarah",
                isFromMe = false,
                content = "We need to finalize the quarterly release."
            ),
            MessageEntity(
                id = "2",
                chatId = "c1",
                senderPhone = "+2",
                senderName = "Alex",
                isFromMe = true,
                content = "Agreed, let us schedule a sync."
            )
        )
        val summary = service.summarizeChat(messages, "Project Sprint")
        assertNotNull(summary)
        assertTrue(summary.contains("Conversation Summary"))
        assertTrue(summary.contains("Project Sprint"))
    }
}
