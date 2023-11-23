package org.tigz.alex.ui.camera

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.tigz.alex.databinding.FragmentCameraBinding
import org.tigz.alex.service.ImageToTextService
import org.tigz.alex.service.MediaService
import org.tigz.alex.ui.currentactivity.CurrentActivityViewModel
import javax.inject.Inject
import org.tigz.alex.R

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val currentActivityViewModel: CurrentActivityViewModel by viewModels()

    @Inject
    lateinit var mediaService: MediaService

    @Inject
    lateinit var imageToTextService: ImageToTextService

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageCapture: ImageCapture

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
        requestCameraPermission()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera()
        binding.captureButton.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch(exc: Exception) {
                // Handle exceptions
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = mediaService.createImageFile()

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    // Handle the error
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Handle the saved image
                    imageToTextService.uploadImageAndFetchContents(output.savedUri!!,
                        onSuccess = { text ->
                            // move to the current activity fragment
                            // and set the text on the current activity model
                            currentActivityViewModel.updateText(text)
                            navigateToCurrentActivityFragment()
                        },
                        onError = { errorMessage ->

                        }
                    )
                }
            })
    }

    private fun navigateToCurrentActivityFragment() {
        findNavController().navigate(R.id.action_cameraFragment_to_currentActivityFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
