package com.example.animalobserving

import android.app.Application
import com.example.animalobserving.data.AppContainer
import com.example.animalobserving.data.DefaultAppContainer

//import com.example.animalobserving.data.DefaultAppContainer

class AnimalObservingApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}