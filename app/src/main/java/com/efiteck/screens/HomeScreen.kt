package com.efiteck.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.efiteck.ai.GeminiManager
import com.efiteck.components.DrawerContent
import com.efiteck.components.drawerMenuItems
import com.efiteck.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userProfile by authViewModel.userProfile.collectAsState()

    var showTestDialog by remember { mutableStateOf(false) }
    var isLoadingTest by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var testError by remember { mutableStateOf<String?>(null) }

    // Test Gemini AI
    fun testGeminiAI() {
        scope.launch {
            isLoadingTest = true
            testError = null
            testResult = null

            val prompt = "Hola, soy ${userProfile?.displayName ?: "Usuario"}. Escribe un mensaje de bienvenida corto y amigable."
            val result = GeminiManager.generateContent(prompt)

            isLoadingTest = false

            result.onSuccess { response ->
                testResult = response
            }.onFailure { error ->
                testError = "Error: ${error.message}"
            }
        }
    }

    // Show test result dialog
    if (showTestDialog) {
        AlertDialog(
            onDismissRequest = { showTestDialog = false },
            title = { Text("Test Gemini AI") },
            text = {
                Column {
                    if (isLoadingTest) {
                        CircularProgressIndicator()
                        Text("Generando respuesta...")
                    } else if (testError != null) {
                        Text("❌ Error:", color = MaterialTheme.colorScheme.error)
                        Text(testError!!, color = MaterialTheme.colorScheme.error)
                    } else if (testResult != null) {
                        Text("✅ Respuesta generada:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(testResult!!)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTestDialog = false
                        testResult = null
                        testError = null
                    }
                ) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                if (!isLoadingTest && testResult != null) {
                    TextButton(onClick = { testGeminiAI() }) {
                        Text("Probar de nuevo")
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userProfile?.displayName ?: "Usuario",
                userEmail = userProfile?.email ?: "",
                userPhotoUrl = userProfile?.photoUrl,
                currentRoute = "home",
                onMenuItemClick = { route ->
                    scope.launch {
                        drawerState.close()
                        onNavigateToRoute(route)
                    }
                },
                onSignOut = {
                    scope.launch {
                        drawerState.close()
                    }
                    authViewModel.signOut()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Efiteck") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { testGeminiAI(); showTestDialog = true }) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = "Test Gemini AI",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hola ${userProfile?.displayName ?: "Usuario"}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Test Gemini AI Button
                OutlinedButton(
                    onClick = { testGeminiAI(); showTestDialog = true },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(
                        Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Gemini AI")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pantalla principal de la aplicación",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Usa el menú lateral para navegar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Gemini status
                Card(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gemini AI Status",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (GeminiManager.isInitialized()) {
                            Text(
                                text = "✅ Inicializado",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Modelo: gemini-2.5-flash",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "❌ No inicializado",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

