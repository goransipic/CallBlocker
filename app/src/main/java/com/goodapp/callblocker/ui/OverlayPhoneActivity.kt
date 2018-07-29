package com.goodapp.callblocker.ui

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.goodapp.callblocker.R

class OverlayPhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay_phone)

        OverlayLiveData.sInstance.observe(this, Observer<Boolean> {
            finish()
        })
    }
}
