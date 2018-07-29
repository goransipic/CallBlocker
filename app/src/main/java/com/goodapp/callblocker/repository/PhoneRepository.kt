package com.goodapp.callblocker.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import com.goodapp.callblocker.R
import com.goodapp.callblocker.model.NormalCall
import com.goodapp.callblocker.model.PhoneStatus
import com.goodapp.callblocker.model.ScamCall
import com.goodapp.callblocker.model.SuspiciousCall
import com.goodapp.callblocker.repository.db.CallBlockerDb
import io.reactivex.Observable


class PhoneRepository(private val context: Context, private val checkPhoneState: CheckPhoneState) {

    private var callBlockerDb: CallBlockerDb

    companion object {
        const val SUSPICIOUS_TABLE = "suspicious_numbers"
        const val SCAM_TABLE = "scam_numbers"
    }

    init {
        callBlockerDb = Room.databaseBuilder(context,
                CallBlockerDb::class.java, "database-name")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        val contentValues = ContentValues()
                        contentValues.apply {
                            put("name", "Some Elvis")
                            put("phoneNumber", "425-950-1212")
                            db.insert(SUSPICIOUS_TABLE, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
                            clear()
                            contentValues.put("name", "Some Elvis")
                            contentValues.put("phoneNumber", "253-950-1212")
                            db.insert(SCAM_TABLE, SQLiteDatabase.CONFLICT_REPLACE, contentValues)
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {

                    }

                }).build()
    }

    fun isOnBlackList(phoneNumber: String) {
        Observable.concat(
                checkPhoneState.isNormalCall(phoneNumber),
                checkPhoneState.isSuspiciousCall(callBlockerDb, phoneNumber),
                checkPhoneState.isScamCall(callBlockerDb, phoneNumber)).take(1).subscribe(this::process)
    }

    private fun process(phoneStatus: PhoneStatus) {
        when (phoneStatus) {
            is NormalCall -> processNormalCall(context, phoneStatus.phoneNumber)
            is SuspiciousCall -> processSuspiciousCall()
            is ScamCall -> processScamCall(context, phoneStatus.phoneNumber)
        }
    }

    private fun processScamCall(ctx: Context, number: String?) {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telephonyService: ITelephony
        try {
            val m = tm.javaClass.getDeclaredMethod("getITelephony")

            m.isAccessible = true
            telephonyService = m.invoke(tm) as ITelephony

            if (number != null) {
                telephonyService.endCall()
                createNotificationChannel(ctx)
                val mBuilder = NotificationCompat.Builder(ctx, PhoneBlocker.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.title_message))
                        .setContentText(context.getString(R.string.content_message_part_1) +" "+ PhoneNumberUtils.formatNumber(number) + context.getString(R.string.content_message_part_2))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.content_message_part_1) +" "+ PhoneNumberUtils.formatNumber(number) + context.getString(R.string.content_message_part_2)))

                val notificationManager = NotificationManagerCompat.from(ctx)

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, mBuilder.build())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSuspiciousCall() {
        Log.d(PhoneRepository::class.java.simpleName,"processSuspiciousCall")
    }

    private fun processNormalCall(ctx: Context, number: String?) {
        Log.d(PhoneRepository::class.java.simpleName,"processNormalCall")
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val description = "channel_description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(PhoneBlocker.CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

}