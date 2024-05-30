package com.manoj.base.network.helper

import android.content.Context
import android.util.Log
import com.manoj.base.network.helper.Constants.Companion.CLIENT_PASSWORD
import com.manoj.base.network.helper.Constants.Companion.CLIENT_USER_NAME
import com.manoj.base.network.helper.Constants.Companion.CONNECTION_CLEAN_SESSION
import com.manoj.base.network.helper.Constants.Companion.CONNECTION_KEEP_ALIVE_INTERVAL
import com.manoj.base.network.helper.Constants.Companion.CONNECTION_RECONNECT
import com.manoj.base.network.helper.Constants.Companion.CONNECTION_TIMEOUT
import com.manoj.base.network.helper.Constants.Companion.MQTT_HOST
import com.manoj.base.presentation.common.Logger
import com.manoj.base.presentation.common.helper.Resource
import com.manoj.base.presentation.common.helper.SingleRequestEvent
import com.manoj.base.presentation.service.MqttAndroidClient
import dagger.hilt.android.qualifiers.ApplicationContext
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject


class MqttClientHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mqttAndroidClient: MqttAndroidClient
) {
    private val clientId: String = MqttClient.generateClientId()
    private val serverUri = MQTT_HOST
    val mqttCallback: SingleRequestEvent<Pair<String, String>> = SingleRequestEvent()

    companion object {
        const val MQTT_CLIENT_HELPER = "MqttClientHelper"
    }

    init {
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                Logger.e(MQTT_CLIENT_HELPER, "connectionLost: $cause")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                topic?.let {
                    Logger.e("Payload-->>", "messageArrived: $message Topic---->$it")
                    val data = Pair(it, message.toString())
                    mqttCallback.postValue(Resource.success(data))
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Logger.e(MQTT_CLIENT_HELPER, "deliveryComplete: $token")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Logger.e(MQTT_CLIENT_HELPER, "connectComplete: $reconnect")
            }

        })
    }

    fun connect(isSuccess: (Boolean) -> Unit) {
        val mqttClientOptions = MqttConnectOptions()
        mqttClientOptions.isCleanSession = CONNECTION_CLEAN_SESSION
        mqttClientOptions.isAutomaticReconnect = CONNECTION_RECONNECT
        mqttClientOptions.userName = CLIENT_USER_NAME
        mqttClientOptions.password = CLIENT_PASSWORD.toCharArray()
        mqttClientOptions.connectionTimeout = CONNECTION_TIMEOUT
        mqttClientOptions.keepAliveInterval = CONNECTION_KEEP_ALIVE_INTERVAL
        try {
            mqttAndroidClient.connect(mqttClientOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    isSuccess.invoke(true)
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    Logger.e(MQTT_CLIENT_HELPER, "onSuccess: ------->>")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    isSuccess.invoke(false)
                    Logger.d(MQTT_CLIENT_HELPER, "Failed to connect to: ${exception}")
                }

            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unSubscribe(subscriptionTopic: String) {
        mqttAndroidClient.unsubscribe(subscriptionTopic, null, object : IMqttActionListener {
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Logger.d(MQTT_CLIENT_HELPER, "failed to unSubscribed to topic '$subscriptionTopic'")
            }

            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Logger.d(MQTT_CLIENT_HELPER, "UnSubscribed to topic '$subscriptionTopic'")
            }
        })
    }

    fun subscribe(subscriptionTopic: String, isSuccess: (Boolean) -> Unit) {
        if (isConnected()) {
            try {
                mqttAndroidClient.subscribe(
                    subscriptionTopic,
                    0,
                    null,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            isSuccess.invoke(true)
                            Logger.d(MQTT_CLIENT_HELPER, "Subscribed to topic '$subscriptionTopic'")
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            isSuccess.invoke(false)
                            Logger.d(
                                MQTT_CLIENT_HELPER,
                                "Subscription to topic '$subscriptionTopic' failed!"
                            )

                        }
                    })
            } catch (e: MqttException) {
                System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
                e.printStackTrace()
            }
        } else {
            connect {
                if (it) {
                    subscribe(subscriptionTopic, isSuccess)
                }
            }
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(
                topic,
                message.payload,
                qos,
                false,
                null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Logger.d(MQTT_CLIENT_HELPER, "Message published to topic `$topic`: $msg")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Logger.d(
                            MQTT_CLIENT_HELPER,
                            "Message failed published to topic `$topic`: $msg"
                        )
                    }

                })
            Logger.d(MQTT_CLIENT_HELPER, "Message published to topic `$topic`: $msg")

        } catch (e: MqttException) {
            Logger.d(MQTT_CLIENT_HELPER, "Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttAndroidClient.isConnected
    }

    fun setCallback(callback: MqttCallbackExtended?) {
        if (callback != null) {
            mqttAndroidClient.setCallback(callback)
        }
    }

    fun destroy() {
        mqttAndroidClient.disconnect(null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Logger.d(MQTT_CLIENT_HELPER, "Disconnected")
                mqttAndroidClient.unregisterResources()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Logger.d(MQTT_CLIENT_HELPER, "Failed to disconnect")
            }
        })
    }

    fun subscribeToMultipleTopics(subscriptionTopics: List<String>) {
        for (topic in subscriptionTopics) {
            subscribe(topic) {}
        }
    }

}