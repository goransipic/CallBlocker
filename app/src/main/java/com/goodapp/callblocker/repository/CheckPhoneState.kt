package com.goodapp.callblocker.repository

import android.content.Context
import android.provider.ContactsContract
import com.goodapp.callblocker.BuildConfig
import com.goodapp.callblocker.model.NormalCall
import com.goodapp.callblocker.model.PhoneStatus
import com.goodapp.callblocker.model.ScamCall
import com.goodapp.callblocker.model.SuspiciousCall
import com.goodapp.callblocker.repository.api.ApiResponse
import com.goodapp.callblocker.repository.api.CnamService
import com.goodapp.callblocker.repository.db.CallBlockerDb
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


class CheckPhoneState(private val context: Context) {

    private var retrofit: Retrofit
    private val BASE_URL = "https://api.truecnam.net/api/v1/"

    init {
        retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun isNormalCall(phoneNumber: String): Observable<PhoneStatus> {

        return Observable.create<String> {
            val phoneNumberFormatted = phoneNumber?.replace("\\D+".toRegex(), "")

            val cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null)

            it.setCancellable { cursor.close() }

            if (cursor == null) {
                it.onComplete()
                return@create
            }

            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    var contactNumber: String = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("\\D+".toRegex(), "")
                    if (phoneNumberFormatted == contactNumber) {
                        it.onNext(phoneNumber)
                        it.onComplete()
                        return@create
                    }
                }
            }
            it.onComplete()
        }.map { NormalCall(phoneNumber) }

    }

    fun isSuspiciousCall(db: CallBlockerDb, phoneNumber: String): Observable<PhoneStatus> {
        return db.phoneCallsDao().getAllSuspiciousCalls()
                .toObservable()
                .flatMapIterable { it -> it }
                .map { it -> it.phoneNumber.replace("\\D+".toRegex(), "") }
                .filter { it == phoneNumber.replace("\\D+".toRegex(), "") }
                .flatMap { it ->
                    retrofit.create(CnamService::class.java)
                            .getCnamInfo(phoneNumber)
                            .map { response -> SuspiciousCall(response, it) }
                            .onErrorReturn { SuspiciousCall(null, phoneNumber = phoneNumber) }
                }

    }

    fun isScamCall(db: CallBlockerDb, phoneNumber: String): Observable<PhoneStatus> {
        return db.phoneCallsDao().getAllScamCalls().toObservable()
                .flatMapIterable { it -> it }
                .map { it -> it.phoneNumber.replace("\\D+".toRegex(), "") }
                .filter { it == phoneNumber.replace("\\D+".toRegex(), "") }
                .map { ScamCall(phoneNumber) }
    }

}