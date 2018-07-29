package com.goodapp.callblocker.repository

import android.content.Context
import android.provider.ContactsContract
import io.reactivex.Observable

class LocalPhoneContacts(private val context: Context) {

    fun isNormalCall(phoneNumber: String?): Observable<String> {

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
                    if (phoneNumberFormatted != null && phoneNumberFormatted == contactNumber) {
                        it.onNext(phoneNumber)
                        it.onComplete()
                        return@create
                    }
                }
            }
            it.onComplete()
        }

    }

}