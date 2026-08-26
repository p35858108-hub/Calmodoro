package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundManager private constructor(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val soundIdMap = mutableMapOf<String, Int>()
    private var isLoaded = false

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val soundFiles = WavAudioGenerator.ensureSoundFiles(context)
                for ((key, file) in soundFiles) {
                    if (file.exists()) {
                        val soundId = soundPool?.load(file.absolutePath, 1) ?: 0
                        if (soundId > 0) {
                            soundIdMap[key] = soundId
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playStart() {
        playSound("sound_start")
    }

    fun playPause() {
        playSound("sound_pause")
    }

    fun playClick() {
        playSound("sound_click", volume = 0.5f)
    }

    fun playFinish(soundChoice: String = "digital_bell") {
        val soundKey = when (soundChoice) {
            "wind" -> "sound_alarm_wind"
            "lofi" -> "sound_alarm_lofi"
            "minimal" -> "sound_alarm_minimal"
            else -> "sound_alarm_digital_bell"
        }
        playSound(soundKey, volume = 1.0f)
    }

    fun previewSound(soundChoice: String) {
        playFinish(soundChoice)
    }

    private fun playSound(key: String, volume: Float = 0.9f) {
        val soundId = soundIdMap[key]
        if (soundId != null && soundId > 0) {
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIdMap.clear()
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SoundManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
