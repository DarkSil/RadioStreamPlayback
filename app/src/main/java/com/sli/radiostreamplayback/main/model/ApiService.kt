package com.sli.radiostreamplayback.main.model

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("cdn-web.tunein.com/stations.json")
    fun listOfStations() : Call<RadioList>
}