package com.efiteck.ai

import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content

object GeminiManager {
    
    // Initialize the Vertex AI Gemini model
    // Create a GenerativeModel instance with a model that supports your use case
    val model by lazy {
        Firebase.vertexAI.generativeModel("gemini-2.5-flash")
    }
    
    /**
     * Generate content using the Gemini model
     * @param prompt The input prompt for the model
     * @return The generated text response
     */
    suspend fun generateContent(prompt: String): Result<String> {
        return try {
            val response = model.generateContent(prompt)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate content with structured prompt
     * @param userMessage The user message
     * @return The generated text response
     */
    suspend fun chat(userMessage: String): Result<String> {
        return try {
            val content = content {
                text(userMessage)
            }
            val response = model.generateContent(content)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if the model is initialized and ready to use
     */
    fun isInitialized(): Boolean {
        return try {
            model != null
        } catch (e: Exception) {
            false
        }
    }
}

