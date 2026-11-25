package com.example.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.Municipality
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.UrgenciaRojo
import com.example.ui.theme.UrgenciaRojoFondo
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeMentaSuave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors

private const val TAG = "CropCameraScreen"

data class CapturedCropPhoto(
    val uri: Uri,
    val bitmap: Bitmap
)

@Composable
fun CropCameraScreen(
    municipality: Municipality,
    onClose: () -> Unit,
    onPhotoCaptured: (photo: CapturedCropPhoto, analyzeImmediately: Boolean) -> Unit
) {
    BackHandler(onBack = onClose)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var canSwitchLens by remember { mutableStateOf(false) }

    var isCapturing by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var capturedPhoto by remember { mutableStateOf<CapturedCropPhoto?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProvider?.unbindAll() }
            cameraExecutor.shutdown()
        }
    }

    DisposableEffect(lifecycleOwner, lensFacing, capturedPhoto == null) {
        if (capturedPhoto != null) {
            return@DisposableEffect onDispose { }
        }

        val providerFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)

        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    cameraProvider = provider

                    val hasBack = provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                    val hasFront = provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
                    canSwitchLens = hasBack && hasFront

                    val resolvedLens = when {
                        lensFacing == CameraSelector.LENS_FACING_BACK && hasBack -> CameraSelector.LENS_FACING_BACK
                        lensFacing == CameraSelector.LENS_FACING_FRONT && hasFront -> CameraSelector.LENS_FACING_FRONT
                        hasBack -> CameraSelector.LENS_FACING_BACK
                        hasFront -> CameraSelector.LENS_FACING_FRONT
                        else -> {
                            cameraError = "No se detectó ninguna cámara disponible en este dispositivo."
                            return@addListener
                        }
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(resolvedLens)
                        .build()

                    val preview = Preview.Builder()
                        .build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }

                    val rotation = previewView.display?.rotation ?: android.view.Surface.ROTATION_0
                    val captureUseCase = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setFlashMode(flashMode)
                        .setTargetRotation(rotation)
                        .build()

                    provider.unbindAll()
                    boundCamera = provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        captureUseCase
                    )
                    imageCapture = captureUseCase
                    cameraError = null
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to bind CameraX use cases", e)
                    cameraError = "No se pudo iniciar CameraX: ${e.localizedMessage ?: "error de hardware"}."
                }
            },
            mainExecutor
        )

        onDispose {
            runCatching { cameraProvider?.unbindAll() }
        }
    }

    val previewPhoto = capturedPhoto

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (previewPhoto != null) {
            // Vista previa de la foto capturada antes de enviar a GeminiVisionService
            Image(
                bitmap = previewPhoto.bitmap.asImageBitmap(),
                contentDescription = "Fotografía capturada del cultivo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            // Barra superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Foto lista · ${municipality.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = BlancoPuro,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.65f),
                        contentColor = BlancoPuro
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("camerax_close_button")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar cámara")
                }
            }

            // Controles de confirmación en la parte inferior
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = VerdeBosqueAndino.copy(alpha = 0.94f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "¿La imagen muestra con claridad la hoja, panoja o raíz?",
                        style = MaterialTheme.typography.titleMedium,
                        color = BlancoPuro
                    )
                    Button(
                        onClick = { onPhotoCaptured(previewPhoto, true) },
                        colors = ButtonDefaults.buttonColors(containerColor = AzulCieloNasa),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .semantics(mergeDescendants = true) {}
                            .testTag("camerax_analyze_now_button")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Analizar ahora con Gemini IA", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { capturedPhoto = null },
                            border = BorderStroke(1.5.dp, VerdeMentaSuave),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .semantics(mergeDescendants = true) {}
                                .testTag("camerax_retake_button")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = BlancoPuro)
                            Spacer(Modifier.width(6.dp))
                            Text("Repetir", color = BlancoPuro)
                        }
                        Button(
                            onClick = { onPhotoCaptured(previewPhoto, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DoradoAltiplano,
                                contentColor = VerdeBosqueAndino
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .semantics(mergeDescendants = true) {}
                                .testTag("camerax_use_photo_button")
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Usar foto", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Visor en vivo CameraX
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Marco guía agrícola para encuadre en campo
            CropTargetingOverlay(modifier = Modifier.fillMaxSize())

            // Barra superior: cerrar, flash, cambio de lente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = BlancoPuro
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("camerax_close_button")
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Volver al escáner")
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cámara Jark'a Yapu · ${municipality.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = BlancoPuro,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val hasFlashUnit = boundCamera?.cameraInfo?.hasFlashUnit() == true
                    if (hasFlashUnit) {
                        IconButton(
                            onClick = {
                                val nextFlash = when (flashMode) {
                                    ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                    ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                    else -> ImageCapture.FLASH_MODE_AUTO
                                }
                                flashMode = nextFlash
                                imageCapture?.flashMode = nextFlash
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Black.copy(alpha = 0.6f),
                                contentColor = DoradoAltiplano
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("camerax_flash_button")
                        ) {
                            Icon(
                                imageVector = when (flashMode) {
                                    ImageCapture.FLASH_MODE_ON -> Icons.Filled.FlashOn
                                    ImageCapture.FLASH_MODE_OFF -> Icons.Filled.FlashOff
                                    else -> Icons.Filled.FlashAuto
                                },
                                contentDescription = "Modo de flash"
                            )
                        }
                    }

                    if (canSwitchLens) {
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Black.copy(alpha = 0.6f),
                                contentColor = BlancoPuro
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("camerax_switch_lens_button")
                        ) {
                            Icon(Icons.Filled.Cameraswitch, contentDescription = "Cambiar cámara")
                        }
                    }
                }
            }

            // Mensaje de error de cámara o instrucción + obturador inferior
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!cameraError.isNullOrBlank()) {
                    Surface(
                        color = UrgenciaRojoFondo,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, UrgenciaRojo)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = UrgenciaRojo)
                            Text(
                                text = cameraError.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = UrgenciaRojo
                            )
                        }
                    }
                } else {
                    Surface(
                        color = Color.Black.copy(alpha = 0.62f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Enfoca la hoja, panoja o raíz afectada a ~20 cm con buena luz del sol",
                            style = MaterialTheme.typography.bodySmall,
                            color = BlancoPuro,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // Botón obturador de alto contraste (>= 72.dp)
                Button(
                    onClick = {
                        val capture = imageCapture
                        if (capture == null || isCapturing) return@Button
                        isCapturing = true
                        cameraError = null

                        val photoFile = createCropPhotoFile(context)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        capture.takePicture(
                            outputOptions,
                            cameraExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    coroutineScope.launch {
                                        val decodedBitmap = decodeUprightBitmap(photoFile)
                                        if (decodedBitmap != null) {
                                            val fileUri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                photoFile
                                            )
                                            capturedPhoto = CapturedCropPhoto(
                                                uri = fileUri,
                                                bitmap = decodedBitmap
                                            )
                                        } else {
                                            cameraError = "No se pudo procesar la foto capturada. Intenta nuevamente."
                                        }
                                        isCapturing = false
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e(TAG, "CameraX capture failed", exception)
                                    coroutineScope.launch {
                                        cameraError = "Error al capturar la fotografía: ${exception.message ?: "intenta de nuevo"}."
                                        isCapturing = false
                                    }
                                }
                            }
                        )
                    },
                    enabled = !isCapturing && imageCapture != null,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DoradoAltiplano,
                        contentColor = VerdeBosqueAndino
                    ),
                    modifier = Modifier
                        .size(78.dp)
                        .border(4.dp, BlancoPuro, CircleShape)
                        .clip(CircleShape)
                        .testTag("camerax_shutter_button")
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = VerdeBosqueAndino,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "Capturar fotografía del cultivo",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun CropTargetingOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val boxWidth = size.width * 0.78f
        val boxHeight = size.height * 0.42f
        val left = (size.width - boxWidth) / 2f
        val top = (size.height - boxHeight) / 2f

        drawRoundRect(
            color = DoradoAltiplano.copy(alpha = 0.85f),
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}

private fun createCropPhotoFile(context: Context): File {
    val photoDir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
    return File(photoDir, "camerax_${System.currentTimeMillis()}.jpg")
}

private suspend fun decodeUprightBitmap(file: File): Bitmap? = withContext(Dispatchers.IO) {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return@withContext null
    val rotationDegrees = runCatching {
        val exif = ExifInterface(file.absolutePath)
        when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }.getOrDefault(0f)

    if (rotationDegrees == 0f) {
        bitmap
    } else {
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
