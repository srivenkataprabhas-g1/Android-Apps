package com.prabhas.bookshelfapp

import android.app.Application
import com.prabhas.bookshelfapp.data.AppContainer

class BookshelfApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}