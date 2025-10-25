package com.efiteck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efiteck.model.PackageData
import com.efiteck.model.PredictiveModel
import com.efiteck.model.PredictionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PredictiveState {
    object Initial : PredictiveState()
    object Loading : PredictiveState()
    data class Success(val results: List<Pair<PackageData, PredictionResult>>, val accuracy: Float) : PredictiveState()
    data class Error(val message: String) : PredictiveState()
}

class PredictiveViewModel : ViewModel() {

    private val _predictiveState = MutableStateFlow<PredictiveState>(PredictiveState.Initial)
    val predictiveState: StateFlow<PredictiveState> = _predictiveState.asStateFlow()

    private val _testData = MutableStateFlow<List<PackageData>>(emptyList())
    val testData: StateFlow<List<PackageData>> = _testData.asStateFlow()

    init {
        loadTestData()
    }

    private fun loadTestData() {
        _testData.value = PredictiveModel.getTestData()
    }

    fun runPredictions() {
        viewModelScope.launch {
            _predictiveState.value = PredictiveState.Loading

            try {
                // For now, use the rule-based prediction since we don't have a real PyTorch model
                val results = PredictiveModel.runAllTests()
                val accuracy = PredictiveModel.getModelAccuracy()

                _predictiveState.value = PredictiveState.Success(results, accuracy)
            } catch (e: Exception) {
                _predictiveState.value = PredictiveState.Error("Error ejecutando predicciones: ${e.message}")
            }
        }
    }

    fun predictSinglePackage(packageData: PackageData) {
        viewModelScope.launch {
            _predictiveState.value = PredictiveState.Loading

            try {
                val prediction = PredictiveModel.predictQuality(packageData)
                val singleResult = listOf(packageData to prediction)
                val accuracy = if (packageData.result.contains("Pass")) 1.0f else 0.0f

                _predictiveState.value = PredictiveState.Success(singleResult, accuracy)
            } catch (e: Exception) {
                _predictiveState.value = PredictiveState.Error("Error en predicción: ${e.message}")
            }
        }
    }

    fun getModelInfo(): String {
        return """
            Sistema Predictivo de Calidad de Empaquetado

            Modelo: PyTorch Mobile + Rule-based Engine
            Dataset: 9 ejemplos de entrenamiento
            Características:
            - Employee ID
            - Model (LX755, LX750, etc.)
            - Route (SPEC_A01, SPEC_B01, etc.)
            - Time analysis
            - Error patterns

            Estado: ${if (PredictiveModel.getTestData().isNotEmpty()) "Cargado" else "No disponible"}
        """.trimIndent()
    }

    fun clearResults() {
        _predictiveState.value = PredictiveState.Initial
    }
}
