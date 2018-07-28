package com.goodapp.callblocker.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(
        entities = [
            SuspiciousItem::class, ScamItem::class],
        version = 1,
        exportSchema = false
)
abstract class CallBlockerDb : RoomDatabase() {

    abstract fun phoneCallsDao(): PhoneCallsDao
}