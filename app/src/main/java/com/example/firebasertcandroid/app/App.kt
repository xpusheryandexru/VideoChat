package com.example.firebasertcandroid.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Inject
import javax.inject.Singleton


@HiltAndroidApp
@InstallIn(SingletonComponent::class)
@Module
class App : Application() {

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

}

