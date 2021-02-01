package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
        AlertDialog.Builder(requireActivity())
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