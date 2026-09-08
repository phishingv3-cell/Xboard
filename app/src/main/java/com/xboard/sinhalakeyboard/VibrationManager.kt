package com.xboard.sinhalakeyboard

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibrationManager(private val context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    // Key එකක් එබූ විට පොඩි Vibration එකක් ලබා දීම
    fun vibrateKeyClick(durationMs: Long = 25) { // 20-30ms අතර ප්‍රමාණය ඉතා සුදුසුයි
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 (API 26) සහ ඊට ඉහළ සඳහා
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // පැරණි Android Versions සඳහා
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
