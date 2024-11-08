package com.sli.radiostreamplayback.base

sealed class Reason(val reason: String? = null) {

    data object Unknown : Reason()
    data object Internet : Reason()
    data class Specified(private val text: String) : Reason(text)

}