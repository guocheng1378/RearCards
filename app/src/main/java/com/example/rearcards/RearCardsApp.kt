package com.example.rearcards

import android.app.Application
import hk.uwu.reareye.widgetapi.RearWidgetApiClient

class RearCardsApp : Application() {
    val widgetClient = RearWidgetApiClient()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RearCardsApp
            private set
    }
}
