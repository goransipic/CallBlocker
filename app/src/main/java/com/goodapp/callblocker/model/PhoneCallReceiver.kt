package com.goodapp.callblocker.model

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.TelephonyManager
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class PhoneCallReceiver : BroadcastReceiver() {
    //The receiver will be recreated whenever android feels like it.
    override fun onReceive(context: Context, intent: Intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
        val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

        val myData: Data = Data.Builder()
                .putString(TelephonyManager.EXTRA_STATE,stateStr)
                .putString(TelephonyManager.EXTRA_INCOMING_NUMBER,number)
                .build()

        val phoneBlocker = OneTimeWorkRequest.Builder(PhoneBlocker::class.java)
                .setInputData(myData)
                .build()

        WorkManager.getInstance().enqueue(phoneBlocker)

    }
}