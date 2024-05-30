package com.manoj.base.presentation.common

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.manoj.base.network.helper.ApiResponse
import com.manoj.base.network.helper.SimpleApiResponse
import com.manoj.base.presentation.common.helper.Resource
import com.manoj.base.presentation.common.helper.SingleRequestEvent
import com.manoj.base.presentation.common.helper.Status
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection


fun <M> Single<ApiResponse<M>>.apiSubscription(liveData: SingleRequestEvent<M>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<M>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(it.data, it.message.toString()))
        else liveData.postValue(Resource.warn(null, it.message.toString()))
    }, { it ->
        val error = parseException(it)
        error?.let {
            liveData.postValue(Resource.error(null, it))
        }
    })
}


fun Single<SimpleApiResponse>.simpleSubscription(liveData: SingleRequestEvent<Void>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<Void>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(null, it.message.toString()))
        else liveData.postValue(Resource.warn(null, it.message.toString()))
    }, { it ->
        val error = parseException(it);
        error?.let {
            liveData.postValue(Resource.error(null, it))
        }

    })
}

fun <B> Single<B>.customSubscription(liveData: SingleRequestEvent<B>): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<B>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        liveData.postValue(Resource.success(null, "Successful"))
    }, { it ->
        val error = parseException(it);
        error?.let {
            liveData.postValue(Resource.error(null, it))
        }
    })
}

fun <M> Single<SimpleApiResponse>.simpleSubscriptionWithTag(
    tag: M, liveData: SingleRequestEvent<M>
): Disposable {
    return this.doOnSubscribe {
        liveData.postValue(Resource.loading<M>())
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
        if (it.isStatusOK) liveData.postValue(Resource.success(tag, it.message.toString()))
        else liveData.postValue(Resource.warn(tag, it.message.toString()))
    }, { it ->
        val error = parseException(it);
        error?.let {
            liveData.postValue(Resource.error(tag, it))
        }

    })
}


fun parseException(it: Throwable?): String? {
    Log.e("http", "Error=>${it?.message}")
    when (it) {
        is HttpException -> {
            val exception: HttpException = it
            when (exception.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    val message = getErrorText(it)
                    if (message.contains("Unauthorised")) {
                        return "Unauthorised"
                    }
                    return message
                }

                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                    return exception.message()
                }

                else -> {
                    return getErrorText(it)
                }
            }
        }

        is IOException -> {
            return "Slow or No Internet Access"
        }

        else -> {
            return it?.message.toString()
        }
    }
}


fun <T> SingleRequestEvent<T>.singleObserver(
    owner: LifecycleOwner,
    onLoading: (Boolean) -> Unit,
    onSuccess: ((data: T) -> Unit),
    onError: ((throwable: Throwable, showError: Boolean) -> Unit),
) {
    this.observe(owner, SingleRequestEvent.RequestObserver { result ->

        when (result.status) {
            Status.LOADING -> onLoading(true)
            Status.SUCCESS -> {
                onLoading(false)
                result.data?.let { onSuccess(it) }
            }

            else -> {
                onLoading(false)
                onError(Throwable(result.message), true)
            }
        }
    })
}

fun getErrorText(it: HttpException): String {
    val errorBody: ResponseBody? = it.response()?.errorBody()
    val text: String? = errorBody?.string()
    if (!text.isNullOrEmpty()) {
        return try {
            val obj = JSONObject(text)
            obj.getString("message")
        } catch (e: Exception) {
            return text
        }
    }
    return it.message().toString()
}

class UnAuthUser
