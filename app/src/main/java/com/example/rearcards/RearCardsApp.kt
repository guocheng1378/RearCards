package com.example.rearcards

import android.app.Application
import hk.uwu.reareye.widgetapi.RearWidgetApiClient
import hk.uwu.reareye.widgetapi.RearWidgetApiContract

class RearCardsApp : Application() {
    val widgetClient = RearWidgetApiClient(hookHostPackage = RearWidgetApiContract.HOOK_HOST_PACKAGE)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RearCardsApp
            private set
    }
}
