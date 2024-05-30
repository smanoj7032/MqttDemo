package com.manoj.mqttdemo

import android.app.Application
import android.content.Intent
import com.manoj.base.network.helper.MqttClientHelper
import com.manoj.mqttdemo.presentation.view.main.MainActivity
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    private var isRestarting: Boolean = false

    @Inject
    lateinit var mqttClientHelper: MqttClientHelper


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun onLogout() {
        restartApp()
    }

    companion object {
        @get:Synchronized
        lateinit var instance: MyApplication
    }

    private fun restartApp() {
        isRestarting = true
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isComeFrom", "Home")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}