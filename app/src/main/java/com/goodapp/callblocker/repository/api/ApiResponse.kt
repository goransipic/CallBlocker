package com.goodapp.callblocker.repository.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ApiResponse {

    @SerializedName("calling_number")
    @Expose
    var callingNumber: String? = null
    @SerializedName("error_msg")
    @Expose
    var errorMsg: String? = null
    @SerializedName("err")
    @Expose
    var err: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}