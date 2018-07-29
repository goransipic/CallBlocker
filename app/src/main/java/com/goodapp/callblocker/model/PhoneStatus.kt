package com.goodapp.callblocker.model

import com.goodapp.callblocker.repository.api.ApiResponse

sealed class PhoneStatus
data class NormalCall(val phoneNumber : String) : PhoneStatus()
data class SuspiciousCall(val apiResponse : ApiResponse?, val phoneNumber : String) : PhoneStatus()
data class ScamCall(val phoneNumber : String) : PhoneStatus()