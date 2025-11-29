package com.cafeteria.verdadeoudesafio.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.cafeteria.verdadeoudesafio.ui.theme.*
import com.google.accompanist.permissions.*
import java.io.File
import java.io.FileInputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoCaptureScreen(
    challengedPlayer: String,
    onPhotoSelected: (Uri?) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoFile by remember { mutableStateOf<File?>(null) }
    var showConsentDialog by remember { mutableStateOf(false) }
    var consentGiven by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Gerenciador de permissões
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val storagePermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // Launcher para galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    // Launcher para câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null && tempPhotoFile != null) {
            // Salvar foto na galeria
            val savedUri = savePhotoToGallery(context, tempPhotoFile!!)
            if (savedUri != null) {
                showSuccessMessage = true
                onPhotoSelected(savedUri)
            } else {
                onPhotoSelected(photoUri)
            }
            // Limpar arquivo temporário
            tempPhotoFile?.delete()
            tempPhotoFile = null
        } else {
            photoUri = null
            tempPhotoFile?.delete()
            tempPhotoFile = null
        }
    }

    fun createImageFile(): Uri? {
        return try {
            val imageFile = File(
                context.cacheDir,
                "VERDADE_DESAFIO_${System.currentTimeMillis()}.jpg"
            )
            tempPhotoFile = imageFile
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun launchCamera() {
        when {
            cameraPermissionState.status.isGranted -> {
                photoUri = createImageFile()
                photoUri?.let { uri ->
                    try {
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onSkip()
                    }
                } ?: onSkip()
            }
            cameraPermissionState.status.shouldShowRationale -> {
                showConsentDialog = false
            }
            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    fun launchGallery() {
        when {
            storagePermissionState.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                try {
                    galleryLauncher.launch("image/*")
                } catch (e: Exception) {
                    e.printStackTrace()
                    onSkip()
                }
            }
            storagePermissionState.status.shouldShowRationale -> {
                showConsentDialog = false
            }
            else -> {
                storagePermissionState.launchPermissionRequest()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = NeonBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SALVAR MEMÓRIA?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Deseja capturar uma foto de $challengedPlayer realizando este desafio?",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botão Tirar Foto
            Button(
                onClick = {
                    pendingAction = "camera"
                    showConsentDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "TIRAR FOTO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Escolher da Galeria
            OutlinedButton(
                onClick = {
                    pendingAction = "gallery"
                    showConsentDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "ESCOLHER DA GALERIA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Pular
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Não salvar foto",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            // Mensagem de sucesso
            if (showSuccessMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "✓ Foto salva na galeria!",
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Diálogo de Consentimento
    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = {
                showConsentDialog = false
                consentGiven = false
                pendingAction = null
            },
            title = {
                Text(
                    text = "Consentimento",
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            },
            text = {
                Column {
                    Text(
                        text = "Todos os participantes da foto consentiram com a captura e armazenamento da imagem?",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = consentGiven,
                            onCheckedChange = { consentGiven = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Sim, todos consentiram",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (consentGiven) {
                            showConsentDialog = false
                            when (pendingAction) {
                                "camera" -> launchCamera()
                                "gallery" -> launchGallery()
                            }
                            pendingAction = null
                            consentGiven = false
                        }
                    },
                    enabled = consentGiven,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    Text("CONTINUAR")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConsentDialog = false
                    consentGiven = false
                    pendingAction = null
                }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            containerColor = DarkCard
        )
    }
}

// Função para salvar foto na galeria
private fun savePhotoToGallery(context: Context, photoFile: File): Uri? {
    return try {
        val bitmap = BitmapFactory.decodeStream(FileInputStream(photoFile))

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Verdade_ou_Desafio_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Verdade ou Desafio")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }

            uri
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}       