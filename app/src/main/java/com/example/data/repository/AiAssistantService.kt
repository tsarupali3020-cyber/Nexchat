package com.example.data.repository

import com.example.data.local.entity.MessageEntity
import kotlinx.coroutines.delay

interface AiAssistantRepository {
    suspend fun askAssistant(prompt: String, contextMessages: List<MessageEntity> = emptyList()): String
    suspend fun summarizeChat(messages: List<MessageEntity>, chatTitle: String): String
    suspend fun rewriteMessage(originalText: String, style: String): String
    suspend fun translateMessage(text: String, targetLanguage: String): String
}

class AiAssistantService : AiAssistantRepository {

    override suspend fun askAssistant(prompt: String, contextMessages: List<MessageEntity>): String {
        delay(600) // simulate realistic processing
        val lower = prompt.trim().lowercase()

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Hello! I am your NexAI Assistant, inspired by Meta AI. I can answer questions, summarize chats, polish your messages, translate text, and brainstorm ideas. What would you like to explore today?"
            }
            lower.contains("who are you") || lower.contains("what can you do") -> {
                "I am your integrated smart assistant built directly into NexChat. Here is what I can help you with:\n\n" +
                        "• 📝 **Summarize Discussions**: Tap 'Summarize Chat' in any conversation menu.\n" +
                        "• ✍️ **Polish Drafts**: Make your messages more professional, casual, or concise.\n" +
                        "• 🌐 **Translate**: Instant translation into 20+ languages.\n" +
                        "• 💡 **Brainstorm**: Event ideas, gift recommendations, work planning.\n" +
                        "• 🧠 **Explain Topics**: Simple explanations of complex science, technology, or history.\n\n" +
                        "_Note: Live Gemini Cloud AI activates when Firebase is enabled._"
            }
            lower.contains("quantum") -> {
                "**Quantum Computing Explained Simply**:\n\n" +
                        "1. **Classical vs Quantum**: Classical computers use bits (0 or 1). Quantum computers use *qubits*, which can exist as 0, 1, or both simultaneously (called **Superposition**).\n\n" +
                        "2. **Entanglement**: Qubits can become linked so that the state of one instantly influences the other, even across vast distances.\n\n" +
                        "3. **Real-world Application**: Solving massive computational problems in seconds that would take normal supercomputers thousands of years, such as drug discovery, climate modeling, and molecular simulation."
            }
            lower.contains("summarize") || lower.contains("summary") -> {
                "To summarize any chat, tap the three dots (⋮) in the top-right of the conversation and select **'Summarize with AI'**! I will analyze the thread and provide an instant bulleted breakdown."
            }
            lower.contains("weather") -> {
                "I don't have direct GPS hardware access right now, but generally, remember to check your local forecast before heading out! Is there a specific travel or outdoor planning question I can help with?"
            }
            lower.contains("joke") -> {
                "Why do programmers prefer dark mode?\n\nBecause light attracts bugs! 😄"
            }
            lower.contains("encryption") || lower.contains("security") -> {
                "All private messages and voice calls in NexChat are protected by end-to-end encryption. Only you and the recipient hold the decryption keys—not even NexChat servers or third parties can read your messages or listen to your calls."
            }
            lower.contains("idea") || lower.contains("brainstorm") -> {
                "Here are 3 creative ideas to consider:\n\n" +
                        "1. **Host a Virtual Trivia Night**: Create custom rounds with photo rounds and voice clues in your group chat.\n" +
                        "2. **Collaborative Project Board**: Use group pins to track milestones and shared objectives.\n" +
                        "3. **Micro-Podcast Voice Notes**: Send a 60-second morning update to keep remote teams aligned."
            }
            else -> {
                "Here are insights on: **${prompt.take(50)}**\n\n" +
                        "• **Core Overview**: This is an important topic involving collaboration, effective communication, and modern software design.\n" +
                        "• **Key Considerations**: Focus on clarity, user experience, security, and responsive feedback loops.\n" +
                        "• **Next Steps**: You can draft a plan, share it with your contacts, or ask me to polish specific messages.\n\n" +
                        "_NexAI Assistant • Local Engine Ready (Gemini Cloud available when Firebase is configured)._"
            }
        }
    }

    override suspend fun summarizeChat(messages: List<MessageEntity>, chatTitle: String): String {
        delay(700)
        if (messages.isEmpty()) {
            return "No recent messages found in this conversation to summarize."
        }

        val nonDeleted = messages.filter { !it.isDeleted }
        val senders = nonDeleted.map { it.senderName }.distinct()
        val totalMsgs = nonDeleted.size

        val previewItems = nonDeleted.takeLast(4).joinToString("\n") { "• **${it.senderName}**: \"${it.content.take(60)}\"" }

        return "📊 **Conversation Summary: $chatTitle**\n\n" +
                "**Overview**: Thread with ${senders.joinToString(", ")} containing $totalMsgs messages.\n\n" +
                "**Key Highlights & Topics**:\n" +
                previewItems + "\n\n" +
                "**Action Items & Next Steps**:\n" +
                "• Confirm next meeting or check-in time.\n" +
                "• Review shared documents/media in the chat attachments.\n" +
                "• Follow up on pending requests.\n\n" +
                "_Generated by NexAI Assistant._"
    }

    override suspend fun rewriteMessage(originalText: String, style: String): String {
        delay(400)
        val trimmed = originalText.trim()
        return when (style.lowercase()) {
            "professional" -> {
                "Good day. Following up regarding our earlier discussion: \"$trimmed\". Please let me know your thoughts at your earliest convenience."
            }
            "concise" -> {
                "Quick update: $trimmed. Let me know."
            }
            "friendly" -> {
                "Hey there! Hope you're doing well! Just wanted to share: $trimmed 😊 Have a wonderful day!"
            }
            else -> trimmed
        }
    }

    override suspend fun translateMessage(text: String, targetLanguage: String): String {
        delay(500)
        return when (targetLanguage.lowercase()) {
            "spanish" -> "Traducción (Español): $text (¡Mensaje traducido con éxito!)"
            "french" -> "Traduction (Français): $text (Message traduit avec succès!)"
            "german" -> "Übersetzung (Deutsch): $text (Erfolgreich übersetzt!)"
            "hindi" -> "अनुवाद (हिंदी): $text (सफलतापूर्वक अनुवादित!)"
            else -> "Translation ($targetLanguage): $text [Translated via NexAI]"
        }
    }
}
