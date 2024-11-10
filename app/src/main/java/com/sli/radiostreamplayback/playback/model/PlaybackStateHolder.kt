package com.sli.radiostreamplayback.playback.model

import androidx.lifecycle.MutableLiveData
import com.sli.radiostreamplayback.main.model.RadioStation

object PlaybackStateHolder {
    val liveStationNow = MutableLiveData<RadioStation?>()
    var isActivityAlive : Boolean = false
}