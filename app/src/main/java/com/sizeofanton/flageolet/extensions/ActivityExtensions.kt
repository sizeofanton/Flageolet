package com.sizeofanton.flageolet.extensions

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

fun AppCompatActivity.playSound(resId: Int) {
    val mediaPlayer = MediaPlayer.create(this, resId)
    mediaPlayer.setAudioAttributes(
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    )
    mediaPlayer.start()
}