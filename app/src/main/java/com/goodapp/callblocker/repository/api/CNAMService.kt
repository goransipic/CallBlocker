package com.goodapp.callblocker.repository.api

import com.goodapp.callblocker.BuildConfig
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CNAMService {

    @GET("api/v1/")
    fun getCnamInfo(@Query("calling_number") phoneNumber: String,
                    @Query("username") username: String = BuildConfig.CNAM_API_KEY,
                    @Query("password") password: String = BuildConfig.CNAM_PASSWORD,
                    @Query("resp_type") respType: String = "basic",
                    @Query("resp_format") respFormat: String = "json",
                    @Query("call_party") callParty: String = "terminating"): Observable<ApiResponse>
}