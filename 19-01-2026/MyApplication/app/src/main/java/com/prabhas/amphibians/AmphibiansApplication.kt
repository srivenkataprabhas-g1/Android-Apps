package com.prabhas.amphibians

import android.app.Application
import com.prabhas.amphibians.data.AppContainer
import com.prabhas.amphibians.data.DefaultContainer

class AmphibiansApplication : Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultContainer()
    }
}