package com.manoj.base.presentation.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.manoj.base.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


/**
 * Use this extension to show the view.
 * The view visibility will be changed to [View.VISIBLE]
 * @see [View.setVisibility]
 * **/
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Use this extension to hide the view.
 * The view visibility will be changed to [View.GONE]
 * @see [View.setVisibility]
 * **/
fun View.hide() {
    visibility = View.GONE
}

fun TextView.updateConnectionStatus(isConnected: Boolean) {
    isSelected = isConnected
    text = context.getString(if (isConnected) R.string.mqtt_server_connected else R.string.mqtt_server_disconnected)
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun AppCompatActivity.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}



fun covertTimeAgoToText(dataString: String?): String {
    if (dataString == null) {
        return ""
    }

    val suffix = "ago"

    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val pasTime = dateFormat.parse(dataString)

        val nowTime = Date()
        val dateDiff = nowTime.time - pasTime.time
        if (dateDiff < 0) {
            return ""  // Handle the case where the parsed time is in the future
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hours = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val days = TimeUnit.MILLISECONDS.toDays(dateDiff).toDouble()

        return when {
            seconds < 60 -> "$seconds seconds $suffix"
            minutes < 60 -> "$minutes minutes $suffix"
            hours < 24 -> "$hours hours $suffix"
            days < 7 -> "$days days $suffix"
            else -> {
                val weeks = days / 7
                if (weeks < 4) {
                    "$weeks weeks $suffix"
                } else {
                    val months = weeks / 4
                    if (months < 12) {
                        "$months months $suffix"
                    } else {
                        val years = months / 12
                        "$years years $suffix"
                    }
                }
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        "((day / 360) * 10.0).roundToInt() / 10.0"
        return ""
    }
}

/**
 *
 * Returns FirstVisibleItemPosition
 *
 */
fun RecyclerView.findFirstVisibleItemPosition(): Int {
    if (layoutManager is LinearLayoutManager) {
        return (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
    }
    return if (layoutManager is StaggeredGridLayoutManager) {
        val mItemPositionsHolder =
            IntArray((layoutManager as StaggeredGridLayoutManager?)!!.spanCount)
        return min(
            (layoutManager as StaggeredGridLayoutManager?)!!.findFirstVisibleItemPositions(
                mItemPositionsHolder
            )
        )
    } else -1
}


/**
 *
 * Returns the min value in an array.
 *
 * @param array  an array, must not be null or empty
 * @return the min value in the array
 */
private fun min(array: IntArray): Int {

    // Finds and returns max
    var min: Int = array[0]
    for (j in 1 until array.size) {
        if (array[j] < min) {
            min = array[j]
        }
    }
    return min
}


/**
 *
 * Returns true if recyclerView isAtTop
 *
 */
fun RecyclerView.isAtTop(): Boolean {
    val pos: Int =
        (layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition()!!
    return pos == 0
}


@SuppressLint("DiscouragedApi")
fun Context.getResource(name: String): Drawable? {
    val resID = this.resources.getIdentifier(name, "drawable", this.packageName)
    return ActivityCompat.getDrawable(this, resID)
}

private fun getStoragePermission(): Array<String> {
    return if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

fun getLocationPermissions(): Array<String> {
    return arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

}
@RequiresApi(Build.VERSION_CODES.Q)
fun getBackgroundLocationPermission(): Array<String>{
    return  arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
}

val PERMISSION_READ_STORAGE = getStoragePermission()


inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

object Logger {
    private var TAG = "CORTEX"
    var isDebug = true
    fun setTAG(tag: String) {
        TAG = tag
    }

    fun d(msg: String?) {
        if (isDebug) Log.d(TAG, msg!!)
    }

    fun d(tag: String?, msg: String?) {
        if (isDebug) Log.d(tag, msg!!)
    }

    fun d(msg: Int) {
        if (isDebug) Log.d(TAG, msg.toString() + "")
    }

    fun d(tag: String?, msg: Int) {
        if (isDebug) Log.d(tag, msg.toString() + "")
    }

    fun e(msg: String?) {
        if (isDebug) Log.e(TAG, msg!!)
    }

    fun e(tag: String?, msg: String?) {
        if (isDebug) Log.e(tag, msg!!)
    }

    fun e(msg: Int) {
        if (isDebug) Log.e(TAG, msg.toString() + "")
    }

    fun e(tag: String?, msg: Int) {
        if (isDebug) Log.e(tag, msg.toString() + "")
    }

    fun i(msg: String?) {
        if (isDebug) Log.i(TAG, msg!!)
    }

    fun i(tag: String?, msg: String?) {
        if (isDebug) Log.i(tag, msg!!)
    }

    fun i(msg: Int) {
        if (isDebug) Log.i(TAG, msg.toString() + "")
    }

    fun i(tag: String?, msg: Int) {
        if (isDebug) Log.i(tag, msg.toString() + "")
    }

    fun v(tag: String?, message: String?) {
        if (isDebug) {
            Log.v(tag, message!!)
        }
    }
}


fun isSdkVersionGreaterThan(version: Int): Boolean {
    return Build.VERSION.SDK_INT > version
}

fun isSdkVersionGreaterThanOrEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT >= version
}

fun isSdkVersionLessThan(version: Int): Boolean {
    return Build.VERSION.SDK_INT < version
}

fun isSdkVersionLessThanOrEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT <= version
}

fun isSdkVersionEqualTo(version: Int): Boolean {
    return Build.VERSION.SDK_INT == version
}
