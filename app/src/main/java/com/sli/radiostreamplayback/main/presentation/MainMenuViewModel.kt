package com.sli.radiostreamplayback.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.base.Reason
import com.sli.radiostreamplayback.main.model.MainModel
import com.sli.radiostreamplayback.main.model.MainState
import com.sli.radiostreamplayback.main.model.RadioList
import com.sli.radiostreamplayback.main.model.SortType
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

    fun getListOfStations() : LiveData<MainState> {
        if (listOfStations.value == null) {
            listOfStations.value = MainState(progress = true)

            mainModel.getRadioList().enqueue(object : Callback<RadioList> {
                override fun onResponse(request: Call<RadioList>, response: Response<RadioList>) {

                    val radioList = response.body()
                    if (response.isSuccessful && radioList != null) {
                        listOfStations.value = MainState(radioList = radioList)
                    } else {
                        throwError()
                    }
                }

                override fun onFailure(request: Call<RadioList>, throwable: Throwable) {
                    throwError(Reason.Internet)
                }
            })
        }
        return listOfStations
    }

    fun getSortedListBy(sortType: SortType, tags: List<String>? = null) : List<RadioList> {
        val sortedList = arrayListOf<RadioList>()
        listOfStations.value?.radioList?.let {
            mainModel.sortListBy(it.radioList, sortType, tags)
        }
        return sortedList
    }

    private fun throwError(reason: Reason? = null) {
        listOfStations.value = MainState(error = reason ?: Reason.Unknown)
    }

    // TODO fun updateListOfStations()

}