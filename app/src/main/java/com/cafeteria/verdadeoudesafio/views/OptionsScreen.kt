package com.example.truthordare.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    bottleImageUri: Uri?,
    customTruths: List<String>,
    customDares: List<String>,
    onBottleImageChanged: (Uri?) -> Unit,
    onAddTruth: (String) -> Unit,
    onRemoveTruth: (Int) -> Unit,
    onAddDare: (String) -> Unit,
    onRemoveDare: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var newTruth by remember { mutableStateOf("") }
    var newDare by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onBottleImageChanged(uri)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                Text(
                    text = "OPÇÕES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRed.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = if (bottleImageUri != null) "MUDAR IMAGEM DA GARRAFA" else "ESCOLHER IMAGEM DA GARRAFA",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCard,
                contentColor = NeonRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "VERDADES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (selectedTab == 0) NeonBlue else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "DESAFIOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (selectedTab == 1) NeonRed else Color.Gray
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> QuestionsListTab(
                    questions = customTruths,
                    newQuestion = newTruth,
                    onNewQuestionChanged = { newTruth = it },
                    onAdd = {
                        if (newTruth.isNotBlank()) {
                            onAddTruth(newTruth)
                            newTruth = ""
                        }
                    },
                    onDelete = onRemoveTruth,
                    color = NeonBlue
                )
                1 -> QuestionsListTab(
                    questions = customDares,
                    newQuestion = newDare,
                    onNewQuestionChanged = { newDare = it },
                    onAdd = {
                        if (newDare.isNotBlank()) {
                            onAddDare(newDare)
                            newDare = ""
                        }
                    },
                    onDelete = onRemoveDare,
                    color = NeonRed
                )
            }
        }
    }
}

@Composable
fun QuestionsListTab(
    questions: List<String>,
    newQuestion: String,
    onNewQuestionChanged: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
    color: Color
) {
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onDelete(index) }) {
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