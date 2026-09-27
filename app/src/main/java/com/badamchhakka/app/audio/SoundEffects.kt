package com.badamchhakka.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundEffects(context: Context) {
    var enabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    private fun playTone(freqHz: Double, durationMs: Int) {
        if (!enabled) return
        audioScope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (durationMs * sampleRate / 1000)
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
                    val sample = (sin(angle) * Short.MAX_VALUE * 0.4).toInt()
                    buffer[i] = sample.toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                track.setNotificationMarkerPosition(numSamples)
                track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onPeriodicNotification(t: AudioTrack?) {}
                    override fun onMarkerReached(t: AudioTrack?) {
                        t?.release()
                    }
                })
            } catch (_: Exception) {
                // Ignore audio generation failures gracefully
            }
        }
    }

    fun playCardClack() {
        playTone(520.0, 45)
        vibrate(20)
    }

    fun playPassSound() {
        playTone(330.0, 70)
        vibrate(35)
    }

    fun playInvalid() {
        playTone(180.0, 110)
        vibrate(60)
    }

    fun playClick() {
        playTone(880.0, 25)
        vibrate(15)
    }

    fun playCoinCollect() {
        playTone(1046.5, 60)
        vibrate(30)
    }

    fun playWin() {
        audioScope.launch {
            playTone(523.25, 100) // C5
            kotlinx.coroutines.delay(120)
            playTone(659.25, 100) // E5
            kotlinx.coroutines.delay(120)
            playTone(783.99, 100) // G5
            kotlinx.coroutines.delay(120)
            playTone(1046.50, 250) // C6
        }
        vibrate(100)
    }

    private fun vibrate(durationMs: Long) {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
