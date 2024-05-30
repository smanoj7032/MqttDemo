package com.manoj.mqttdemo.presentation.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.manoj.base.data.local.SharedPrefManager
import com.manoj.base.network.helper.NetworkMonitor
import com.manoj.base.presentation.common.Logger
import com.manoj.base.presentation.common.base.BaseViewModel
import com.manoj.mqttdemo.BR
import com.manoj.mqttdemo.MyApplication
import com.manoj.mqttdemo.R
import com.manoj.mqttdemo.databinding.ViewProgressSheetBinding
import javax.inject.Inject


abstract class
BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {

    @Inject
    lateinit var sharepref: SharedPrefManager
    val TAG: String = this.javaClass.simpleName
    private var progressSheet: ProgressSheet? = null
    open val onRetry: (() -> Unit)? = null
    lateinit var binding: Binding
    val app: MyApplication
        get() = application as MyApplication
    private val TIMER_ANIMATION: Long = 1200

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        onCreateView()
        setObserver()
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()
    protected abstract fun setObserver()

    fun showToast(msg: String? = "Something went wrong !!") {
        Toast.makeText(this, msg ?: "Showed null value !!", Toast.LENGTH_SHORT).show()
    }


    fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    fun showLoading(s: String?) {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = ProgressSheet(object : ProgressSheet.BaseCallback {
            override fun onClick(view: View?) {}
            override fun onBind(bind: ViewProgressSheetBinding) {
                progressSheet?.showMessage(s)
            }
        })
        progressSheet?.show(supportFragmentManager, progressSheet?.tag)
    }

    fun onLoading(show: Boolean) {
        val progressBar: View = findViewById(R.id.progress_bar)
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }


    fun hideLoading() {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = null
        getLoaderView()?.setVariable(BR.show, false)
    }

    /* fun onLoading(show: Boolean) {
         if (show) showLoading("Loading")
         else hideLoading()
     }*/

    protected open fun getLoaderView(): ViewDataBinding? {
        return null
    }

    fun onError(error: Throwable, showErrorView: Boolean) {
        if (showErrorView) {
            showToast(error.message)
            Logger.e("Error-->>", "${error.message}")
        }
    }

    override fun onDestroy() {
        progressSheet?.dismissAllowingStateLoss()
        hideLoading()
        super.onDestroy()
    }
}