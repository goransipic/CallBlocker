package com.goodapp.callblocker.ui.main

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.goodapp.callblocker.CallBlockerApp
import com.goodapp.callblocker.model.PhoneStatus
import com.goodapp.callblocker.model.ScamCall
import com.goodapp.callblocker.model.SuspiciousCall
import com.goodapp.callblocker.repository.PhoneRepository
import com.goodapp.callblocker.repository.db.CallBlockerDb
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.util.*


class MainViewModel : ViewModel() {

    private val callBlockerDb: CallBlockerDb = Room.databaseBuilder(CallBlockerApp.instance,
            CallBlockerDb::class.java, "database-name")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val contentValues = ContentValues()
                    contentValues.apply {
                        put("name", "Some Elvis")
                        put("phoneNumber", "425-950-1212")
                        put("date", Date().time)
                        db.insert(PhoneRepository.SUSPICIOUS_TABLE, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
                        clear()
                        put("name", "Some Elvis")
                        put("phoneNumber", "253-950-1212")
                        put("date", Date().time)
                        db.insert(PhoneRepository.SCAM_TABLE, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {

                }

            }).build()

    private val renderLiveData = MutableLiveData<List<PhoneStatus>>()

    fun getBlockedCalls(): LiveData<List<PhoneStatus>> {

        callBlockerDb.phoneCallsDao()
                .getAllScamCalls()
                .toObservable()
                .doOnNext({ it -> Log.d("MainViewModel", it.toString()) })
                .subscribeOn(Schedulers.computation())
                .flatMapIterable { it -> it }
                .doOnNext({ it -> Log.d("MainViewModel", it.phoneNumber) })
                .map { it -> ScamCall(it.phoneNumber, it.name , DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it.date))) }
                .defaultIfEmpty(ScamCall(emptyList = true))
                .toList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    renderLiveData.value = it
                }

        return renderLiveData
    }

    fun getSuspisiousCalls(): LiveData<List<PhoneStatus>> {

        callBlockerDb.phoneCallsDao()
                .getAllSuspiciousCalls()
                .toObservable()
                .doOnNext({ it -> Log.d("MainViewModel", it.toString()) })
                .subscribeOn(Schedulers.computation())
                .flatMapIterable { it -> it }
                .doOnNext({ it -> Log.d("MainViewModel", it.phoneNumber) })
                .map { it -> SuspiciousCall(phoneNumber =  it.phoneNumber, name =  it.name , date =  DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it.date))) }
                .defaultIfEmpty(SuspiciousCall(emptyList = true))
                .toList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    renderLiveData.value = it
                }

        return renderLiveData
    }
}
