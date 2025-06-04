package com.example.myapplication.ui.model

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraModel(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraModel", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture(onImageCaptured: (Uri?) -> Unit) {
        val imageCapture = imageCapture ?: run {
            Log.e("CameraModel", "ImageCapture is not initialized.")
            onImageCaptured(null)
            return
        }

        val picturesDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "DiaryApp"
        ).apply {
            if (!exists()) mkdirs()
        }

        val photoFile = File(picturesDir, "photo_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, executorService, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri
                Log.d("CameraModel", "Image saved at: $savedUri")
                onImageCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraModel", "Photo capture failed: ${exception.message}", exception)
                onImageCaptured(null)
            }
        })
    }

    fun shutdown() {
        executorService.shutdown()
    }
}
