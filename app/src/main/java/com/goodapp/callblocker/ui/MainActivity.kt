package com.goodapp.callblocker.ui

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.goodapp.callblocker.R
import com.goodapp.callblocker.ui.main.MainFragment
import com.goodapp.callblocker.ui.main.MainFragment.Companion.TYPE_BLOCKED_CALLS
import com.goodapp.callblocker.ui.main.MainFragment.Companion.TYPE_SUSPICIOUS_CALLS
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance(TYPE_BLOCKED_CALLS))
                    .commitNow()
        }

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_blocked -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, MainFragment.newInstance(TYPE_BLOCKED_CALLS))
                            .commitNow()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_suspicious -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, MainFragment.newInstance(TYPE_SUSPICIOUS_CALLS))
                            .commitNow()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
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
