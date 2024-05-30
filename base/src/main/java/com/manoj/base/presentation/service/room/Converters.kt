package com.manoj.base.presentation.service.room

import androidx.room.TypeConverter
import com.manoj.base.presentation.service.QoS
import org.eclipse.paho.client.mqttv3.MqttMessage

class Converters {

    @TypeConverter
    fun toQoS(value: Int) = enumValues<QoS>()[value]

    @TypeConverter
    fun fromQoS(value: QoS) = value.value

    @TypeConverter
    fun toMqttMessage(value: String): MqttMessage = MqttMessage(value.toByteArray(Charsets.UTF_8))

    @TypeConverter
    fun fromMqttMessage(value: MqttMessage): String = value.payload.toString(Charsets.UTF_8) //.decodeToString()
}