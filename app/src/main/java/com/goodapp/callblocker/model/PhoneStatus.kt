package com.goodapp.callblocker.model

import android.text.BoringLayout
import com.goodapp.callblocker.repository.api.ApiResponse

sealed class PhoneStatus
data class NormalCall(val phoneNumber: String, val name: String? = null, val date: String? = null) : PhoneStatus()
data class SuspiciousCall(val apiResponse: ApiResponse? = null, val phoneNumber: String? = null, val name: String? = null, val date: String? = null,val emptyList : Boolean = true) : PhoneStatus()
data class ScamCall(val phoneNumber: String? = null, val name: String? = null, val date: String? = null, val emptyList : Boolean = true) : PhoneStatus()