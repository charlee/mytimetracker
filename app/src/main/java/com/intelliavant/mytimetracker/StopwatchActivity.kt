package com.intelliavant.mytimetracker

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.intelliavant.mytimetracker.databinding.ActivityStopwatchBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StopwatchActivity : AppCompatActivity() {

    private var workId: Long? = null

    private lateinit var binding: ActivityStopwatchBinding

    private var mBound = false
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
        broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                TODO("Not yet implemented")
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.intent_action_time_elasped))

        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }


    private fun startStopwatch(activityName: String?) {
        Log.d("STOPWATCH", "MainActivity::startStopwatch()")

        val serviceIntent = Intent(this, StopwatchService::class.java).apply {
            action = getString(R.string.intent_action_start_stopwatch)
            putExtra("activity_name", activityName)
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


        binding = DataBindingUtil.setContentView(this, R.layout.activity_stopwatch)

        workId = intent.extras?.getLong("workId")
        workId?.let {
            workListViewModel.findById(it).observe(this, {
                Log.d("STOPWATCH", "StopwatchFragment started, work = $it")
            })
        }

        registerBroadcastReceiver()
        // start the service
        // TODO: where's the work model?
    }

    override fun onDestroy() {
        unregisterBroadcastReceiver()
        super.onDestroy()
    }
}