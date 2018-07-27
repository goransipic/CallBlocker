package com.goodapp.callblocker

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        Permissions.check(this, Manifest.permission.READ_PHONE_STATE, "Read Phone State permission is required.",
                object : PermissionHandler() {
                    override fun onGranted() {
                        //do your task.
                    }
                })

    }

}
