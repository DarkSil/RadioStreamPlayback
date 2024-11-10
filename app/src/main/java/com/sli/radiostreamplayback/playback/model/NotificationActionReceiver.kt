package com.sli.radiostreamplayback.playback.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

// From Android 12 we can't start service from service
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val serviceIntent = Intent(context, RadioService::class.java).apply {
            this.action = action
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}