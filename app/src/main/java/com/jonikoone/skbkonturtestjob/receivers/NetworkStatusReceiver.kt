package com.jonikoone.skbkonturtestjob.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jonikoone.skbkonturtestjob.ui.activities.MainActivity

class NetworkStatusReceiver : BroadcastReceiver() {
    private val TAG = this::class.java.canonicalName

    lateinit var mainActivity: MainActivity

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG, "change network status")
        if (mainActivity.isOnline())
            mainActivity.showSnackbarPositive()
        else
            mainActivity.showSnackbarNegative()
    }
}