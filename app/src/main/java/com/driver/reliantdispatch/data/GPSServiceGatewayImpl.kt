package com.driver.reliantdispatch.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.driver.reliantdispatch.ACTION_START
import com.driver.reliantdispatch.EXTRA_INTERVAL
import com.driver.reliantdispatch.MainService
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.GPSServiceGateway

const val REQ_CODE = 1

class GPSServiceGatewayImpl: GPSServiceGateway {

    override fun start(context: Context, interval: Long){
        Log.d(LOG_TAG,"GPSServiceGatewayImpl start interval = ${interval/1000/3600}")
        context.startService(
            Intent(context, MainService::class.java).apply{
                action = ACTION_START
                putExtra(EXTRA_INTERVAL, interval)
            }
        )
    }

    override fun setNextStart(context: Context, interval: Long){
        val appContext = context.applicationContext

        Log.d(LOG_TAG,"GPSServiceGatewayImpl setNextStart interval = ${interval/1000/3600}")

        val intent = Intent(appContext, MainService::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_INTERVAL, interval)
        }
        val pendingIntent =
            PendingIntent.getService(appContext, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        (appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager).run{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Log.d(LOG_TAG, "alarmService.setExact")
                setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingIntent
                )
            } else {
                Log.d(LOG_TAG, "alarmService.set")
                set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingIntent
                )
            }
        }
    }

    override fun cancelNextStart(context: Context, interval: Long){
        val appContext = context.applicationContext

        val intent = Intent(appContext, MainService::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_INTERVAL, interval)
        }
        val pendingIntent =
            PendingIntent.getService(appContext, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        (appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager).run{
            Log.d(LOG_TAG, "alarmService.cancel")
            cancel(pendingIntent)
        }
    }
}