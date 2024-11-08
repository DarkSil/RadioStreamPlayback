package com.sli.radiostreamplayback.main.model

import retrofit2.Call
import javax.inject.Inject

class MainModelImpl @Inject constructor(
    private val apiService: ApiService
) : MainModel {

    override fun getRadioList(): Call<RadioList> {
        return apiService.listOfStations()
    }

    override fun sortListBy(
        list: List<RadioStation>,
        sortType: SortType,
        tags: List<String>?
    ): List<RadioStation> {
        val sortedList = when (sortType) {
            SortType.NAME -> list.sortedBy { it.name }
            SortType.RELIABILITY_ASC -> list.sortedBy { it.reliability }
            SortType.RELIABILITY_DESC -> list.sortedByDescending { it.reliability }
            SortType.POPULARITY_ASC -> list.sortedBy { it.popularity }
            SortType.POPULARITY_DESC -> list.sortedByDescending { it.popularity }
            SortType.TAG -> {
                if (tags != null) {
                    list.filter { radioStation ->
                        radioStation.tags.any { tag -> tags.contains(tag) }
                    }
                } else {
                    null
                }
            }
        }
        return sortedList ?: emptyList()
    }
}