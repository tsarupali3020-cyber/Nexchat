package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.data.local.dao.CallDao
import com.example.data.local.entity.CallRecordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.sin

enum class CallState {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTED,
    ENDED
}

data class ActiveCallInfo(
    val callId: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val contactAvatar: String = "",
    val state: CallState = CallState.IDLE,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isIncoming: Boolean = false
)

class VoiceCallManager(
    private val context: Context,
    private val callDao: CallDao,
    private val scope: CoroutineScope
) {
    private val _callState = MutableStateFlow(ActiveCallInfo())
    val callState: StateFlow<ActiveCallInfo> = _callState.asStateFlow()

    private var durationJob: Job? = null
    private var toneJob: Job? = null
    private var audioTrack: AudioTrack? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun startOutgoingCall(contactName: String, contactPhone: String, contactAvatar: String) {
        val callId = UUID.randomUUID().toString()
        _callState.value = ActiveCallInfo(
            callId = callId,
            contactName = contactName,
            contactPhone = contactPhone,
            contactAvatar = contactAvatar,
            state = CallState.OUTGOING_RINGING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = false,
            isIncoming = false
        )

        startRingingTone(isIncoming = false)

        // Automatically connect call after 3 seconds of ringing
        scope.launch {
            delay(3000)
            if (_callState.value.state == CallState.OUTGOING_RINGING) {
                connectCall()
            }
        }
    }

    fun triggerIncomingCall(contactName: String, contactPhone: String, contactAvatar: String) {
        val callId = UUID.randomUUID().toString()
        _callState.value = ActiveCallInfo(
            callId = callId,
            contactName = contactName,
            contactPhone = contactPhone,
            contactAvatar = contactAvatar,
            state = CallState.INCOMING_RINGING,
            durationSeconds = 0,
            isMuted = false,
            isSpeakerOn = false,
            isIncoming = true
        )

        startRingingTone(isIncoming = true)
    }

    fun acceptIncomingCall() {
        stopTone()
        connectCall()
    }

    fun rejectIncomingCall() {
        stopTone()
        val current = _callState.value
        scope.launch {
            callDao.insertCall(
                CallRecordEntity(
                    id = current.callId.ifBlank { UUID.randomUUID().toString() },
                    contactName = current.contactName,
                    contactPhone = current.contactPhone,
                    contactAvatar = current.contactAvatar,
                    timestamp = System.currentTimeMillis(),
                    durationSeconds = 0,
                    callType = "MISSED"
                )
            )
        }
        _callState.value = current.copy(state = CallState.ENDED)
        scope.launch {
            delay(1000)
            _callState.value = ActiveCallInfo()
        }
    }

    private fun connectCall() {
        stopTone()
        _callState.value = _callState.value.copy(state = CallState.CONNECTED)
        startDurationTimer()
        playConnectChime()
    }

    fun toggleMute() {
        val current = _callState.value
        _callState.value = current.copy(isMuted = !current.isMuted)
    }

    fun toggleSpeaker() {
        val current = _callState.value
        val newState = !current.isSpeakerOn
        _callState.value = current.copy(isSpeakerOn = newState)
        try {
            audioManager.isSpeakerphoneOn = newState
        } catch (_: Exception) {}
    }

    fun endCall() {
        stopTone()
        durationJob?.cancel()
        val current = _callState.value
        if (current.state != CallState.IDLE) {
            playEndChime()
            scope.launch {
                callDao.insertCall(
                    CallRecordEntity(
                        id = current.callId.ifBlank { UUID.randomUUID().toString() },
                        contactName = current.contactName,
                        contactPhone = current.contactPhone,
                        contactAvatar = current.contactAvatar,
                        timestamp = System.currentTimeMillis(),
                        durationSeconds = current.durationSeconds,
                        callType = if (current.isIncoming) "INCOMING" else "OUTGOING"
                    )
                )
            }
            _callState.value = current.copy(state = CallState.ENDED)
            scope.launch {
                delay(1200)
                _callState.value = ActiveCallInfo()
            }
        }
    }

    private fun startDurationTimer() {
        durationJob?.cancel()
        durationJob = scope.launch {
            while (isActive && _callState.value.state == CallState.CONNECTED) {
                delay(1000)
                _callState.value = _callState.value.copy(
                    durationSeconds = _callState.value.durationSeconds + 1
                )
            }
        }
    }

    private fun startRingingTone(isIncoming: Boolean) {
        stopTone()
        toneJob = scope.launch(Dispatchers.Default) {
            val sampleRate = 8000
            val freq = if (isIncoming) 520.0 else 440.0
            val buffSize = sampleRate / 2
            val samples = ShortArray(buffSize)

            for (i in 0 until buffSize) {
                val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                samples[i] = (sin(angle) * 8000).toInt().toShort()
            }

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                while (isActive) {
                    audioTrack?.write(samples, 0, samples.size)
                    delay(if (isIncoming) 1000 else 1800)
                }
            } catch (_: Exception) {}
        }
    }

    private fun playConnectChime() {
        scope.launch(Dispatchers.Default) {
            playTone(880.0, 150)
            delay(50)
            playTone(1320.0, 200)
        }
    }

    private fun playEndChime() {
        scope.launch(Dispatchers.Default) {
            playTone(440.0, 150)
            delay(50)
            playTone(330.0, 250)
        }
    }

    private fun playTone(frequency: Double, durationMs: Long) {
        try {
            val sampleRate = 8000
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val angle = 2.0 * Math.PI * i / (sampleRate / frequency)
                samples[i] = (sin(angle) * 7000).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs)
            track.stop()
            track.release()
        } catch (_: Exception) {}
    }

    private fun stopTone() {
        toneJob?.cancel()
        toneJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
