package com.driver.reliantdispatch

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.boundaries.GPSServiceGateway
import com.driver.reliantdispatch.domain.boundaries.LocationGateway
import kotlinx.coroutines.*
import javax.inject.Inject


const val ACTION_START = "com.driver.reliantdispatch.action.START"

const val EXTRA_INTERVAL = "com.driver.reliantdispatch.action.EXTRA_INTERVAL"

const val NOTIFICATION_ID = 3000

const val CHANNEL_ID = "main"

//android:process=":reliantservice"


class MainService: Service() {
    @Inject
    lateinit var locationService: LocationGateway

    @Inject
    lateinit var api: ApiGateway

    @Inject
    lateinit var gpsService: GPSServiceGateway

    init {
        App.component.inject(this)
    }

    private val job = SupervisorJob()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)


    override fun onCreate() {
        Log.d(LOG_TAG, "service create $this")

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val chan = NotificationChannel(CHANNEL_ID,
                    resources.getString(R.string.channel_name), NotificationManager.IMPORTANCE_NONE)
                chan.lightColor = Color.BLUE
                chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                service.createNotificationChannel(chan)
                CHANNEL_ID
            } else ""

        val intent = Intent(this@MainService, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this@MainService, 0, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(this@MainService, channelId)
            .setSmallIcon(R.drawable.ic_terminal_gray)
            .setContentTitle(resources.getString(R.string.notify_title))
            .setPriority(Notification.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (startId > 1){
            //Log.d(LOG_TAG,"stopSelf ${startId-1} $this")
            ioScope.coroutineContext.cancelChildren()
        }

        (intent?.getLongExtra(EXTRA_INTERVAL, 0) ?: 0).let{
            Log.d(LOG_TAG,"onStartCommand $startId interval = ${it/1000/3600}")

            if (it > 0L) gpsService.setNextStart(this@MainService, it)
        }

        ioScope.launch {
            val loc = locationService.getCurrentLocation()
            Log.d(LOG_TAG,"SERVICE LOCATION ${loc?.longitude} - ${loc?.latitude}")

            loc?.let {
                val response = api.putGPS(it)
                when {
                    response.success -> Log.d(LOG_TAG,"put gps - success $startId")
                    response.noInternet -> Log.d(LOG_TAG,"put gps - no internet")
                    response.notAuthorized -> Log.d(LOG_TAG,"put gps - not auth")
                    else -> Log.d(LOG_TAG,"put gps - error: ${response.errorMsg}")
                }
            }

            //delay(2000)
            stopSelf(startId)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.coroutineContext.cancelChildren()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}