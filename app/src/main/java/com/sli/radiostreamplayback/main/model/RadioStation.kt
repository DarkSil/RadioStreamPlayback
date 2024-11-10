package com.sli.radiostreamplayback.main.model

import java.io.Serializable

data class RadioStation(
    val id : String,
    val description : String,
    val name : String,
    val imgUrl : String,
    val streamUrl : String,
    val reliability : Int,
    val popularity : Float,
    val tags : List<String>
) : Serializable
