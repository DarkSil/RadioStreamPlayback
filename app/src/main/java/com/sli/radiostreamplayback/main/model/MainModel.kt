package com.sli.radiostreamplayback.main.model

import retrofit2.Call

interface MainModel {
    fun getRadioList() : Call<RadioList>
    fun sortListBy(list: List<RadioStation>, sortType: SortType, tags: List<String>? = null) : List<RadioStation>
}