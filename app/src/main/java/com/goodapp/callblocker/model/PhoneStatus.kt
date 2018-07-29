package com.goodapp.callblocker.model

sealed class PhoneStatus
data class NormalCall(val phoneNumber : String) : PhoneStatus()
object SuspiciousCall : PhoneStatus()
data class ScamCall(val phoneNumber : String) : PhoneStatus()