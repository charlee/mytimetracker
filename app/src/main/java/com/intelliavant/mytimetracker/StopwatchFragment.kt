package com.intelliavant.mytimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intelliavant.mytimetracker.databinding.FragmentStopwatchBinding
import com.intelliavant.mytimetracker.utils.StopwatchServiceUtils
import com.intelliavant.mytimetracker.utils.formatTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StopwatchFragment : Fragment() {

    // Blinking animation on the text
    private val blinking = AlphaAnimation(0.0f, 1.0f).apply {
        duration = 500
        startOffset = 0
        repeatMode = Animation.REVERSE
        repeatCount = Animation.INFINITE
    }

    private lateinit var binding: FragmentStopwatchBinding

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.extras?.apply {
                val elapsedMilliseconds = getLong("elapsedMilliseconds")
                val isRunning = getBoolean("isRunning")

                binding.workName = getString("workName")
                binding.isRunning = isRunning
                binding.timerText = formatTime(elapsedMilliseconds)

                // show blinking effect if the stopwatch is not running
                if (!isRunning && elapsedMilliseconds >= 1000) {
                    binding.timerTextView.startAnimation(blinking)
                } else {
                    binding.timerTextView.clearAnimation()
                }
            }
        }
    }

    // Handle back button
    // https://developer.android.com/guide/navigation/navigation-custom-back
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // show confirm dialog when back button pressed
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    stopWork()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        // The big pause/resume button
        binding.pauseResumeButton.setOnClickListener {
            StopwatchServiceUtils.pauseResumeStopwatch(requireContext())
        }

        // Stop button handler
        binding.stopButton.setOnClickListener {
            stopWork()
        }

        return binding.root
    }

    override fun onPause() {
        // stop receiving broadcast when paused
        requireActivity().unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        // start receiving broadcast when resumed
        val intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.intent_action_time_elasped))

        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        // query service for initial data
        StopwatchServiceUtils.query(requireContext())
    }

    fun stopWork() {
        // TODO: replace with isNightModeActive after SDK upgrade
        val isNightMode =
            when (requireActivity().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
        val icon =
            if (isNightMode) R.drawable.ic_round_warning_24_night else R.drawable.ic_round_warning_24

        AlertDialog.Builder(requireActivity())
            .setTitle("Stop activity")
            .setMessage("Are you sure to stop this activity?")
            .setPositiveButton(R.string.stop) { dialog, _ ->
                dialog.dismiss()
                StopwatchServiceUtils.stopStopwatchService(requireContext())

                findNavController().popBackStack()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setIcon(icon)
            .show()

    }
}