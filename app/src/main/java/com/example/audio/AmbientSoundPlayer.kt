package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

enum class AmbientSound(val id: String, val title: String, val emoji: String) {
    NONE("none", "Silencio", "🔇"),
    RAIN("rain", "Lluvia Suave", "🌧️"),
    WHITE_NOISE("noise", "Ruido Blanco", "⚡"),
    WAVES("waves", "Olas del Mar", "🌊"),
    FOREST("forest", "Bosque Zen", "🌲")
}

object AmbientSoundPlayer {

    private val playerScope = CoroutineScope(Dispatchers.Default + Job())
    private var playbackJob: Job? = null
    private var audioTrack: AudioTrack? = null

    private val _currentSound = MutableStateFlow(AmbientSound.NONE)
    val currentSound: StateFlow<AmbientSound> = _currentSound.asStateFlow()

    private const val SAMPLE_RATE = 44100
    private const val BUFFER_SIZE = 8192

    fun setSound(sound: AmbientSound) {
        val prev = _currentSound.value
        _currentSound.value = sound

        if (sound == AmbientSound.NONE) {
            stop()
        } else if (prev != sound) {
            play(sound)
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            val track = audioTrack
            audioTrack = null
            track?.stop()
            track?.flush()
            track?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun play(sound: AmbientSound) {
        stop()

        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val actualBufferSize = maxOf(minBufferSize, BUFFER_SIZE)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(actualBufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        playbackJob = playerScope.launch {
            val random = Random()
            val buffer = ShortArray(BUFFER_SIZE)
            var brownVal = 0.0
            var phase = 0.0
            var wavePhase = 0.0

            while (isActive) {
                when (sound) {
                    AmbientSound.RAIN -> {
                        // Soft Rain: Low-pass filtered noise with droplet transients
                        for (i in 0 until BUFFER_SIZE) {
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            brownVal = (brownVal * 0.94) + (white * 0.06)
                            var sample = brownVal * 0.35

                            // Random droplet
                            if (random.nextInt(12000) == 0) {
                                sample += (random.nextDouble() * 0.5 - 0.25)
                            }
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    AmbientSound.WHITE_NOISE -> {
                        // Gentle Pink Noise
                        var b0 = 0.0; var b1 = 0.0; var b2 = 0.0
                        for (i in 0 until BUFFER_SIZE) {
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            b0 = 0.99886 * b0 + white * 0.0555179
                            b1 = 0.99332 * b1 + white * 0.0750759
                            b2 = 0.96900 * b2 + white * 0.1538520
                            val pink = (b0 + b1 + b2 + white * 0.5362) * 0.12
                            buffer[i] = (pink * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    AmbientSound.WAVES -> {
                        // Ocean waves: Slow periodic swell (0.15 Hz) over deep noise
                        for (i in 0 until BUFFER_SIZE) {
                            wavePhase += 2 * PI * 0.12 / SAMPLE_RATE
                            if (wavePhase > 2 * PI) wavePhase -= 2 * PI
                            val swell = (sin(wavePhase) * 0.5 + 0.5) * 0.6 + 0.2

                            val white = (random.nextDouble() * 2.0 - 1.0)
                            brownVal = (brownVal * 0.96) + (white * 0.04)
                            val sample = brownVal * swell * 0.55
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    AmbientSound.FOREST -> {
                        // Forest Zen: subtle wind breeze with gentle low resonance
                        for (i in 0 until BUFFER_SIZE) {
                            phase += 2 * PI * 180.0 / SAMPLE_RATE
                            if (phase > 2 * PI) phase -= 2 * PI
                            val drone = sin(phase) * 0.05

                            val white = (random.nextDouble() * 2.0 - 1.0)
                            brownVal = (brownVal * 0.97) + (white * 0.03)
                            val sample = (brownVal * 0.35 + drone)
                            buffer[i] = (sample * Short.MAX_VALUE).toInt()
                                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }
                    AmbientSound.NONE -> {
                        buffer.fill(0)
                    }
                }
                track.write(buffer, 0, BUFFER_SIZE)
            }
        }
    }
}
