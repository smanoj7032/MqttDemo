package com.manoj.base.presentation.common

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manoj.base.R
import com.manoj.base.presentation.common.helper.Resource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun SharedPreferences.saveValue(key: String, value: Any?) {
    when (value) {
        is String? -> editNdCommit { it.putString(key, value) }
        is Int -> editNdCommit { it.putInt(key, value) }
        is Boolean -> editNdCommit { it.putBoolean(key, value) }
        is Float -> editNdCommit { it.putFloat(key, value) }
        is Long -> editNdCommit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

fun <T> SharedPreferences.getValue(key: String, defaultValue: Any? = null): T? {
    return when (defaultValue) {
        is String? -> {
            getString(key, defaultValue as? String) as? T
        }

        is Int -> {
            getInt(key, defaultValue as? Int ?: -1) as? T
        }

        is Boolean -> getBoolean(key, defaultValue as? Boolean ?: false) as? T
        is Float -> getFloat(key, defaultValue as? Float ?: -1f) as? T
        is Long -> getLong(key, defaultValue as? Long ?: -1) as? T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun SharedPreferences.editNdCommit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}


fun Activity.hideKeyboard() {
    val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard() {
    val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.toggleSoftInputFromWindow(
        this.currentFocus?.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0
    );
}


fun Fragment.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}


fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also {
        it.view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.black))
        it.show()
    }
}

fun <T> Resource<T>.log() {
    Log.i("Resource", this.toString())
}


fun RecyclerView.setLinearLayoutManger() {
    this.layoutManager = LinearLayoutManager(this.context)
}


fun Fragment.showSheet(sheet: BottomSheetDialogFragment) {
    sheet.show(this.childFragmentManager, sheet.tag)
}

fun FragmentActivity.showSheet(sheet: BottomSheetDialogFragment) {
    sheet.show(this.supportFragmentManager, sheet.tag)
}

fun ContentResolver.getFileName(fileUri: Uri): String {

    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}

fun Resources.dptoPx(dp: Int): Float {
    return dp * this.displayMetrics.density
}

fun <T> Activity.startNewActivity(s: Class<T>, killCurrent: Boolean = false) {
    val intent = Intent(this, s)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
    if (killCurrent) finish()
}

fun <T> Activity.startNewActivityFromDrawer(s: Class<T>) {
    val intent = Intent(this, s)
    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    finish()
    startActivity(intent)

}

fun <T> Activity.getNewIntent(s: Class<T>): Intent {
    val intent = Intent(this, s)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    return intent
}


fun String.toGenderId(): String {
    return when (this) {
        "Male" -> "1"
        "Female" -> "2"
        "Unspecified" -> "3"
        "Undisclosed" -> "4"
        else -> "0"
    }
}


fun String.toGenderName(): String {
    return when (this) {
        "1" -> "Male"
        "2" -> "Female"
        "3" -> "Unspecified"
        "4" -> "Undisclosed"
        else -> ""
    }
}

fun Activity.setTwoColorInText(text: String): String {
    val lastChar = text.last().toString()

    val whiteText = "<font color='" + ContextCompat.getColor(
        this, R.color.white
    ) + "'>" + text.substring(0, text.length - 1) + "</font>"

    val greenText = "<font color='" + ContextCompat.getColor(
        this, R.color.white
    ) + "'>$lastChar</font>"

    return "$whiteText$greenText"
}


fun View.rotateInfinite() {
    val rotate = RotateAnimation(
        0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    )
    rotate.duration = 5500
    rotate.interpolator = LinearInterpolator()
    rotate.repeatCount = Animation.INFINITE
    this.startAnimation(rotate)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Any.toJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}


fun Long.convertLongToTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return format.format(date)
}

fun <M, T> Map<M, T>.toRequestBody(): RequestBody {
    val jsonString = Gson().toJson(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}


fun Long.convertLongToDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val remainingSeconds = this % 3600
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    return when {
        hours > 0 -> "${String.format("%02d:%02d:%02d", hours, minutes, seconds)} h"
        minutes > 0 -> "${String.format("%02d:%02d", minutes, seconds)} min"
        else -> "${seconds}s"
    }
}

fun Context.getDrawables(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}

fun JSONObject.getLastKey(): String? {
    val keys = this.keys()
    var lastKey: String? = null
    while (keys.hasNext()) {
        lastKey = keys.next()
    }
    return lastKey
}


fun <T> updateEntries(
    dataList: List<T>,
    existingList: MutableList<T>,
    addEntry: (T) -> Unit
) {
    dataList.forEach { data ->
        if (data !in existingList) {
            addEntry(data)
            existingList.add(data)
        }
    }
}


fun View.setSingleClickListener(listener: (v: View) -> Unit) {
    this.setOnClickListener {
        if (ClickController.isClickAllowed()) {
            listener(this)
        }
    }
}
object ClickController {

    private const val MIN_CLICK_INTERVAL_MS = 600L

    private var lastClickTime: Long = 0L

    fun isClickAllowed(): Boolean {
        val currentTime = getCurrentTimeMillis()
        val isClickAllowed = isClickAllowed(currentTime)

        if (isClickAllowed) {
            lastClickTime = currentTime
        }

        return isClickAllowed
    }

    private fun isClickAllowed(currentTime: Long): Boolean = (currentTime - lastClickTime) > MIN_CLICK_INTERVAL_MS

    private fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

}

/*
fun List<MQTTPayloadCurrent>.combineWith(voltageList: List<MQTTPayloadVoltage>): List<TableData> {
    val payloadList = (this.map { it.toTableData() } + voltageList.map { it.toTableData() }).groupBy { it.timeStamp }
    return payloadList.values.map { payloads ->
        payloads.reduce { acc, payload ->
            acc.copy(
                voltage = payload.voltage.ifBlank { acc.voltage },
                current = payload.current.ifBlank { acc.current }
            )
        }
    }.sortedBy { it.timeStamp }
}

private fun MQTTPayloadCurrent.toTableData() = TableData(
    voltage = "",
    current = current,
    timeStamp = timeStamp.toLong().convertLongToTime(),
    date = timeStamp.toLong().convertLongToDate()
)

private fun MQTTPayloadVoltage.toTableData() = TableData(
    voltage = voltage,
    current = "",
    timeStamp = timeStamp.toLong().convertLongToTime(),
    date = timeStamp.toLong().convertLongToDate()
)
*/
