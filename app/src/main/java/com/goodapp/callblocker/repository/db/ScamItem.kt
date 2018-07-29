package com.goodapp.callblocker.repository.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.goodapp.callblocker.repository.PhoneRepository

@Entity(tableName = PhoneRepository.SCAM_TABLE)
data class ScamItem(
        val name: String,
        val phoneNumber: String,
        val date: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}