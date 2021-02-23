package com.intelliavant.mytimetracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.common.base.Stopwatch
import com.intelliavant.mytimetracker.data.AppDatabase
import com.intelliavant.mytimetracker.data.WorkRepository
import com.intelliavant.mytimetracker.utils.formatTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.observeOn
import java.time.LocalTime
import java.util.*
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.coroutines.coroutineContext

class StopwatchService : Service() {
    private val timer: Timer = Timer()
    private val NOTIFICATION_ID = 101
    private var currentStopwatchSec: Long = -1;

    private var workId: Long = 0L
    private var workName: String? = null

    private val binder = StopwatchServiceBinder()

    private  var startTime: LocalTime? = null
    private var elapsedMillisecondsUntilLastStop: Long = 0
    private var elapsedMilliseconds: Long = 0
    private var isRunning: Boolean = false

    inner class StopwatchServiceBinder : Binder() {
        fun getService(): StopwatchService = this@StopwatchService
    }

    private fun updateElapsedMilliseconds() {
        startTime?.let {
            elapsedMilliseconds = elapsedMillisecondsUntilLastStop + it.until(LocalTime.now(), MILLIS)
        }
    }

    override fun onCreate() {
        super.onCreate()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateElapsedMilliseconds()

                val sec = elapsedMilliseconds / 1000;

                if (currentStopwatchSec != sec) {
                    // send elapsed time back to `StopwatchActivity`
                    broadcastStatus()

                    // Update notification
                    updateNotification()

                    // Save to db
                    if (workId != 0L) {
                        AppDatabase.getInstance(applicationContext).workDao().updateDuration(workId, elapsedMilliseconds)
                    }

                    currentStopwatchSec = sec
                }
            }
        }, 0, 100)

        createNotificationChannel()

        startForeground(NOTIFICATION_ID, getNotification())
    }

    private fun broadcastStatus() {
        val timerIntent = Intent()
        timerIntent.action = getString(R.string.intent_action_time_elasped)
        timerIntent.putExtra("workName", workName)
        timerIntent.putExtra("elapsedMilliseconds", elapsedMilliseconds)
        timerIntent.putExtra("isRunning", isRunning)
        sendBroadcast(timerIntent)
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = descriptionText

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun updateNotification() {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getNotification(): Notification {
        // Create notification builder
        val contentIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val channelId = getString(R.string.notification_channel_id)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
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
        if (workName != null) {
            contentBuilder.append(workName)
            contentBuilder.append(": ")
        }
        contentBuilder.append(formatTime(elapsedMilliseconds))
        builder.setContentText(contentBuilder.toString())

        if (isRunning) {
            builder.addAction(android.R.drawable.ic_media_pause, getString(R.string.pause), actionPendingIntent)
        } else {
            builder.addAction(android.R.drawable.ic_media_play, getString(R.string.resume), actionPendingIntent)
        }
        return builder.build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("STOPWATCH", "Stopwatch.onBind() called")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("STOPWATCH", "Stopwatch.onStartCommand() called, intent.action = ${intent?.action}")

        if (intent?.action == getString(R.string.intent_action_pause_resume_stopwatch)) {
            if (isRunning) {
                pause();
            } else {
                resume();
            }
        }

        if (intent?.action == getString(R.string.intent_action_start_stopwatch)) {
            workId = intent.getLongExtra("work_id", 0)
            Log.d("STOPWATCH", "workName=${workName}")
            if (workId > 0) {
                // TODO: should terminate the service if workId === 0
                CoroutineScope(Dispatchers.IO).launch {
                    val work = AppDatabase.getInstance(applicationContext).workDao().findById(workId)
                    workName = work.work.name
                    elapsedMillisecondsUntilLastStop = work.work.duration
                    elapsedMilliseconds = work.work.duration

                }
            }

//            resume()
        }

        if (intent?.action == getString(R.string.intent_action_query_state)) {
            broadcastStatus()
        }

        return START_STICKY
    }

    fun pause() {
        Log.d("STOPWATCH", "StopwatchService.pause() called")
        if (isRunning) {
            isRunning = false
            updateElapsedMilliseconds()
            elapsedMillisecondsUntilLastStop = elapsedMilliseconds
            startTime = null

            broadcastStatus()
            updateNotification()
        }
    }

    fun resume() {
        Log.d("STOPWATCH", "StopwatchService.resume() called")
        if (!isRunning) {
            isRunning = true
            startTime = LocalTime.now()

            broadcastStatus()
            updateNotification()
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

        if (isRunning) {
            pause()
        }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }
}