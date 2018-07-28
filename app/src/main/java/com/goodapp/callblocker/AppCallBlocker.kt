package com.goodapp.callblocker

import android.app.Application
import com.extensions.DelegatesExt

class AppCallBlocker : Application(){

    companion object {
        var instance: AppCallBlocker by DelegatesExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}