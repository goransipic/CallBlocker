package com.goodapp.callblocker.ui

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.goodapp.callblocker.R
import com.goodapp.callblocker.ui.main.MainFragment
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        Permissions.check(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS),
                "Read Phone State permission is required.",
                Permissions.Options(),
                object : PermissionHandler() {
                    override fun onGranted() {
                        //do your task.
                    }
                })

    }

}
