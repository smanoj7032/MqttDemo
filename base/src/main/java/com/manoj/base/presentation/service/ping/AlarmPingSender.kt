package com.manoj.base.presentation.service.ping

import android.annotation.SuppressLint
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.manoj.base.presentation.common.Logger
import com.manoj.base.presentation.service.MqttService
import org.eclipse.paho.client.mqttv3.MqttPingSender
import org.eclipse.paho.client.mqttv3.internal.ClientComms
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


/**
 * Default ping sender implementation on Android. It is based on AlarmManager.
 *
 * This class implements the [MqttPingSender] ping interface
 * allowing applications to send ping packet to server every keep alive interval.
 *
 * @see MqttPingSender
 */
internal class AlarmPingSender(val service: MqttService) : MqttPingSender {

    private val workManager = WorkManager.getInstance(service)

    override fun init(comms: ClientComms) {
        clientComms = comms
    }

    override fun start() {
        // add clientState null check to avoid ClientState.getKeepAlive() NPE(#358,#433)
        clientComms?.clientState?.let {
            schedule(clientComms!!.keepAlive)
        } ?: Logger.e("FIXME: try to start ping schedule, but clientState null, not able to get keepAlive")
    }

    override fun stop() {
        workManager.cancelUniqueWork(PING_JOB)
    }

    override fun schedule(delayInMilliseconds: Long) {
        Logger.d("Schedule next alarm at ${sdf.format(Date(System.currentTimeMillis() + delayInMilliseconds))}")
        workManager.enqueueUniqueWork(
            PING_JOB,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest
                .Builder(PingWorker::class.java)
                .setInitialDelay(delayInMilliseconds, TimeUnit.MILLISECONDS)
                .build()
        )
    }

    companion object {
        private const val PING_JOB = "PING_JOB"
        internal var clientComms: ClientComms? = null

        @SuppressLint("ConstantLocale")
        internal val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault())
    }

}