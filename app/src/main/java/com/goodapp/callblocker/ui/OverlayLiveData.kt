package com.goodapp.callblocker.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData


class OverlayLiveData : MutableLiveData<Boolean>() {

    companion object {
        var sInstance: MutableLiveData<Boolean> = OverlayLiveData()
    }
}