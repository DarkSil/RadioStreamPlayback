package com.sli.radiostreamplayback.playback.model

sealed class ServiceAction(val action: String, open val error: String? = null) {
    data object PLAY : ServiceAction("com.sli.radiostreamplayback.ACTION_PLAY")
    data object PAUSE : ServiceAction("com.sli.radiostreamplayback.ACTION_PAUSE")
    data object STOP : ServiceAction("com.sli.radiostreamplayback.ACTION_STOP")
    data object OPEN : ServiceAction("com.sli.radiostreamplayback.ACTION_OPEN")
    data object ERROR : ServiceAction("com.sli.radiostreamplayback.ACTION_ERROR") {
            val key: String = "error"
        }
}