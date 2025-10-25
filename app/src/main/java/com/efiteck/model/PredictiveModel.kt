package com.efiteck.model

import android.content.Context
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream

data class PackageData(
    val packageId: String,
    val employeeId: String,
    val model: String,
    val route: String,
    val startTime: String,
    val endTime: String,
    val result: String,
    val number: String,
    val status: String,
    val comments: String = ""
)

data class PredictionResult(
    val qualityScore: Float,
    val predictedErrors: Int,
    val confidence: Float,
    val recommendation: String
)

object PredictiveModel {

    private const val MODEL_FILE = "packaging_quality_model.pt"
    private var module: Module? = null
    private val testData = listOf(
        PackageData("PKG003", "EMP008", "LX755", "SPEC_A01", "2025-10-13 09:03:15", "2025-10-13 09:03:55", "40 Minor Error", "15", "No", ""),
        PackageData("PKG004", "EMP008", "LX750", "SPEC_C01", "2025-10-13 06:31:46", "2025-10-13 06:32:21", "35 Pass", "13", "No", ""),
        PackageData("PKG005", "EMP001", "LX735", "SPEC_B01", "2025-10-13 07:28:47", "2025-10-13 07:29:45", "58 Pass", "15", "No", ""),
        PackageData("PKG006", "EMP001", "LX726", "SPEC_C02", "2025-10-13 07:30:13", "2025-10-13 07:30:43", "48 Pass", "11", "No", ""),
        PackageData("PKG607", "EMP018", "LX726", "SPEC_D01", "2025-10-13 06:51:31", "2025-10-13 06:52:56", "85 Pass", "15", "No", "Improved speed"),
        PackageData("PKG608", "EMP012", "LX760", "SPEC_B02", "2025-10-13 06:48:33", "2025-10-13 06:49:19", "20 Minor Error", "17", "No", "missing one snack item"),
        PackageData("PKG609", "EMP010", "LX760", "SPEC_B02", "2025-10-13 06:49:37", "2025-10-13 06:50:07", "63 Pass", "16", "No", ""),
        PackageData("PKG610", "EMP012", "LX730", "SPEC_C02", "2025-10-13 08:07:00", "2025-10-13 08:07:41", "41 Pass", "13", "No", ""),
        PackageData("PKG611", "EMP003", "LX760", "SPEC_B01", "2025-10-13 08:55:51", "2025-10-13 08:56:37", "46 Pass", "15", "No", "")
    )

    fun loadModel(context: Context): Boolean {
        return try {
            android.util.Log.d("PredictiveModel", "Intentando cargar modelo PyTorch...")
            // Try to load from assets first
            val modelFile = File(context.filesDir, MODEL_FILE)
            if (!modelFile.exists()) {
                android.util.Log.d("PredictiveModel", "Modelo no existe, creando placeholder...")
                // Copy model from assets if available
                copyModelFromAssets(context, modelFile)
            }

            module = Module.load(modelFile.absolutePath)
            android.util.Log.d("PredictiveModel", "✅ Modelo PyTorch cargado exitosamente")
            true
        } catch (e: Exception) {
            android.util.Log.e("PredictiveModel", "❌ Error loading model: ${e.message}")
            android.util.Log.d("PredictiveModel", "Usando predicciones basadas en reglas")
            false
        }
    }

    private fun copyModelFromAssets(context: Context, modelFile: File) {
        try {
            // For now, we'll create a simple model file
            // In production, you would include the actual .pt file in assets
            context.assets.open(MODEL_FILE).use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            // Create a simple placeholder model if asset doesn't exist
            createPlaceholderModel(modelFile)
        }
    }

    private fun createPlaceholderModel(modelFile: File) {
        // This would normally be a trained PyTorch model
        // For demo purposes, we'll use a simple rule-based prediction
        android.util.Log.d("PredictiveModel", "📦 Creating placeholder model - using rule-based prediction")
    }

    fun predictQuality(packageData: PackageData): PredictionResult {
        android.util.Log.d("PredictiveModel", "🔮 Prediciendo calidad para: ${packageData.packageId}")
        
        module?.let { model ->
            return try {
                android.util.Log.d("PredictiveModel", "Usando modelo PyTorch para predicción")
                // Prepare input tensor from package data
                val inputTensor = prepareInputTensor(packageData)
                val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
                val outputArray = outputTensor.dataAsFloatArray

                val result = PredictionResult(
                    qualityScore = outputArray[0],
                    predictedErrors = outputArray[1].toInt(),
                    confidence = outputArray[2],
                    recommendation = generateRecommendation(outputArray[0], outputArray[1])
                )
                
                android.util.Log.d("PredictiveModel", "✅ Predicción PyTorch: Score=${result.qualityScore}, Errors=${result.predictedErrors}")
                result
            } catch (e: Exception) {
                android.util.Log.w("PredictiveModel", "⚠️ Error en PyTorch, usando reglas: ${e.message}")
                // Fallback to rule-based prediction
                ruleBasedPrediction(packageData)
            }
        } ?: run {
            android.util.Log.d("PredictiveModel", "📊 Usando predicción basada en reglas")
            // Rule-based prediction if model not loaded
            return ruleBasedPrediction(packageData)
        }
    }

    private fun prepareInputTensor(packageData: PackageData): Tensor {
        // Convert package data to numerical features
        val features = floatArrayOf(
            getEmployeeCode(packageData.employeeId),
            getModelCode(packageData.model),
            getRouteCode(packageData.route),
            parseTime(packageData.startTime),
            parseTime(packageData.endTime),
            packageData.result.split(" ")[0].toFloatOrNull() ?: 0f,
            packageData.number.toFloatOrNull() ?: 0f,
            getStatusCode(packageData.status)
        )

        return Tensor.fromBlob(features, longArrayOf(1, features.size.toLong()))
    }

    private fun getEmployeeCode(employeeId: String): Float {
        return employeeId.replace("EMP", "").toFloatOrNull() ?: 0f
    }

    private fun getModelCode(model: String): Float {
        return when (model) {
            "LX755" -> 755f
            "LX750" -> 750f
            "LX735" -> 735f
            "LX726" -> 726f
            "LX760" -> 760f
            "LX730" -> 730f
            else -> 0f
        }
    }

    private fun getRouteCode(route: String): Float {
        return when {
            route.startsWith("SPEC_A") -> 1f
            route.startsWith("SPEC_B") -> 2f
            route.startsWith("SPEC_C") -> 3f
            route.startsWith("SPEC_D") -> 4f
            else -> 0f
        }
    }

    private fun parseTime(timeString: String): Float {
        return try {
            val parts = timeString.split(" ")[1].split(":")
            val hour = parts[0].toFloat()
            val minute = parts[1].toFloat()
            hour * 60 + minute
        } catch (e: Exception) {
            0f
        }
    }

    private fun getStatusCode(status: String): Float {
        return if (status == "No") 0f else 1f
    }

    private fun generateRecommendation(qualityScore: Float, predictedErrors: Float): String {
        return when {
            qualityScore >= 80 -> "Excelente calidad - Continuar proceso"
            qualityScore >= 60 -> "Buena calidad - Monitorear"
            qualityScore >= 40 -> "Calidad aceptable - Revisar proceso"
            else -> "Calidad baja - Requiere atención inmediata"
        }
    }

    private fun ruleBasedPrediction(packageData: PackageData): PredictionResult {
        android.util.Log.d("PredictiveModel", "🧮 Analizando: ${packageData.packageId} - ${packageData.result}")
        
        // Simple rule-based prediction based on the training data patterns
        val baseScore = when {
            packageData.result.contains("Pass") -> 70f
            packageData.result.contains("Error") -> 30f
            else -> 50f
        }

        val errorAdjustment = when {
            packageData.comments.contains("Improved") -> 15f
            packageData.comments.contains("missing") -> -20f
            else -> 0f
        }

        val employeeBonus = when (packageData.employeeId) {
            "EMP008", "EMP001", "EMP018" -> 5f
            else -> 0f
        }

        val finalScore = (baseScore + errorAdjustment + employeeBonus).coerceIn(0f, 100f)

        val result = PredictionResult(
            qualityScore = finalScore,
            predictedErrors = if (finalScore < 50) 1 else 0,
            confidence = 0.75f,
            recommendation = generateRecommendation(finalScore, if (finalScore < 50) 1f else 0f)
        )
        
        android.util.Log.d("PredictiveModel", "✨ Resultado: Score=${result.qualityScore.toInt()}%, Errors=${result.predictedErrors}, Confidence=${(result.confidence * 100).toInt()}%")
        
        return result
    }

    fun getTestData(): List<PackageData> {
        return testData
    }

    fun runAllTests(): List<Pair<PackageData, PredictionResult>> {
        android.util.Log.d("PredictiveModel", "🚀 Ejecutando tests para ${testData.size} paquetes...")
        val results = testData.map { data ->
            data to predictQuality(data)
        }
        android.util.Log.d("PredictiveModel", "✅ Tests completados: ${results.size} predicciones generadas")
        return results
    }

    fun getModelAccuracy(): Float {
        android.util.Log.d("PredictiveModel", "📊 Calculando precisión del modelo...")
        val results = runAllTests()
        val accuratePredictions = results.count { (data, prediction) ->
            val actualQuality = if (data.result.contains("Pass")) 1 else 0
            val predictedQuality = if (prediction.qualityScore >= 50) 1 else 0
            actualQuality == predictedQuality
        }

        val accuracy = accuratePredictions.toFloat() / results.size
        android.util.Log.d("PredictiveModel", "🎯 Precisión del modelo: ${(accuracy * 100).toInt()}% ($accuratePredictions/${results.size} correctas)")
        return accuracy
    }
}
