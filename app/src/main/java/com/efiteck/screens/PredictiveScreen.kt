package com.efiteck.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efiteck.model.PackageData
import com.efiteck.model.PredictionResult
import com.efiteck.viewmodel.PredictiveViewModel
import com.efiteck.viewmodel.PredictiveState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictiveScreen(
    onNavigateBack: () -> Unit,
    predictiveViewModel: PredictiveViewModel = viewModel()
) {
    val predictiveState by predictiveViewModel.predictiveState.collectAsState()

    LaunchedEffect(Unit) {
        predictiveViewModel.runPredictions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sistema Predictivo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { predictiveViewModel.runPredictions() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Model Status Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Modelo Predictivo de Calidad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PyTorch Mobile - Gemini 2.5 Flash",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    when (predictiveState) {
                        is PredictiveState.Success -> {
                            val successState = predictiveState as PredictiveState.Success
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Precisión: ${(successState.accuracy * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (successState.accuracy > 0.7f) Color.Green else Color(0xFFFF9800)
                            )
                        }
                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Run Tests Button
            Button(
                onClick = { predictiveViewModel.runPredictions() },
                modifier = Modifier.fillMaxWidth(),
                enabled = predictiveState !is PredictiveState.Loading
            ) {
                when (predictiveState) {
                    is PredictiveState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ejecutando predicciones...")
                    }
                    else -> {
                        Text("Ejecutar Tests de Predicción")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results
            when (predictiveState) {
                is PredictiveState.Success -> {
                    val successState = predictiveState as PredictiveState.Success
                    LazyColumn {
                        items(successState.results) { (packageData, prediction) ->
                            PredictionCard(packageData, prediction)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                is PredictiveState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Ejecutando predicciones...")
                        }
                    }
                }
                is PredictiveState.Error -> {
                    val errorState = predictiveState as PredictiveState.Error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorState.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    // Initial state - show test data info
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sistema Predictivo",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${predictiveViewModel.testData.value.size} ejemplos de entrenamiento cargados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = predictiveViewModel.getModelInfo(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionCard(packageData: PackageData, prediction: PredictionResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = packageData.packageId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (prediction.qualityScore >= 50) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (prediction.qualityScore >= 50) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${prediction.qualityScore.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (prediction.qualityScore >= 50) Color.Green else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Package details
            Text(
                text = "Empleado: ${packageData.employeeId} | Modelo: ${packageData.model}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Ruta: ${packageData.route} | Tiempo: ${packageData.startTime} - ${packageData.endTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prediction details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Errores predichos: ${prediction.predictedErrors}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Confianza: ${(prediction.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Resultado real: ${packageData.result}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recommendation
            Text(
                text = prediction.recommendation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            // Comments if any
            if (packageData.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comentarios: ${packageData.comments}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
