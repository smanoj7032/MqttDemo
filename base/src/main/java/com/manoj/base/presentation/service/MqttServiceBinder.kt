package com.manoj.base.presentation.service
import android.os.Binder

class MqttServiceBinder(val service: MqttService) : Binder() {

    var activityToken: String? = null

}