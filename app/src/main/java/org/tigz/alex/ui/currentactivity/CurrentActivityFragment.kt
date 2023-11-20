package org.tigz.alex.ui.currentactivity

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.tigz.alex.databinding.FragmentCurrentactivityBinding
import org.tigz.alex.service.TranscriptionService
import javax.inject.Inject

@AndroidEntryPoint
class CurrentActivityFragment : Fragment() {

    @Inject
    lateinit var transcriptionService: TranscriptionService

    private var _binding: FragmentCurrentactivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // features require a permission that the user has denied.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestRecordAudioPermission()
        super.onCreate(savedInstanceState)
    }

    private fun requestRecordAudioPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentActivityViewModel =
            ViewModelProvider(this).get(CurrentActivityViewModel::class.java)

        _binding = FragmentCurrentactivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCurrentActivity
        currentActivityViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // when the record button is held down then record the audio
        // when the record button stops being held down then transcribe the audio
        binding.btnRecord.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> transcriptionService.startRecording()
                MotionEvent.ACTION_UP -> transcriptionService.stopRecordingAndTranscribe()
            }
            true
        }

        // play on play
        binding.btnPlay.setOnClickListener {
            transcriptionService.playCurrentAudio()
        }
    }

    override fun onPause() {
        super.onPause()
        transcriptionService.cleanUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        transcriptionService.cleanUp()
    }

}