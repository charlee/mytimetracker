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
        Log.d("STOPWATCH", "unregister broadcast receiver")
        context.unregisterReceiver(broadcastReceiver)
    }

    private fun bindService() {
        // create a bound service
        // https://developer.android.com/guide/components/bound-services#bind-started-service
        Intent(context, StopwatchService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        // register boardcast receiver to receive broadcast from service
        registerBroadcastReceiver()
    }

    private fun unbindService() {
        if (mBound) {
            context.unbindService(connection)
            unregisterBroadcastReceiver()
        }
    }

    fun onResume() {
        // Check if service is running, if so, bind it
        if (isStopwatchServiceRunning()) {
            Log.d("STOPWATCH", "StopwatchService already running, bind")
            bindService()
        }
    }

    fun onPause() {
        unbindService()
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

    fun pause() {
        mService.pause()
    }

    fun resume() {
        mService.resume()
    }


    companion object {
        @Volatile
        private var instance: StopwatchManager? = null

        fun getInstance(contextWrapper: ContextWrapper): StopwatchManager {
            return instance ?: StopwatchManager(contextWrapper).also { instance = it }
        }
    }
}
