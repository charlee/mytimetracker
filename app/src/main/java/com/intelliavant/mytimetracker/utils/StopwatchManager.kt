package com.intelliavant.mytimetracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.IBinder
import android.util.Log
import com.intelliavant.mytimetracker.R
import com.intelliavant.mytimetracker.StopwatchService
import com.intelliavant.mytimetracker.data.Work

typealias  OnUpdateListener = (elapsedMilliseconds: Long, isRunning: Boolean) -> Unit


class StopwatchManager(private val context: Context) {

    private var mBound = false
    private lateinit var mService: StopwatchService
    private lateinit var broadcastReceiver: BroadcastReceiver

    var isRunning = false
        private set

    var elapsedMilliseconds = 0L
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

    fun destroy() {
        unregisterBroadcastReceiver()
    }

    fun start(workId: Long, workName: String) {
        Log.d("STOPWATCH", "MainActivity::startStopwatch()")

        val serviceIntent = Intent(context, StopwatchService::class.java).apply {
            action = context.getString(R.string.intent_action_start_stopwatch)
            putExtra("work_id", workId)
            putExtra("work_name", workName)
        }
        context.startService(serviceIntent)

        // create a bound service
        // https://developer.android.com/guide/components/bound-services#bind-started-service
        Intent(context, StopwatchService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun stop() {
        mService.stop()
        if (mBound) {
            context.unbindService(connection)
            mBound = false
        }
    }

    fun pause() {
        mService.pause()
    }

    fun resume() {
        mService.resume()
    }

    init {
        // create notification channel
        createNotificationChannel()

        registerBroadcastReceiver()
    }

    companion object {
        @Volatile
        private var instance: StopwatchManager? = null

        fun getInstance(contextWrapper: ContextWrapper): StopwatchManager {
            return instance ?: StopwatchManager(contextWrapper).also { instance = it }
        }
    }
}
