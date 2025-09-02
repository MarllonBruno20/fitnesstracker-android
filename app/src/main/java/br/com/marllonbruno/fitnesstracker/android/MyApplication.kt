package br.com.marllonbruno.fitnesstracker.android

import android.app.Application
import br.com.marllonbruno.fitnesstracker.android.di.AppContainer
import br.com.marllonbruno.fitnesstracker.android.di.DefaultAppContainer

class MyApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}