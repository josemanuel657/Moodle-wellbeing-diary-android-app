package com.example.myapplication.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entities.Image
import com.example.myapplication.ui.model.CameraModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.example.myapplication.data.dao.DayDao
import com.example.myapplication.data.dao.ImageDao
import java.time.LocalDate


class CameraViewModel(
    private val cameraModel: CameraModel,
    private val imageDao: ImageDao,
    private val dayDao: DayDao,
    private val date: LocalDate,
    private val numImages: Int,
) : ViewModel() {

    private val _capturedImages = MutableStateFlow<List<Image?>>(List(numImages) { null })
    val capturedImages: StateFlow<List<Image?>> get() = _capturedImages

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> get() = _permissionGranted

    init {
        viewModelScope.launch {
            dayDao.getOrCreateDay(date)
            getImagesForDay()
        }
    }

    companion object {
        fun getFactory(context: Context, date: LocalDate, numImages: Int): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
                        val cameraModel = CameraModel(context)
                        val db = DatabaseClient.getDatabase(context)
                        @Suppress("UNCHECKED_CAST")
                        return CameraViewModel(cameraModel, db.imageDao(), db.dayDao(), date, numImages) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

    fun checkCameraPermission(context: Context) {
        _permissionGranted.value = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (_permissionGranted.value) {
            cameraModel.startCamera(previewView, lifecycleOwner)
        }
    }

    fun takePictureForSlot(slotIndex: Int) {
        viewModelScope.launch {
            try {
                cameraModel.takePicture { uri ->
                    uri?.let {
                        viewModelScope.launch {
                            val image = saveImageToDb(uri)
                            val updatedImages = _capturedImages.value.toMutableList().apply {
                                this[slotIndex] = image
                            }
                            _capturedImages.value = updatedImages
                        }
                    } ?: run {
                        Log.e("CameraViewModel", "Error saving image: path was null")
                    }
                }
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Exception in takePicture", e)
            }
        }
    }

    fun getImagesForDay() {
        viewModelScope.launch {
            val imagesFromDb = imageDao.getImagesForDay(date)
            val paddedImages = MutableList(numImages) { index ->
                imagesFromDb.getOrNull(index)
            }
            _capturedImages.value = paddedImages
            Log.d("CameraViewModel", "Loaded ${paddedImages.size} images for day")
            Log.d("CameraViewModel", "Loaded ${paddedImages.size} uris for day")
        }
    }

    private suspend fun saveImageToDb(uri: Uri): Image {
        val image = Image(dayDate = date, uri = uri)
        val id = imageDao.insertImage(image)
        return image.copy(id = id)
    }

    override fun onCleared() {
        super.onCleared()
        cameraModel.shutdown()
    }
}
