package com.intelliavant.mytimetracker

import android.content.res.Configuration
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
        sm = StopwatchManager.getInstance(requireActivity())

        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        // The big pause/resume button
        binding.pauseResumeButton.setOnClickListener {
            if (sm.isRunning) {
                sm.pause()
            } else {
                sm.resume()
            }
        }

        // Stop button handler
        binding.stopButton.setOnClickListener {
            stopWork()
        }

        // Setup background color
//        binding.stopwatchLayout.setBackgroundColor(sm.color)

        sm.onUpdate = { elapsedMilliseconds, isRunning ->
            binding.isRunning = isRunning
            binding.timerText = formatTime(elapsedMilliseconds)
        }

        // Name and state
        binding.workName = sm.workName
        binding.isRunning = sm.isRunning
        binding.timerText = formatTime(sm.elapsedMilliseconds)

        return binding.root
    }

    fun stopWork() {
        // TODO: replace with isNightModeActive after SDK upgrade
        val isNightMode = when (requireActivity().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        val icon = if (isNightMode) R.drawable.ic_round_warning_24_night else R.drawable.ic_round_warning_24

        AlertDialog.Builder(requireActivity())
            .setTitle("Stop activity")
            .setMessage("Are you sure to stop this activity?")
            .setPositiveButton(R.string.stop) { dialog, _ ->
                dialog.dismiss()
                sm.stop()

                findNavController().popBackStack()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setIcon(icon)
            .show()

    }
}