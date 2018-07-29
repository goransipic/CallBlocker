package com.goodapp.callblocker.ui

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.Window
import com.goodapp.callblocker.R
import com.goodapp.callblocker.repository.PhoneRepository.Companion.PHONE_NUMBER
import kotlinx.android.synthetic.main.activity_overlay_phone.*
import java.util.*
import com.goodapp.callblocker.R.id.view
import android.view.animation.Animation
import android.view.animation.AlphaAnimation



class OverlayPhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay_phone)

        phoneNumber.text = PhoneNumberUtils.formatNumber(intent.getStringExtra(PHONE_NUMBER), Locale.getDefault().country)

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 500
        alphaAnimation.repeatCount = 200
        alphaAnimation.repeatMode = Animation.REVERSE
        textView.startAnimation(alphaAnimation)

        button.setOnClickListener {
            finish()
        }
    }
}
