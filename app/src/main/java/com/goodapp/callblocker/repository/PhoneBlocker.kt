package com.goodapp.callblocker.repository

import android.content.Context
import androidx.work.Worker
import com.goodapp.callblocker.CallBlockerApp
import android.telephony.TelephonyManager
import java.util.*


class PhoneBlocker : Worker() {

    private val phoneRepository = PhoneRepository(CallBlockerApp.instance, CheckPhoneState(CallBlockerApp.instance))

    companion object {
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var callStartTime: Date? = null
        private var isIncoming: Boolean = false
        private var savedNumber: String? = null   //because the passed incoming is only valid in ringing
        const val CHANNEL_ID: String = "phone_blocker"
    }

    override fun doWork(): Result {
        var state = 0
        val stateStr = inputData.getString(TelephonyManager.EXTRA_STATE)
        val number = inputData.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

        number ?: return Result.SUCCESS

        when (stateStr) {
            TelephonyManager.EXTRA_STATE_IDLE -> state = TelephonyManager.CALL_STATE_IDLE
            TelephonyManager.EXTRA_STATE_OFFHOOK -> state = TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_RINGING -> state = TelephonyManager.CALL_STATE_RINGING
        }

        onCallStateChanged(CallBlockerApp.instance, state, number)

        return Result.SUCCESS
    }

    //Derived classes should override these to respond to specific events of interest
    private fun onIncomingCallStarted(ctx: Context, number: String, start: Date?) {
        phoneRepository.isOnBlackList(number)
    }

    private fun onOutgoingCallStarted(ctx: Context, number: String?, start: Date?) {

    }

    private fun onIncomingCallEnded(ctx: Context, number: String?, start: Date?, end: Date) {}
    private fun onOutgoingCallEnded(ctx: Context, number: String?, start: Date?, end: Date) {}
    private fun onMissedCall(ctx: Context, number: String?, start: Date?) {}

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    fun onCallStateChanged(context: Context, state: Int, number: String) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                onIncomingCallStarted(context, number, callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    onOutgoingCallStarted(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                when {
                    lastState == TelephonyManager.CALL_STATE_RINGING -> //Ring but no pickup-  a miss
                        onMissedCall(context, savedNumber, callStartTime)
                    isIncoming -> onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                    else -> onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                }
        }
        lastState = state
    }


}
