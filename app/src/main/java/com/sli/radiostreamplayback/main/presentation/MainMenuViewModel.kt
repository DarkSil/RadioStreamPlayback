package com.sli.radiostreamplayback.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.base.Reason
import com.sli.radiostreamplayback.main.model.MainModel
import com.sli.radiostreamplayback.main.model.MainState
import com.sli.radiostreamplayback.main.model.RadioList
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.main.model.SortResults
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.model.Tag
import com.sli.radiostreamplayback.main.model.TagsList
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val mainModel: MainModel
) : ViewModel() {

    private val listOfStations = MutableLiveData<MainState>()
    var tagsList: TagsList = TagsList(emptyList())
        private set
    var selectedSortType: SortType = SortType.NAME
        private set


    fun getListOfStations() : LiveData<MainState> {
        listOfStations.value = MainState(progress = true)

        mainModel.getRadioList().enqueue(object : Callback<RadioList> {
            override fun onResponse(request: Call<RadioList>, response: Response<RadioList>) {

                val radioList = response.body()
                if (response.isSuccessful && radioList != null) {
                    tagsList = TagsList(radioList.getTags().map { Tag(it, false) })

                    val filteredList = getFilteredListByTags(tagsList, radioList.radioList)
                    val sortedList = getSortedListBy(selectedSortType, filteredList)
                    listOfStations.value = MainState(
                        radioList = radioList.copy(radioList = sortedList)
                    )
                } else {
                    throwError()
                }
            }

            override fun onFailure(request: Call<RadioList>, throwable: Throwable) {
                throwError(Reason.Internet)
            }
        })

        return listOfStations
    }

    fun getSortedListBy(sortType: SortType, list: List<RadioStation>? = null) : List<RadioStation> {
        val sortedList = arrayListOf<RadioStation>()
        val selectedList = list ?: listOfStations.value?.radioList?.radioList
        selectedList?.let {
            sortedList.addAll(mainModel.sortListBy(it, sortType))
        }
        selectedSortType = sortType
        return sortedList
    }

    fun getFilteredListByTags(tags: TagsList, list: List<RadioStation>? = null): List<RadioStation> {
        tagsList = tags
        /*tagsList.tagsList.forEach { it.isSelected = false }
        tagsList.tagsList.filter { tag -> tags.tagsList.any { tag.tag == it.tag && it.isSelected } }
            .forEach {
                it.isSelected = true
            }*/

        val filteredList = arrayListOf<RadioStation>()
        val selectedList = list ?: listOfStations.value?.radioList?.radioList
        selectedList?.let {
            filteredList.addAll(mainModel.filterListByTags(it, tags))
        }
        return filteredList
    }

    fun getUpdatedList(sortResults: SortResults) : List<RadioStation> {
        val filteredList = getFilteredListByTags(sortResults.tags)
        val sortedList = getSortedListBy(sortResults.sortType, filteredList)
        return sortedList
    }

    private fun throwError(reason: Reason? = null) {
        listOfStations.value = MainState(error = reason ?: Reason.Unknown)
    }

}