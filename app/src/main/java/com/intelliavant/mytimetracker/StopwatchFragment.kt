package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intelliavant.mytimetracker.databinding.FragmentStopwatchBinding
import com.intelliavant.mytimetracker.utils.StopwatchManager
import com.intelliavant.mytimetracker.utils.formatTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StopwatchFragment : Fragment() {

    private lateinit var binding: FragmentStopwatchBinding

    private lateinit var sm: StopwatchManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            sm = StopwatchManager.getInstance(it)
        }

        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        binding.pauseResumeButton.setOnClickListener {
            if (sm.isRunning) {
                sm.pause()
            } else {
                sm.resume()
            }
        }

        binding.stopButton.setOnClickListener {
            stopWork()
        }

        sm.onUpdate = { elapsedMilliseconds, isRunning ->
            binding.isRunning = isRunning
            binding.timerText = formatTime(elapsedMilliseconds)
        }

        binding.workName = sm.workName
        binding.isRunning = sm.isRunning
        binding.timerText = formatTime(sm.elapsedMilliseconds)

        return binding.root
    }

    fun stopWork() {
        activity?.let { activity ->
            AlertDialog.Builder(activity)
                .setTitle("Stop activity")
                .setMessage("Are you sure to stop this activity?")
                .setPositiveButton(R.string.stop) { dialog, _ ->
                    Log.d("STOPWATCH", "stop work")
                    dialog.dismiss()
                    sm.stop()

                    findNavController().popBackStack()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        }
    }
}