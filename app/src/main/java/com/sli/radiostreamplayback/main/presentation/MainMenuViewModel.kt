package com.sli.radiostreamplayback.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.main.model.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val listOfStations = MutableLiveData<String>()

    fun getListOfStations() : LiveData<String> {
        if (listOfStations.value == null) {
            apiService.listOfStations().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(p0: Call<ResponseBody>, p1: Response<ResponseBody>) {
                    listOfStations.value = "RESPONSE ${p1.body()?.string()}"
                }

                override fun onFailure(p0: Call<ResponseBody>, p1: Throwable) {
                    listOfStations.value = "FAIl"
                }
            })
        }
        return listOfStations
    }

    // TODO fun updateListOfStations()

}