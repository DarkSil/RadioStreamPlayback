package com.sli.radiostreamplayback.playback.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.utils.PlaybackStateUtils

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
        val liveStation = PlaybackStateUtils.observePlaybackStatus(observer)

        if (liveStation == null) {
            isPlaying.value = false
        }

        return isPlaying
    }

    fun playPause(context: Context) {
        radioStation?.let {
            PlaybackStateUtils.sendActionToService(isPlaying.value != true, context, it)
        }
    }

    override fun onCleared() {
        PlaybackStateUtils.removeObserver(observer)
        super.onCleared()
    }

}