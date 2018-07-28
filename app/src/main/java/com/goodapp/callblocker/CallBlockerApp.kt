package com.goodapp.callblocker

import android.app.Application
import com.extensions.DelegatesExt

class CallBlockerApp : Application(){

    companion object {
        var instance: CallBlockerApp by DelegatesExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}