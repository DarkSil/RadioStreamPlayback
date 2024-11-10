package com.sli.radiostreamplayback.main.model

import com.google.gson.annotations.SerializedName

data class RadioList(
    @SerializedName("data")
    val radioList : List<RadioStation>
) {
    private var tagsList : List<String> = arrayListOf()
        get() {
            // It will be null due to init skip while serializing
            if (field.isNullOrEmpty()) {
                val set = mutableSetOf<String>()
                radioList.forEach {
                    set.addAll(it.tags)
                }
                field = set.toList()
            }
            return field
        }

    // Getting tags with no repeats at first time we call this function
    // I thought to put it into the model but I don't want to
    // 1. Do another loop with LiveData in both ViewModel and Model
    // 2. Save this tags inside of model and rewrite them every time it updates
    fun getTags() : List<String> {
        return tagsList
    }
}
