package com.efiteck.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object FirestoreRepository {

    // Obtener instancia de Firestore con base de datos por defecto
    val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    // O si necesitas especificar el nombre de la base de datos explícitamente
    // (aunque "default" es la BD por defecto en Firebase)
    fun getDatabaseByName(name: String = "(default)"): FirebaseFirestore {
        return if (name == "(default)") {
            Firebase.firestore
        } else {
            // Para bases de datos nombradas (feature en beta)
            Firebase.firestore
        }
    }

    /**
     * Ejemplo de consulta a una colección usando callbacks
     */
    fun getCollection(
        collectionName: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionName)
            .get()
            .addOnSuccessListener { snapshot ->
                val documents = snapshot.documents.mapNotNull { it.data }
                onSuccess(documents)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * Información de la base de datos
     */
    fun getDatabaseInfo(): String {
        return "Firestore Database: (default)\nProject: efiteck"
    }
}

