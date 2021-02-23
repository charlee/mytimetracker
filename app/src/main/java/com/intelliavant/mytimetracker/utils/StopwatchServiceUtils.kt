package com.intelliavant.mytimetracker.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.intelliavant.mytimetracker.R
import com.intelliavant.mytimetracker.StopwatchService

// Don't try to do anything fancy and make this class a singleton.
// Simply use static methods.
// Using singleton to handle the register/unregister of the receiver may cause
// unexpected error "LeakedIntentReceiver"

class StopwatchServiceUtils {

    companion object {
        fun isStopwatchServiceRunning(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getRunningServices(Int.MAX_VALUE).forEach {
                if (StopwatchService::class.java.name == it.service.className) {
                    return true;
                }
            }

            return false;
        }

        fun startStopwatchService(context: Context, workId: Long) {
            Log.d("STOPWATCH", "MainActivity::startStopwatch()")

            val serviceIntent = Intent(context, StopwatchService::class.java).apply {
                action = context.getString(R.string.intent_action_start_stopwatch)
                putExtra("work_id", workId)
            }
            context.startService(serviceIntent)
        }

        fun stopStopwatchService(context: Context) {
            val serviceIntent = Intent(context, StopwatchService::class.java)
            context.stopService(serviceIntent)
        }

        /** Query the service state. Used by activity when resuming from sleeping,
         * to retrieve current state of the stopwatch
         */
        fun query(context: Context) {
            // For communicating with service, I simply use `startService` to send an intent.
            // Have tried using `bindService` but managing the bound is a disaster.
            // Occasionally saw exceptions like 'service not bound'
            val serviceIntent = Intent(context, StopwatchService::class.java).apply {
                action = context.getString(R.string.intent_action_query_state)
            }
            context.startService(serviceIntent)
        }

        /**
         * Pause or resume the stopwatch.
         */
        fun pauseResumeStopwatch(context: Context) {
            val serviceIntent = Intent(context, StopwatchService::class.java).apply {
                action = context.getString(R.string.intent_action_pause_resume_stopwatch)
            }
            context.startService(serviceIntent)
        }

    }
}