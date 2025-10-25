package com.efiteck.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null
) {
    fun getInitials(): String {
        return if (displayName.isNotBlank()) {
            displayName.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
        } else {
            email.firstOrNull()?.uppercase()?.toString() ?: "U"
        }
    }
}

