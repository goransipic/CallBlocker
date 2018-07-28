package com.goodapp.callblocker.repository

import android.content.Context
import android.provider.ContactsContract
import io.reactivex.Observable

class LocalPhoneContacts(private val context: Context) {

    fun isOnBlockList(phoneNumber: String?): Observable<String> {

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
                    var contactNumber: String = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("\\D+".toRegex(),"")
                    if (phoneNumberFormatted != null && phoneNumberFormatted == contactNumber) {
                        it.onComplete()
                        return@create
                    }
                }
                if (phoneNumber != null) {
                    it.onNext(phoneNumber)
                    it.onComplete()
                } else {
                    it.onComplete()
                }

            }
            it.onComplete()
        }

    }

}