package com.cafeteria.verdadeoudesafio.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cafeteria.verdadeoudesafio.ui.theme.*
import com.google.accompanist.permissions.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoRecordScreen(
    challenger: String,
    challenged: String,
    challengeType: String,
    question: String,
    onVideoRecorded: (Uri, Long) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0L) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val audioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    } else {
        rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                recordingTime++
            }
        } else {
            recordingTime = 0
        }
    }

    LaunchedEffect(permissions.allPermissionsGranted) {
        hasPermission = permissions.allPermissionsGranted
    }

    fun startRecording() {
        val name = "Verdade_Desafio_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Verdade ou Desafio")
            }
        }

        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        videoCapture?.let { capture ->
            try {
                activeRecording = capture.output
                    .prepareRecording(context, mediaStoreOutput)
                    .apply {
                        if (audioPermission.status.isGranted) {
                            withAudioEnabled()
                        }
                    }
                    .start(ContextCompat.getMainExecutor(context)) { event ->
                        when (event) {
                            is VideoRecordEvent.Start -> {
                                isRecording = true
                            }
                            is VideoRecordEvent.Finalize -> {
                                isRecording = false
                                if (!event.hasError()) {
                                    event.outputResults.outputUri.let { uri ->
                                        thread {
                                            try {
                                                val retriever = MediaMetadataRetriever()
                                                retriever.setDataSource(context, uri)
                                                val duration = retriever.extractMetadata(
                                                    MediaMetadataRetriever.METADATA_KEY_DURATION
                                                )?.toLongOrNull() ?: 0L
                                                retriever.release()
                                                onVideoRecorded(uri, duration)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                onVideoRecorded(uri, 0L)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                isRecording = false
            }
        }
    }

    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !hasPermission -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = NeonRed
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "PERMISSÕES NECESSÁRIAS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonRed,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Para gravar vídeos, precisamos de acesso à câmera e ao microfone.",
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { permissions.launchMultiplePermissionRequest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "PERMITIR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onSkip) {
                        Text("PULAR", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            }

            else -> {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = androidx.camera.core.Preview.Builder().build()
                                    preview.setSurfaceProvider(surfaceProvider)

                                    val recorder = Recorder.Builder()
                                        .setQualitySelector(
                                            QualitySelector.from(
                                                Quality.HD,
                                                FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                                            )
                                        )
                                        .build()

                                    videoCapture = VideoCapture.withOutput(recorder)

                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        videoCapture
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkCard.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = challenged.uppercase(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = challengeType,
                                fontSize = 14.sp,
                                color = if (challengeType == "Verdade") NeonBlue else NeonRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedVisibility(
                        visible = isRecording,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = NeonRed.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = String.format("%02d:%02d", recordingTime / 60, recordingTime % 60),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onSkip,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Pular",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (isRecording) {
                                    stopRecording()
                                } else {
                                    startRecording()
                                }
                            },
                            modifier = Modifier.size(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) NeonRed else Color.White
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                                contentDescription = if (isRecording) "Parar" else "Gravar",
                                tint = if (isRecording) Color.White else NeonRed,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.size(60.dp))
                    }
                }
            }
        }
    }
}