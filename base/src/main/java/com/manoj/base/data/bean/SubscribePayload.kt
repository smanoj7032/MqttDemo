package com.manoj.base.data.bean

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

data class SubscribePayload(
    val sensor_id: String,
    val flash_chip_id: String,
    val voltage: String,
    val current: String = ""
)

@Parcelize
data class Range(
    val minCurrent: Int,
    val maxCurrent: Int,
    val minVoltage: Int,
    val maxVoltage: Int,
) : Parcelable

@Parcelize
data class MQTTPayloadVoltage(
    val sensor_id: String,
    val flash_chip_id: String,
    val entry: String,
    val voltage: String, val timeStamp: String
):Parcelable

@Parcelize
data class MQTTPayloadCurrent(
    val sensor_id: String,
    val flash_chip_id: String,
    val entry: String,
    val current: String, val timeStamp: String
):Parcelable


