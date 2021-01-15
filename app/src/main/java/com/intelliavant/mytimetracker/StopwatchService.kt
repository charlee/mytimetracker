package com.intelliavant.mytimetracker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.common.base.Stopwatch
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.TimeUnit

class StopwatchService : Service() {
    private lateinit var stopwatch: Stopwatch
    private val timer: Timer = Timer()
    private val NOTIFICATION_ID = 101
    private var currentStopwatchSec: Long = -1;
    private var activityName: String? = null

    private val binder = StopwatchServiceBinder()

    inner class StopwatchServiceBinder : Binder() {
        fun getService(): StopwatchService = this@StopwatchService
    }

    override fun onCreate() {
        super.onCreate()

        stopwatch = Stopwatch.createUnstarted()
        stopwatch.start()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val elapsedMilliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS)
                val sec = elapsedMilliseconds / 1000;

                if (currentStopwatchSec != sec) {
                    // send elapsed time back to `MainActivity`
                    val elapsedMilliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS)
                    val timerIntent = Intent()
                    timerIntent.action = "TimerElapsed"
                    timerIntent.putExtra("elapsedMilliseconds", elapsedMilliseconds)
                    sendBroadcast(timerIntent)

                    // Update notification
                    updateNotification()
                    currentStopwatchSec = sec
                }
            }
        }, 0, 100)

        val elapsedMilliseconds = stopwatch.elapsed(TimeUnit.MILLISECONDS)
        startForeground(NOTIFICATION_ID, getNotification())
    }

    private fun formatTime(milliseconds: Long): String {
        val secs = milliseconds / 1000;
        val hours = (secs / 3600).toString().padStart(2, '0');
        val minutes = ((secs % 3600) / 60).toString().padStart(2, '0');
        val seconds = (secs % 60).toString().padStart(2, '0');

        return "$hours:$minutes:$seconds";
    }

    private fun updateNotification() {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getNotification(): Notification {
        // Create notification builder
        val contentIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val channelId = getString(R.string.notification_channel_id)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_timer_icon)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)

        // Create a pause/start button
        // https://stackoverflow.com/a/49551341/905321
        val actionIntent = Intent(this, StopwatchService::class.java).apply {
            action = getString(R.string.intent_action_pause_resume_stopwatch)
        }
        val actionPendingIntent = PendingIntent.getService(this, 0, actionIntent, 0)

        val contentBuilder: StringBuilder = StringBuilder()
        if (activityName != null) {
            contentBuilder.append(activityName)
            contentBuilder.append(": ")
        }
        contentBuilder.append(formatTime(stopwatch.elapsed(TimeUnit.MILLISECONDS)))
        builder.setContentText(contentBuilder.toString())

        if (stopwatch.isRunning) {
            builder.addAction(android.R.drawable.ic_media_pause, getString(R.string.pause), actionPendingIntent)
        } else {
            builder.addAction(android.R.drawable.ic_media_play, getString(R.string.resume), actionPendingIntent)
        }
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("STOPWATCH", "Stopwatch.onStartCommand() called")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("STOPWATCH", "Stopwatch.onStartCommand() called, intent.action = ${intent?.action}")

        if (intent?.action == getString(R.string.intent_action_pause_resume_stopwatch)) {
            if (stopwatch.isRunning) {
                pause();
            } else {
                resume();
            }
        }

        if (intent?.action == getString(R.string.intent_action_start_stopwatch)) {
            activityName = intent.getStringExtra("activity_name")
            Log.d("STOPWATCH", "activityName=${activityName}")
        }

        return START_STICKY
    }

    fun pause() {
        Log.d("STOPWATCH", "StopwatchService.pause() called")
        if (stopwatch.isRunning) {
            stopwatch.stop()
            updateNotification()
        }
    }

    fun resume() {
        Log.d("STOPWATCH", "StopwatchService.resume() called")
        if (!stopwatch.isRunning) {
            stopwatch.start()
        }
    }

    fun stop() {
        Log.d("STOPWATCH", "StopwatchService.stop() called")
        pause()
        stopSelf()
    }

    override fun onDestroy() {
        Log.d("STOPWATCH", "StopwatchService.onDestroy() called")

        timer.cancel()

        if (stopwatch.isRunning) {
            stopwatch.stop()
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }
}