package com.goodapp.callblocker.model

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.TelephonyManager
import android.content.Intent
import android.widget.Toast
import java.util.*
import java.lang.reflect.AccessibleObject.setAccessible
import androidx.core.content.ContextCompat.getSystemService
import com.android.internal.telephony.ITelephony


class PhoneCallReceiver : BroadcastReceiver() {
    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming: Boolean = false
    private lateinit var savedNumber: String   //because the passed incoming is only valid in ringing


    override fun onReceive(context: Context, intent: Intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
        val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
        var state = 0
        if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
            state = TelephonyManager.CALL_STATE_IDLE
        } else if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            state = TelephonyManager.CALL_STATE_OFFHOOK
        } else if (stateStr == TelephonyManager.EXTRA_STATE_RINGING) {
            state = TelephonyManager.CALL_STATE_RINGING
        }


        onCallStateChanged(context, state, number)

    }

    //Derived classes should override these to respond to specific events of interest
    private fun onIncomingCallStarted(ctx: Context, number: String, start: Date?) {

        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telephonyService: ITelephony
        try {
            val m = tm.javaClass.getDeclaredMethod("getITelephony")

            m.isAccessible = true
            telephonyService = m.invoke(tm) as ITelephony

            if (number != null) {
                telephonyService.endCall()
                Toast.makeText(ctx, "Ending the call from: $number", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun onOutgoingCallStarted(ctx: Context, number: String, start: Date?) {

    }
    private fun onIncomingCallEnded(ctx: Context, number: String, start: Date?, end: Date) {}
    private fun onOutgoingCallEnded(ctx: Context, number: String, start: Date?, end: Date) {}
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
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime)
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                }
        }
        lastState = state
    }

}