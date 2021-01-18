package com.intelliavant.mytimetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.common.base.Stopwatch
import com.intelliavant.mytimetracker.databinding.FragmentStopwatchBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StopwatchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class StopwatchFragment : Fragment() {
    private var workId: Long? = null

    private lateinit var binding: FragmentStopwatchBinding

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

        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        activity?.unregisterReceiver(broadcastReceiver)
    }


    private fun startStopwatch(activityName: String?) {
        Log.d("STOPWATCH", "MainActivity::startStopwatch()")

        val serviceIntent = Intent(activity, StopwatchService::class.java).apply {
            action = getString(R.string.intent_action_start_stopwatch)
            putExtra("activity_name", activityName)
        }
        activity?.startService(serviceIntent)

        // create a bound service
        // https://developer.android.com/guide/components/bound-services#bind-started-service
        Intent(activity, StopwatchService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopStopwatch() {
        mService.stop()
        if (mBound) {
            activity?.unbindService(connection)
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

        arguments?.let {
            workId = it.getLong("workId")

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        workId?.let {
            workListViewModel.findById(it).observe(viewLifecycleOwner, {
                Log.d("STOPWATCH", "StopwatchFragment started, workId = $id")
            })
        }
        // start the service
        // TODO: where's the work model?

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        unregisterBroadcastReceiver()
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StopwatchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StopwatchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}