package com.yuksholat.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Menghasilkan efek suara chiptune 8-bit (Square wave & Triangle wave) langsung via PCM AudioTrack
 * tanpa membutuhkan file aset audio eksternal.
 */
object RetroSoundSynthesizer {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 44100

    var isSoundEnabled: Boolean = true

    /**
     * Efek suara koin / poin didapat: 2 nada cepat naik (b5 -> E6)
     */
    fun playCoinSound() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val note1 = generateSquareWave(frequency = 987.77, durationMs = 60, amplitude = 0.6f)
            val note2 = generateSquareWave(frequency = 1318.51, durationMs = 120, amplitude = 0.6f)
            val buffer = note1 + note2
            playPcmAudio(buffer)
        }
    }

    /**
     * Efek suara Level Up / Naik Tier: Arpeggio megah 8-bit (C5 -> E5 -> G5 -> C6)
     */
    fun playLevelUpFanfare() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val c5 = generateSquareWave(523.25, 80, 0.7f)
            val e5 = generateSquareWave(659.25, 80, 0.7f)
            val g5 = generateSquareWave(783.99, 80, 0.7f)
            val c6 = generateSquareWave(1046.50, 240, 0.8f)
            val buffer = c5 + e5 + g5 + c6
            playPcmAudio(buffer)
        }
    }

    /**
     * Efek suara tombol / klik retro: nada pendek cepat (440Hz pop)
     */
    fun playButtonClick() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val click = generateSquareWave(frequency = 440.0, durationMs = 25, amplitude = 0.4f)
            playPcmAudio(click)
        }
    }

    /**
     * Efek suara azan / chime adzan 8-bit: melodi harmonis segitiga menenangkan
     */
    fun playPrayerChime() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val n1 = generateTriangleWave(440.0, 180, 0.6f)
            val n2 = generateTriangleWave(554.37, 180, 0.6f)
            val n3 = generateTriangleWave(659.25, 250, 0.6f)
            val n4 = generateTriangleWave(880.0, 400, 0.7f)
            val buffer = n1 + n2 + n3 + n4
            playPcmAudio(buffer)
        }
    }

    fun playTransformCharge() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val c5 = generateSquareWave(523.25, 80, 0.7f)
            val e5 = generateSquareWave(659.25, 80, 0.7f)
            val g5 = generateSquareWave(783.99, 80, 0.7f)
            val c6Num = (SAMPLE_RATE * 0.2).toInt()
            val c6 = ShortArray(c6Num)
            var phase = 0.0
            for (i in 0 until c6Num) {
                val vibrato = 1.0 + 0.02 * kotlin.math.sin(2.0 * kotlin.math.PI * 6.0 * i / SAMPLE_RATE)
                phase += 2.0 * kotlin.math.PI * 1046.50 * vibrato / SAMPLE_RATE
                val s = if (kotlin.math.sin(phase) >= 0) 1 else -1
                val decay = 1.0f - (i.toFloat() / c6Num) * 0.3f
                c6[i] = (s * Short.MAX_VALUE * 0.8f * decay).toInt().toShort()
            }
            val buffer = c5 + e5 + g5 + c6
            playPcmAudio(buffer)
        }
    }

    fun playTransformFlash() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val noise = generateWhiteNoise(50, 0.5f)
            val bass = generateSquareWave(130.81, 100, 0.8f)
            val buffer = noise + bass
            playPcmAudio(buffer)
        }
    }

    fun playRamadanFanfare() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val d5 = generateTriangleWave(587.33, 150, 0.6f)
            val fs5 = generateTriangleWave(739.99, 150, 0.6f)
            val a5 = generateTriangleWave(880.0, 150, 0.6f)
            val d6 = generateTriangleWave(1174.66, 300, 0.7f)
            val buffer = d5 + fs5 + a5 + d6
            playPcmAudio(buffer)
        }
    }

    fun playStreakAlert() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val e5 = generateSquareWave(659.25, 60, 0.6f)
            val b5 = generateSquareWave(987.77, 60, 0.6f)
            val buffer = e5 + b5
            playPcmAudio(buffer)
        }
    }

    private fun generateWhiteNoise(durationMs: Int, amplitude: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        return ShortArray(numSamples) { (Short.MAX_VALUE * amplitude * (kotlin.random.Random.nextFloat() * 2 - 1)).toInt().toShort() }
    }

    private fun generateSquareWave(frequency: Double, durationMs: Int, amplitude: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        val period = (SAMPLE_RATE / frequency).toInt()

        for (i in 0 until numSamples) {
            val decay = 1.0f - (i.toFloat() / numSamples.toFloat()) * 0.3f
            val value = if ((i % period) < (period / 2)) {
                (Short.MAX_VALUE * amplitude * decay).toInt().toShort()
            } else {
                (-Short.MAX_VALUE * amplitude * decay).toInt().toShort()
            }
            buffer[i] = value
        }
        return buffer
    }

    private fun generateTriangleWave(frequency: Double, durationMs: Int, amplitude: Float): ShortArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val raw = 2.0 * kotlin.math.abs(2.0 * (t * frequency - kotlin.math.floor(t * frequency + 0.5))) - 1.0
            val decay = 1.0f - (i.toFloat() / numSamples.toFloat()) * 0.4f
            buffer[i] = (raw * Short.MAX_VALUE * amplitude * decay).toInt().toShort()
        }
        return buffer
    }

    private fun playPcmAudio(pcmData: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcmData.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcmData, 0, pcmData.size)
            audioTrack.play()
            
            audioScope.launch {
                val durationMs = (pcmData.size * 1000L) / SAMPLE_RATE
                kotlinx.coroutines.delay(durationMs + 100)
                audioTrack.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}