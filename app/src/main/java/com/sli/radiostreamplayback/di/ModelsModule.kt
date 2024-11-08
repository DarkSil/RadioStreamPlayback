package com.sli.radiostreamplayback.di

import com.sli.radiostreamplayback.main.model.MainModel
import com.sli.radiostreamplayback.main.model.MainModelImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ModelsModule {

    @Binds
    abstract fun provideMainModel(mainModelImpl: MainModelImpl) : MainModel

}