package com.goodapp.callblocker.repository.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface PhoneCallsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuspiciousItem(callItem: SuspiciousItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScamItem(callItem: ScamItem)

    @Query("SELECT * FROM suspicious_numbers")
    fun getAllSuspiciousCalls(): Flowable<SuspiciousItem>

    @Query("SELECT * FROM scam")
    fun getAllScamCalls(): Flowable<ScamItem>

}