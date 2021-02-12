package com.intelliavant.mytimetracker.utils

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.annotation.ColorInt
import com.intelliavant.mytimetracker.R
import com.intelliavant.mytimetracker.StopwatchService

typealias  OnUpdateListener = (elapsedMilliseconds: Long, isRunning: Boolean) -> Unit


class StopwatchManager(private val context: Context) {

    private var mBound = false
    private lateinit var mService: StopwatchService
    private lateinit var broadcastReceiver: BroadcastReceiver

    var isRunning = false
        private set

    var elapsedMilliseconds = 0L
        private set

    var workName = ""
        private set

    var color = 0
        private set

    var onUpdate: OnUpdateListener? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StopwatchService.StopwatchServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    fun isStopwatchServiceRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningServices(Int.MAX_VALUE).forEach {
            if (StopwatchService::class.java.name == it.service.className) {
                return true;
            }
        }

        return false;
    }

    private fun createNotificationChannel() {
        val channelId = context.getString(R.string.notification_channel_id)
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = descriptionText

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun registerBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.extras?.apply {
                    isRunning = getBoolean("isRunning")
                    elapsedMilliseconds = getLong("elapsedMilliseconds")
                    onUpdate?.invoke(elapsedMilliseconds, isRunning)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(context.getString(R.string.intent_action_time_elasped))

        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        context.unregisterReceiver(broadcastReceiver)
    }

    fun start(workId: Long, workName: String, color: Int) {
        Log.d("STOPWATCH", "MainActivity::startStopwatch()")

        this.workName = workName
        this.color = color

        val serviceIntent = Intent(context, StopwatchService::class.java).apply {
            action = context.getString(R.string.intent_action_start_stopwatch)
            putExtra("work_id", workId)
            putExtra("work_name", workName)
            putExtra("color", color)
        }
        context.startService(serviceIntent)

        bindService()
    }

    fun stop() {
        mService.stop()
        unbindService()
    }

    private fun bindService() {
        // create a bound service
        // https://developer.android.com/guide/components/bound-services#bind-started-service
        Intent(context, StopwatchService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindService() {
        if (mBound) {
            context.unbindService(connection)
        }
    }

    fun pause() {
        mService.pause()
    }

    fun resume() {
        mService.resume()
    }

    fun create() {
        Log.d("STOPWATCH", "StopwatchManager.init called")
        // create notification channel
        createNotificationChannel()
        registerBroadcastReceiver()

        // Check if service is running, if so, bind it
        if (isStopwatchServiceRunning()) {
            Log.d("STOPWATCH", "StopwatchService already running, bind")
            bindService()
        }
    }

    fun destroy() {
        unregisterBroadcastReceiver()
        unbindService()
    }

    companion object {
        @Volatile
        private var instance: StopwatchManager? = null

        fun getInstance(contextWrapper: ContextWrapper): StopwatchManager {
            return instance ?: StopwatchManager(contextWrapper).also { instance = it }
        }
    }
}
