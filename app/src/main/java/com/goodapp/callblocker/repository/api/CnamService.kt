package com.goodapp.callblocker.repository.api

import com.goodapp.callblocker.BuildConfig
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CnamService {

    @GET("username=" + BuildConfig.CNAM_API_KEY + "&password=" + BuildConfig.CNAM_PASSWORD + "&resp_type=basic&resp_format=json&call_party=terminating")
    fun getCnamInfo(@Query("calling_number") phoneNumber: String): Observable<ApiResponse>
}