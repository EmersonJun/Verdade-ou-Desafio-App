package com.cafeteria.verdadeoudesafio.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.database.CustomDareEntity
import com.cafeteria.verdadeoudesafio.database.CustomTruthEntity
import com.cafeteria.verdadeoudesafio.models.GameSettings
import com.cafeteria.verdadeoudesafio.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    bottleImageUri: Uri?,
    onBottleImageChanged: (Uri?) -> Unit,
    customTruths: List<CustomTruthEntity>,
    customDares: List<CustomDareEntity>,
    onAddTruth: (String) -> Unit,
    onUpdateTruth: (CustomTruthEntity) -> Unit,
    onDeleteTruth: (CustomTruthEntity) -> Unit,
    onAddDare: (String) -> Unit,
    onUpdateDare: (CustomDareEntity) -> Unit,
    onDeleteDare: (CustomDareEntity) -> Unit,
    gameSettings: GameSettings = GameSettings(),
    onSettingsChanged: (GameSettings) -> Unit = {},
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var newTruth by remember { mutableStateOf("") }
    var newDare by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(gameSettings) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onBottleImageChanged(uri)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            // Substitua apenas a seção do Header no seu OptionsScreen.kt atual:

// Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 24.dp),  // Padding adicional para afastar do topo
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = NeonRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = NeonRed,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPÇÕES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCard,
                contentColor = NeonRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("GERAL", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "VERDADES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (selectedTab == 1) NeonBlue else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "DESAFIOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (selectedTab == 2) NeonRed else Color.Gray
                        )
                    }
                )
            }

            // Content
            when (selectedTab) {
                0 -> GeneralSettingsTab(
                    bottleImageUri = bottleImageUri,
                    onImagePick = { imagePickerLauncher.launch("image/*") },
                    settings = settings,
                    onSettingsChange = { newSettings ->
                        settings = newSettings
                        onSettingsChanged(newSettings)
                    }
                )
                1 -> QuestionsListTabWithEdit(
                    questions = customTruths,
                    newQuestion = newTruth,
                    onNewQuestionChanged = { newTruth = it },
                    onAdd = {
                        if (newTruth.isNotBlank()) {
                            onAddTruth(newTruth)
                            newTruth = ""
                        }
                    },
                    onUpdate = { entity -> onUpdateTruth(entity) },
                    onDelete = { entity -> onDeleteTruth(entity) },
                    color = NeonBlue
                )
                2 -> QuestionsListTabWithEdit(
                    questions = customDares,
                    newQuestion = newDare,
                    onNewQuestionChanged = { newDare = it },
                    onAdd = {
                        if (newDare.isNotBlank()) {
                            onAddDare(newDare)
                            newDare = ""
                        }
                    },
                    onUpdate = { entity -> onUpdateDare(entity) },
                    onDelete = { entity -> onDeleteDare(entity) },
                    color = NeonRed
                )
            }
        }
    }
}

@Composable
fun <T> QuestionsListTabWithEdit(
    questions: List<T>,
    newQuestion: String,
    onNewQuestionChanged: (String) -> Unit,
    onAdd: () -> Unit,
    onUpdate: (T) -> Unit,
    onDelete: (T) -> Unit,
    color: Color
) where T : Any {
    var editingQuestion by remember { mutableStateOf<T?>(null) }
    var editText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ADICIONAR NOVA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newQuestion,
                    onValueChange = onNewQuestionChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Digite aqui...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = color,
                        unfocusedBorderColor = Color.Gray
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(12.dp),
                    enabled = newQuestion.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("ADICIONAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LISTA (${questions.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(questions) { index, question ->
                val questionText = when (question) {
                    is CustomTruthEntity -> question.question
                    is CustomDareEntity -> question.question
                    else -> ""
                }

                val isDefault = when (question) {
                    is CustomTruthEntity -> question.createdAt == 0L
                    is CustomDareEntity -> question.createdAt == 0L
                    else -> false
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDefault)
                            DarkCard.copy(alpha = 0.7f)
                        else
                            DarkCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (editingQuestion == question) {
                        // Modo de edição
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = editText,
                                onValueChange = { editText = it },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = color,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        editingQuestion = null
                                        editText = ""
                                    }
                                ) {
                                    Text("CANCELAR", color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (editText.isNotBlank()) {
                                            val updated = when (question) {
                                                is CustomTruthEntity -> question.copy(question = editText)
                                                is CustomDareEntity -> question.copy(question = editText)
                                                else -> question
                                            }
                                            @Suppress("UNCHECKED_CAST")
                                            onUpdate(updated as T)
                                            editingQuestion = null
                                            editText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = color),
                                    enabled = editText.isNotBlank()
                                ) {
                                    Text("SALVAR")
                                }
                            }
                        }
                    } else {
                        // Modo de visualização
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = questionText,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (isDefault) {
                                    Text(
                                        text = "Padrão",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        editingQuestion = question
                                        editText = questionText
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = color
                                    )
                                }

                                IconButton(onClick = { onDelete(question) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar",
                                        tint = NeonRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// GeneralSettingsTab e SettingRow permanecem iguais...
@Composable
fun GeneralSettingsTab(
    bottleImageUri: Uri?,
    onImagePick: () -> Unit,
    settings: GameSettings,
    onSettingsChange: (GameSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Image,
                            null,
                            tint = NeonRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "PERSONALIZAÇÃO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonRed
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onImagePick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonRed.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = if (bottleImageUri != null) "MUDAR GARRAFA" else "ESCOLHER GARRAFA",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VolumeUp,
                            null,
                            tint = NeonBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "ÁUDIO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonBlue
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    SettingRow(
                        icon = Icons.Default.MusicNote,
                        title = "Efeitos Sonoros",
                        checked = settings.soundEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(soundEnabled = it))
                        }
                    )

                    if (settings.soundEnabled) {
                        Spacer(Modifier.height(8.dp))
                        Text("Volume dos Efeitos", fontSize = 14.sp, color = Color.Gray)
                        Slider(
                            value = settings.soundVolume,
                            onValueChange = {
                                onSettingsChange(settings.copy(soundVolume = it))
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonBlue,
                                activeTrackColor = NeonBlue
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    SettingRow(
                        icon = Icons.Default.Album,
                        title = "Música de Fundo",
                        checked = settings.musicEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(musicEnabled = it))
                        }
                    )

                    if (settings.musicEnabled) {
                        Spacer(Modifier.height(8.dp))
                        Text("Volume da Música", fontSize = 14.sp, color = Color.Gray)
                        Slider(
                            value = settings.musicVolume,
                            onValueChange = {
                                onSettingsChange(settings.copy(musicVolume = it))
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonBlue,
                                activeTrackColor = NeonBlue
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Tune,
                            null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "OUTROS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    SettingRow(
                        icon = Icons.Default.Camera,
                        title = "Permitir Fotos",
                        checked = settings.allowSavePhotos,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(allowSavePhotos = it))
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    SettingRow(
                        icon = Icons.Default.Vibration,
                        title = "Vibração",
                        checked = settings.hapticEnabled,
                        onCheckedChange = {
                            onSettingsChange(settings.copy(hapticEnabled = it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                null,
                tint = if (checked) NeonBlue else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonBlue,
                checkedTrackColor = NeonBlue.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}