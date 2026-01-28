package com.cafeteria.verdadeoudesafio.managers

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log
import com.cafeteria.verdadeoudesafio.R

class AudioManager private constructor(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private val soundMap = mutableMapOf<SoundEffect, Int>()

    var soundEnabled = true
    var musicEnabled = true
    var soundVolume = 0.7f
    var musicVolume = 0.5f

    enum class SoundEffect {
        CLICK,
        SPIN_START,
        SPIN_LOOP,
        SUCCESS,
        FAIL,
        POINT_GAIN
    }

    companion object {
        @Volatile
        private var instance: AudioManager? = null

        fun getInstance(context: Context): AudioManager {
            return instance ?: synchronized(this) {
                instance ?: AudioManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        initSoundPool()
        loadSounds()
    }

    private fun initSoundPool() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build()
        } else {
            @Suppress("DEPRECATION")
            SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0)
        }
    }

    private fun loadSounds() {
        try {
            soundMap[SoundEffect.CLICK] = soundPool?.load(context, R.raw.click, 1) ?: 0
            soundMap[SoundEffect.SPIN_START] = soundPool?.load(context, R.raw.spin_start, 1) ?: 0
            soundMap[SoundEffect.SUCCESS] = soundPool?.load(context, R.raw.success, 1) ?: 0
            soundMap[SoundEffect.FAIL] = soundPool?.load(context, R.raw.fail, 1) ?: 0
            soundMap[SoundEffect.POINT_GAIN] = soundPool?.load(context, R.raw.point_gain, 1) ?: 0

            Log.d("AudioManager", "Sons carregados (ou prontos para carregar)")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao carregar sons", e)
        }
    }

    fun playSound(effect: SoundEffect) {
        if (!soundEnabled) return

        try {
            soundMap[effect]?.let { soundId ->
                if (soundId != 0) {
                    soundPool?.play(soundId, soundVolume, soundVolume, 1, 0, 1.0f)
                    Log.d("AudioManager", "Tocando som: $effect")
                } else {
                    Log.w("AudioManager", "Som $effect não carregado (soundId = 0)")
                }
            } ?: Log.w("AudioManager", "Som $effect não encontrado no mapa")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao tocar som $effect", e)
        }
    }

    fun startBackgroundMusic(resourceId: Int) {
        if (!musicEnabled) return

        stopBackgroundMusic()

        try {
            mediaPlayer = MediaPlayer.create(context, resourceId).apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
                start()
            }
            Log.d("AudioManager", "Música de fundo iniciada")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao iniciar música", e)
        }
    }

    fun startBackgroundMusicFromAsset(assetPath: String) {
        if (!musicEnabled) return

        stopBackgroundMusic()

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                val descriptor = context.assets.openFd(assetPath)
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                descriptor.close()

                prepare()
                isLooping = true
                setVolume(musicVolume, musicVolume)
                start()
            }
            Log.d("AudioManager", "Música de fundo iniciada de asset")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao iniciar música de asset", e)
        }
    }

    fun stopBackgroundMusic() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            Log.d("AudioManager", "Música de fundo parada")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao parar música", e)
        }
    }

    fun pauseBackgroundMusic() {
        try {
            mediaPlayer?.pause()
            Log.d("AudioManager", "Música pausada")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao pausar música", e)
        }
    }

    fun resumeBackgroundMusic() {
        if (musicEnabled) {
            try {
                mediaPlayer?.start()
                Log.d("AudioManager", "Música retomada")
            } catch (e: Exception) {
                Log.e("AudioManager", "Erro ao retomar música", e)
            }
        }
    }

    fun updateSoundVolume(volume: Float) {
        soundVolume = volume.coerceIn(0f, 1f)
        Log.d("AudioManager", "Volume de som atualizado: $soundVolume")
    }

    fun updateMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(musicVolume, musicVolume)
        Log.d("AudioManager", "Volume de música atualizado: $musicVolume")
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            stopBackgroundMusic()
            Log.d("AudioManager", "AudioManager liberado")
        } catch (e: Exception) {
            Log.e("AudioManager", "Erro ao liberar recursos", e)
        }
    }
}