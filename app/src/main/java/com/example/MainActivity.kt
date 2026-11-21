package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.AiAssistantService
import com.example.data.repository.ChatRepository
import com.example.service.VoiceCallManager
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.main.MainAppContainer
import com.example.ui.theme.NexChatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val aiService = AiAssistantService()
        val chatRepository = ChatRepository(database, aiService, lifecycleScope)
        val voiceCallManager = VoiceCallManager(applicationContext, database.callDao(), lifecycleScope)

        setContent {
            NexChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val account by chatRepository.userAccount.collectAsState(initial = null)

                    if (account != null && !account!!.isLoggedIn) {
                        AuthScreen(
                            repository = chatRepository,
                            onAuthComplete = {}
                        )
                    } else {
                        MainAppContainer(
                            repository = chatRepository,
                            aiService = aiService,
                            callManager = voiceCallManager
                        )
                    }
                }
            }
        }
    }
}
