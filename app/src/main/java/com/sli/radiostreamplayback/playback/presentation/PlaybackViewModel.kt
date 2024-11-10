package com.sli.radiostreamplayback.playback.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.playback.model.PlaybackStateHolder
import com.sli.radiostreamplayback.playback.model.RadioService
import com.sli.radiostreamplayback.playback.model.ServiceAction
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.RADIO_ITEM

class PlaybackViewModel : ViewModel() {

    private val isPlaying = MutableLiveData<Boolean>()
    private var radioStation : RadioStation? = null
    private val observer = Observer<RadioStation?> { liveStation ->
        radioStation?.let { selectedStation ->
            if (liveStation == null) {
                isPlaying.value = false
            } else {
                isPlaying.value = selectedStation.id == liveStation.id
            }
        }
    }

    fun getStationPlaybackStatus(radioStation: RadioStation) : LiveData<Boolean> {
        this.radioStation = radioStation
        val liveStation = PlaybackStateHolder.liveStationNow
        liveStation.observeForever(observer)

        if (liveStation.value == null) {
            isPlaying.value = false
        }

        return isPlaying
    }

    fun playPause(context: Context) {
        val action = if (isPlaying.value == true) {
            ServiceAction.PAUSE.action
        } else {
            ServiceAction.PLAY.action
        }
        sendActionToService(action, context)
    }

    private fun sendActionToService(action: String, context: Context) {
        radioStation?.let {
            val intent = Intent(context, RadioService::class.java).apply {
                this.action = action
                putExtras(bundleOf(RADIO_ITEM to it))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else{
                context.startService(intent)
            }
        }
    }

    override fun onCleared() {
        PlaybackStateHolder.liveStationNow.removeObserver(observer)
        super.onCleared()
    }

}