package com.example.audio

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object WavAudioGenerator {

    private const val SAMPLE_RATE = 44100
    private const val NUM_CHANNELS = 1
    private const val BITS_PER_SAMPLE = 16

    fun ensureSoundFiles(context: Context): Map<String, File> {
        val soundDir = File(context.cacheDir, "sounds").apply { mkdirs() }
        val soundFiles = mutableMapOf<String, File>()

        val filesToGenerate = listOf(
            "sound_start.wav" to ::generateStartTone,
            "sound_pause.wav" to ::generatePauseTone,
            "sound_click.wav" to ::generateClickTone,
            "sound_alarm_digital_bell.wav" to ::generateDigitalBellTone,
            "sound_alarm_wind.wav" to ::generateWindChimesTone,
            "sound_alarm_lofi.wav" to ::generateLofiTone,
            "sound_alarm_minimal.wav" to ::generateMinimalPingTone
        )

        for ((filename, generator) in filesToGenerate) {
            val file = File(soundDir, filename)
            if (!file.exists() || file.length() < 100) {
                try {
                    val pcmData = generator()
                    writeWavFile(file, pcmData, SAMPLE_RATE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            soundFiles[filename.removeSuffix(".wav")] = file
        }

        return soundFiles
    }

    private fun generateStartTone(): ShortArray {
        // C5 (523.25 Hz) for 0.15s, then E5 (659.25 Hz) for 0.35s with smooth envelope
        val durationSec = 0.55
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val split = (SAMPLE_RATE * 0.15).toInt()
        for (i in 0 until split) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = (1.0 - exp(-i / (SAMPLE_RATE * 0.02))) * (1.0 - i.toDouble() / split)
            val freq = 523.25
            val s = (sin(2 * PI * freq * t) * 0.7 + sin(2 * PI * freq * 2 * t) * 0.2) * env
            samples[i] = (s * Short.MAX_VALUE * 0.6).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        for (i in split until numSamples) {
            val localI = i - split
            val t = localI.toDouble() / SAMPLE_RATE
            val localDuration = durationSec - 0.15
            val env = exp(-localI / (SAMPLE_RATE * 0.18))
            val freq = 659.25
            val s = (sin(2 * PI * freq * t) * 0.75 + sin(2 * PI * freq * 2 * t) * 0.15) * env
            samples[i] = (s * Short.MAX_VALUE * 0.7).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return samples
    }

    private fun generatePauseTone(): ShortArray {
        // Soft descending tone: E5 (659Hz) to C5 (523Hz)
        val durationSec = 0.4
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val freq = 659.25 - progress * 136.0
            val env = exp(-i / (SAMPLE_RATE * 0.12))
            val s = sin(2 * PI * freq * t) * env
            samples[i] = (s * Short.MAX_VALUE * 0.5).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateClickTone(): ShortArray {
        // Short subtle 15ms click
        val durationSec = 0.02
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = 1.0 - (i.toDouble() / numSamples)
            val s = sin(2 * PI * 1200.0 * t) * env
            samples[i] = (s * Short.MAX_VALUE * 0.3).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateDigitalBellTone(): ShortArray {
        // 3-note ascending chime: C5 (523Hz) -> G5 (784Hz) -> C6 (1046Hz)
        val durationSec = 1.4
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val notes = listOf(
            Triple(0.00, 523.25, 0.4),
            Triple(0.20, 783.99, 0.5),
            Triple(0.45, 1046.50, 0.95)
        )

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sampleVal = 0.0

            for ((startT, freq, dur) in notes) {
                if (t >= startT && t < startT + dur) {
                    val noteT = t - startT
                    val env = exp(-noteT / (dur * 0.35))
                    // Pure bell harmonics
                    val tone = sin(2 * PI * freq * noteT) * 0.6 +
                            sin(2 * PI * freq * 2.0 * noteT) * 0.25 +
                            sin(2 * PI * freq * 3.0 * noteT) * 0.1
                    sampleVal += tone * env * 0.45
                }
            }
            samples[i] = (sampleVal * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateWindChimesTone(): ShortArray {
        // Pentatonic ethereal chime series
        val durationSec = 1.6
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val freqs = listOf(587.33, 659.25, 880.0, 987.77, 1174.66)
        val startTimes = listOf(0.0, 0.15, 0.32, 0.48, 0.65)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sampleVal = 0.0

            for (idx in freqs.indices) {
                val startT = startTimes[idx]
                val freq = freqs[idx]
                if (t >= startT) {
                    val noteT = t - startT
                    val env = exp(-noteT / 0.35)
                    val tone = sin(2 * PI * freq * noteT) * 0.8 + sin(2 * PI * freq * 2.76 * noteT) * 0.2
                    sampleVal += tone * env * 0.25
                }
            }
            samples[i] = (sampleVal * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateLofiTone(): ShortArray {
        // Warm soft Rhodes / lofi bell chord (Major 7th)
        val durationSec = 1.5
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        val chord = listOf(392.0, 493.88, 587.33, 739.99) // G4, B4, D5, F#5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t / 0.5) * (1.0 - exp(-t / 0.01))
            var sampleVal = 0.0
            for (freq in chord) {
                sampleVal += sin(2 * PI * freq * t) * 0.25
            }
            samples[i] = (sampleVal * env * Short.MAX_VALUE * 0.8).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateMinimalPingTone(): ShortArray {
        // Sharp high-tech minimal crystal ping (880 Hz)
        val durationSec = 0.8
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t / 0.15)
            val s = (sin(2 * PI * 880.0 * t) * 0.8 + sin(2 * PI * 1760.0 * t) * 0.2) * env
            samples[i] = (s * Short.MAX_VALUE * 0.7).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
        val totalAudioLen = (pcmData.size * 2).toLong()
        val totalDataLen = totalAudioLen + 36
        val byteRate = (sampleRate * NUM_CHANNELS * BITS_PER_SAMPLE / 8).toLong()

        val header = ByteArray(44)
        val buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalDataLen.toInt())
        buffer.put("WAVE".toByteArray())

        // fmt chunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size (16 for PCM)
        buffer.putShort(1.toShort()) // AudioFormat (1 for PCM)
        buffer.putShort(NUM_CHANNELS.toShort())
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate.toInt())
        buffer.putShort((NUM_CHANNELS * BITS_PER_SAMPLE / 8).toShort()) // BlockAlign
        buffer.putShort(BITS_PER_SAMPLE.toShort())

        // data chunk
        buffer.put("data".toByteArray())
        buffer.putInt(totalAudioLen.toInt())

        FileOutputStream(file).use { fos ->
            fos.write(header)
            val audioBuffer = ByteBuffer.allocate(pcmData.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in pcmData) {
                audioBuffer.putShort(sample)
            }
            fos.write(audioBuffer.array())
        }
    }
}
