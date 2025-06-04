package com.example.myapplication.ui.view

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.entities.User
import com.example.myapplication.ui.viewmodel.CameraViewModel
import java.time.LocalDate


@Composable
fun CameraScreen(
    user: User,
    numImages: Int = 3,
    date: LocalDate,
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val cameraViewModel: CameraViewModel = viewModel(
        key = date.toString(),
        factory = CameraViewModel.getFactory(context, date, numImages)
    )

    val previewView = remember { PreviewView(context) }

    val permissionGranted by cameraViewModel.permissionGranted.collectAsState()
    val capturedImages by cameraViewModel.capturedImages.collectAsState()

    val activeSlotIndex = remember { mutableStateOf<Int?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        cameraViewModel.checkCameraPermission(context)
    }

    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (permissionGranted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(numImages) { index ->
                TiltedPhotoFrame(
                    index = index,
                    imageUri = capturedImages.getOrNull(index)?.uri,
                    isActive = activeSlotIndex.value == index,
                    onClick = {
                        activeSlotIndex.value = index
                        cameraViewModel.startCamera(previewView, lifecycleOwner)
                    },
                    cameraViewModel = cameraViewModel,
                    lifecycleOwner = lifecycleOwner,
                    onCapture = { uri ->
                        cameraViewModel.takePictureForSlot(index)
                        activeSlotIndex.value = null
                    }
                )
            }
        }
    }
}

@Composable
fun TiltedPhotoFrame(
    index: Int,
    imageUri: Uri?,
    isActive: Boolean,
    onClick: () -> Unit,
    cameraViewModel: CameraViewModel,
    lifecycleOwner: LifecycleOwner,
    onCapture: (Unit) -> Unit
) {
    val rotation = listOf(-5f, 3f, -7f)[index % 3]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .rotate(rotation)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { if (!isActive) onClick() },

        contentAlignment = Alignment.Center
    ) {
        when {
            isActive -> {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            cameraViewModel.startCamera(this, lifecycleOwner)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Button(
                    onClick = {
                        onCapture(Unit)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Capture")
                }
            }

            imageUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Text(
                    text = "Tap to add photo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF50312F)
                )
            }
        }
    }
}
