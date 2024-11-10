package com.sli.radiostreamplayback.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.playback.model.RadioService
import com.sli.radiostreamplayback.playback.model.ServiceAction
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.RADIO_ITEM

object PlaybackStateUtils {
    val liveStationNow = MutableLiveData<RadioStation?>()
    var isActivityAlive : Boolean = false

    fun observePlaybackStatus(observer: Observer<RadioStation?>) : RadioStation? {
        liveStationNow.observeForever(observer)
        return liveStationNow.value
    }

    fun removeObserver(observer: Observer<RadioStation?>) {
        liveStationNow.removeObserver(observer)
    }

    fun sendActionToService(shouldPlay: Boolean, context: Context, radioStation: RadioStation) {
        val action = if (shouldPlay) ServiceAction.PLAY.action else ServiceAction.PAUSE.action

        val intent = Intent(context, RadioService::class.java).apply {
            this.action = action
            putExtras(bundleOf(RADIO_ITEM to radioStation))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else{
            context.startService(intent)
        }
    }
}