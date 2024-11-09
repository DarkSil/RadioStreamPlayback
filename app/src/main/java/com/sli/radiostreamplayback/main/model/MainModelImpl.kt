package com.sli.radiostreamplayback.main.model

import retrofit2.Call
import java.util.Locale
import javax.inject.Inject

class MainModelImpl @Inject constructor(
    private val apiService: ApiService
) : MainModel {

    override fun getRadioList(): Call<RadioList> {
        return apiService.listOfStations()
    }

    override fun filterListByTags(
        list: List<RadioStation>,
        tags: TagsList
    ): List<RadioStation> {
        val selectedTags = tags.tagsList.filter { it.isSelected }
        return if (selectedTags.isNotEmpty()) {
            list.filter { radioStation ->
                radioStation.tags.any { tag -> selectedTags.any { it.tag == tag } }
            }
        } else {
            list
        }
    }

    override fun sortListBy(
        list: List<RadioStation>,
        sortType: SortType
    ): List<RadioStation> {
        val sortedList = when (sortType) {
            SortType.NAME -> list.sortedBy { it.name.lowercase(Locale.ROOT) }
            SortType.RELIABILITY_ASC -> list.sortedBy { it.reliability }
            SortType.RELIABILITY_DESC -> list.sortedByDescending { it.reliability }
            SortType.POPULARITY_ASC -> list.sortedBy { it.popularity }
            SortType.POPULARITY_DESC -> list.sortedByDescending { it.popularity }
        }
        return sortedList
    }
}