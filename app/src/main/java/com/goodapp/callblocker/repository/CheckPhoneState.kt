package com.goodapp.callblocker.repository

import android.content.Context
import android.provider.ContactsContract
import com.goodapp.callblocker.model.NormalCall
import com.goodapp.callblocker.model.PhoneStatus
import com.goodapp.callblocker.model.ScamCall
import com.goodapp.callblocker.model.SuspiciousCall
import com.goodapp.callblocker.repository.db.CallBlockerDb
import io.reactivex.Observable

class CheckPhoneState(private val context: Context) {

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
                .map { it -> it.phoneNumber.replace("\\D+".toRegex(), "")}
                .filter { it == phoneNumber.replace("\\D+".toRegex(), "") }
                .map { SuspiciousCall }
    }

    fun isScamCall(db: CallBlockerDb, phoneNumber: String): Observable<PhoneStatus> {
        return db.phoneCallsDao().getAllScamCalls().toObservable()
                .flatMapIterable { it -> it }
                .map { it -> it.phoneNumber.replace("\\D+".toRegex(), "") }
                .filter { it == phoneNumber.replace("\\D+".toRegex(), "") }
                .map { ScamCall(phoneNumber) }
    }

}