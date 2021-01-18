package com.intelliavant.mytimetracker

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.intelliavant.mytimetracker.databinding.ActivityStopwatchBinding
import com.intelliavant.mytimetracker.utils.formatTime
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import com.intelliavant.mytimetracker.viewmodel.WorkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StopwatchActivity : AppCompatActivity() {

    private var workId: Long? = null

    private lateinit var binding: ActivityStopwatchBinding

    private var mBound = false
    private var isRunning = false
    private lateinit var mService: StopwatchService
    private lateinit var broadcastReceiver: BroadcastReceiver

    private val workListViewModel: WorkListViewModel by viewModels()

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

    private fun registerBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.extras?.apply {
                    isRunning = getBoolean("isRunning")
                    val elaspedMilliseconds = getLong("elapsedMilliseconds")
                    binding.timerText = formatTime(elaspedMilliseconds)
                    binding.isRunning = isRunning
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.intent_action_time_elasped))

        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }


    private fun startStopwatch(workName: String?) {
        Log.d("STOPWATCH", "MainActivity::startStopwatch()")

        val serviceIntent = Intent(this, StopwatchService::class.java).apply {
            action = getString(R.string.intent_action_start_stopwatch)
            putExtra("work_name", workName)
        }
        startService(serviceIntent)

        // create a bound service
        // https://developer.android.com/guide/components/bound-services#bind-started-service
        Intent(this, StopwatchService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopStopwatch() {
        mService.stop()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
    }

    private fun pauseStopwatch() {
        mService.pause()
    }

    private fun resumeStopwatch() {
        mService.resume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBroadcastReceiver()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_stopwatch)

        binding.pauseResumeButton.setOnClickListener {
            if (isRunning) {
                pauseStopwatch()
            } else {
                resumeStopwatch()
            }
        }

        binding.stopButton.setOnClickListener {
            stopWork()
        }

        workId = intent.extras?.getLong("workId")
        workId?.let { workId ->
            workListViewModel.findById(workId).observe(this) { work ->
                Log.d("STOPWATCH", "StopwatchFragment started, work = $work")
                binding.viewModel = WorkViewModel(work)
                binding.timerText = formatTime(0)

                // start the service
                startStopwatch(work.name)
            }
        }
    }

    override fun onDestroy() {
        unregisterBroadcastReceiver()
        stopStopwatch()
        super.onDestroy()
    }

    private fun stopWork() {
        AlertDialog.Builder(this)
            .setTitle("Stop activity")
            .setMessage("Are you sure to stop this activity?")
            .setPositiveButton(R.string.stop) { dialog, _ ->
                Log.d("STOPWATCH", "stop work")
                dialog.dismiss()
                stopStopwatch()
                finish()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}