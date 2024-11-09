package com.sli.radiostreamplayback.main.model

import java.io.Serializable

data class Tag (
    val tag: String,
    var isSelected: Boolean
) : Serializable
