package com.manoj.mqttdemo.presentation.view.main

import com.manoj.base.domain.repositary.BaseRepo
import com.manoj.base.network.helper.MqttClientHelper.Companion.MQTT_CLIENT_HELPER
import com.manoj.base.presentation.common.Logger
import com.manoj.base.presentation.common.base.BaseViewModel
import com.manoj.base.presentation.common.helper.Resource
import com.manoj.base.presentation.common.helper.SingleRequestEvent
import com.manoj.mqttdemo.MyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val baseRepo: BaseRepo) : BaseViewModel() {
    val topicSubscription: SingleRequestEvent<Boolean> = SingleRequestEvent()


    fun subscribeToTopic() {
        MyApplication.instance.mqttClientHelper.subscribe(
            "Manoj",
            isSuccess = { topicSubscription.postValue(Resource.success(it)) })
    }
    override fun onCleared() {
        super.onCleared()
        MyApplication.instance.mqttClientHelper.destroy()
        Logger.d(MQTT_CLIENT_HELPER, "onDestroy")
    }
}