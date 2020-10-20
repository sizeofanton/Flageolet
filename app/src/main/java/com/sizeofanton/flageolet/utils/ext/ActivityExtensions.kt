package com.sizeofanton.flageolet.utils.ext

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
fun AppCompatActivity.vibratePhone() {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26)
        vibrator.vibrate(
            VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    else
        vibrator.vibrate(200)
}