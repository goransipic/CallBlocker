package com.goodapp.callblocker.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "scam")
data class ScamItem(
        val name: String,
        val phoneNumber: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}