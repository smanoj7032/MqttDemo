package com.manoj.base.presentation.common.helper

import androidx.annotation.MainThread

class SingleActionEvent<T> : SingleLiveEvent<T>() {

    @MainThread
    fun call(v: T) {
        value = v
    }
}