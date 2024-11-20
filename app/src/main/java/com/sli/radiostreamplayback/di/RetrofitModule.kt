package com.sli.radiostreamplayback.di

import com.sli.radiostreamplayback.main.model.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun providesRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://s3-us-west-1.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun providesApiService(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }

}