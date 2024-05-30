package com.manoj.mqttdemo.presentation.view.main

import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.manoj.base.data.bean.SubscribePayload
import com.manoj.base.network.helper.NetworkMonitor
import com.manoj.base.presentation.common.Logger
import com.manoj.base.presentation.common.base.BaseViewModel
import com.manoj.base.presentation.common.showSnackBar
import com.manoj.base.presentation.common.singleObserver
import com.manoj.base.presentation.common.toJson
import com.manoj.base.presentation.common.updateConnectionStatus
import com.manoj.mqttdemo.MyApplication.Companion.instance
import com.manoj.mqttdemo.R
import com.manoj.mqttdemo.presentation.base.BaseActivity
import com.manoj.mqttdemo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val TIME_INTERVAL = 2000
    private var doubleBackToExitPressedOnce: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (doubleBackToExitPressedOnce + TIME_INTERVAL > System.currentTimeMillis()) {
                finish()
            } else showToast("Please click BACK again to exit")
            doubleBackToExitPressedOnce = System.currentTimeMillis()
        }
    }
    private val viewModel: MainViewModel by viewModels()


    val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                binding.tvConnection.updateConnectionStatus(instance.mqttClientHelper.isConnected())
            }
            if (instance.mqttClientHelper.isConnected()) {
                val data = """{"sensor_id":"10064382","flash_chip_id":"1458376","voltage":"${
                    Random.nextInt(
                        0, 599
                    )
                }","current":"${Random.nextInt(10, 400)}"}"""
                binding.tvPublishData.text = "Published data : $data"
                instance.mqttClientHelper.publish("Manoj", data)

            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        initViews()
        instance.mqttClientHelper.connect {
            if (it) runOnUiThread {
                viewModel.subscribeToTopic()
                showToast("Connected to MQTT server")
            }
        }
        handler.post(runnable)
    }

    private fun initViews() {
        supportActionBar?.title = "Dashboard"
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }


    override fun setObserver() {
        lifecycleScope.launch {
            networkMonitor.networkState.collectLatest {
                handleNetworkState(it)
            }
        }
        instance.mqttClientHelper.mqttCallback.singleObserver(this,
            onLoading = { },
            onError = ::onError,
            onSuccess = { data ->
                val mqttPayload = Gson().fromJson(data.second, SubscribePayload::class.java)
                binding.tvSubscribeData.text = mqttPayload.toString()
            })
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection")
        if (state.isAvailable()) {
        }
        Logger.d("State---->>>", "$state")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        Logger.d("Test--->>", "onDestroy")
    }
}