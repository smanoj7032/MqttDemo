package com.manoj.base.network.helper

interface Constants {

    /**To get credential to connect with MQTT server you have to make account on HIVEMq, AWS iOT core, EMOXXMqtt  or any other platform*/
    companion object {
        const val CLIENT_USER_NAME = "ENTER_YOUR_USERNAME"
        const val CLIENT_PASSWORD = "ENTER_YOUR_PASSWORD"
        const val MQTT_HOST = "ENTER_YOUR_HOST_WITH_PORT"
        const val CONNECTION_TIMEOUT = 30
        const val CONNECTION_KEEP_ALIVE_INTERVAL = 15
        const val CONNECTION_CLEAN_SESSION = true
        const val CONNECTION_RECONNECT = true
        const val NETWORK_STATE = "network_state"
    }
}