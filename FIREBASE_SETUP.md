# Configuración Firebase - Efiteck

## Componentes Implementados

### ✅ 1. Dependencias Firebase
- Firebase BoM 34.4.0
- Firebase Authentication (Email/Password)
- Firebase Storage (para fotos de perfil)
- Firebase AI (Gemini 2.5 Flash)
- Navigation Compose
- Coil (carga de imágenes)

### ✅ 2. Autenticación
- **LoginScreen**: Pantalla de inicio de sesión con email/password
- **RegisterScreen**: Pantalla de registro con validación de campos
- **AuthViewModel**: Gestión de estado de autenticación
- Validación de inputs (email válido, contraseña mínimo 6 caracteres)
- Manejo de errores de Firebase

### ✅ 3. Navigation Drawer
- **HomeScreen**: Pantalla principal con menú lateral desplegable
- **DrawerContent**: Menú con:
  - Header: Foto de perfil circular + nombre + email
  - Secciones:
    - Perfil
    - Notificaciones
    - Configuraciones
    - Ayuda
    - Acerca de
    - Cerrar sesión

### ✅ 4. Gestión de Perfil
- **ProfileScreen**: Visualización y edición de perfil
- Subida de foto de perfil a Firebase Storage
- Edición de nombre de usuario
- Avatar con iniciales si no hay foto
- **UserRepository**: Gestión de operaciones de usuario

### ✅ 5. Firebase AI (Gemini)
- **GeminiManager**: Singleton para gestionar el modelo Gemini
- Inicialización: `Firebase.vertexAI.generativeModel("gemini-2.5-flash")`
- Método `generateContent()` para generar texto
- Botón de test implementado en HomeScreen
- Listo para integrar en cualquier pantalla

### ✅ 6. Sistema Predictivo (PyTorch Mobile)
- **PredictiveModel**: Motor predictivo con datos de entrenamiento
- **9 ejemplos de test** incluidos (PKG003-PKG611)
- **PyTorch Mobile** integrado para predicciones avanzadas
- **PredictiveViewModel**: Gestión de estado y lógica de predicciones
- **Pantalla dedicada**: Interfaz completa para análisis predictivo

### ✅ 7. Botón de Test Gemini AI
- **HomeScreen**: Botón para probar Gemini AI
- **Dialog interactivo**: Muestra respuestas en tiempo real
- **Estado visual**: Card con estado de inicialización
- **TopBar**: Icono de acceso rápido 🤖

### ✅ 8. Permisos y Configuración
- AndroidManifest actualizado con permisos necesarios:
  - INTERNET
  - ACCESS_NETWORK_STATE
  - READ_MEDIA_IMAGES (Android 13+)
  - READ_EXTERNAL_STORAGE (Android 12 y anteriores)

## Configuración Requerida

### 1. Google Services JSON
El archivo `google-services.json` ya está en `app/`. Asegúrate de que contenga la configuración correcta de tu proyecto Firebase.

### 2. Firebase Console
En Firebase Console (https://console.firebase.google.com/):

1. **Authentication**:
   - Habilita "Email/Password" en Authentication > Sign-in method

2. **Storage**:
   - Habilita Storage en la consola
   - Configura reglas de seguridad (ejemplo):
   ```
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /profile_images/{userId}/{allPaths=**} {
         allow read: if true;
         allow write: if request.auth != null && request.auth.uid == userId;
       }
     }
   }
   ```

3. **Firebase AI (Gemini)**:
   - Asegúrate de tener habilitado Firebase AI en tu proyecto
   - Verifica que tu proyecto tenga acceso a la API de Gemini

## Estructura del Proyecto

```
app/src/main/java/com/efiteck/
├── ai/
│   └── GeminiManager.kt          # Gestión del modelo Gemini
├── components/
│   └── DrawerContent.kt          # Contenido del menú lateral
├── model/
│   ├── UserProfile.kt            # Modelo de datos de usuario
│   └── PredictiveModel.kt        # Motor predictivo PyTorch
├── navigation/
│   └── NavGraph.kt               # Configuración de navegación
├── repository/
│   ├── UserRepository.kt         # Operaciones de usuario
│   └── FirestoreRepository.kt    # Operaciones de Firestore
├── screens/
│   ├── HomeScreen.kt             # Pantalla principal
│   ├── LoginScreen.kt            # Pantalla de login
│   ├── RegisterScreen.kt         # Pantalla de registro
│   ├── ProfileScreen.kt          # Pantalla de perfil
│   ├── PredictiveScreen.kt       # Sistema predictivo UI
│   └── PlaceholderScreen.kt      # Pantalla genérica
├── viewmodel/
│   ├── AuthViewModel.kt          # ViewModel de autenticación
│   └── PredictiveViewModel.kt    # ViewModel del sistema predictivo
└── MainActivity.kt               # Activity principal
```

## Uso

### Iniciar Sesión
1. Abre la app
2. Ingresa email y contraseña
3. Presiona "Iniciar Sesión"
4. Si no tienes cuenta, presiona "¿No tienes cuenta? Regístrate"

### Registro
1. Completa nombre, email y contraseña
2. Confirma la contraseña
3. Presiona "Registrarse"

### Menú Lateral
1. En la pantalla principal, presiona el ícono del menú (☰)
2. Navega a las diferentes secciones
3. Para cerrar sesión, presiona "Cerrar sesión"

### Perfil
1. Abre el menú lateral
2. Presiona "Perfil"
3. Toca el ícono de cámara para cambiar la foto
4. Toca el ícono de editar junto al nombre para cambiarlo

### Usar Gemini AI
Para integrar Gemini en cualquier pantalla:

```kotlin
import com.efiteck.ai.GeminiManager
import kotlinx.coroutines.launch

// En un Composable con coroutineScope
scope.launch {
    val result = GeminiManager.generateContent("Tu prompt aquí")
    result.onSuccess { text ->
        // Usa el texto generado
    }.onFailure { error ->
        // Maneja el error
    }
}
```

## Sistema Predictivo

El sistema predictivo analiza datos de empaquetado usando PyTorch Mobile:

**Datos de entrenamiento incluidos:**
- 9 ejemplos de test (PKG003-PKG611)
- Análisis de empleados, modelos, rutas y resultados
- Predicciones de calidad y recomendaciones

**Para usar el sistema predictivo:**
1. Abre el menú lateral (☰)
2. Selecciona "Sistema Predictivo" (📊)
3. Presiona "Ejecutar Tests de Predicción"
4. Revisa los resultados y precisión del modelo

## Próximos Pasos

- Implementar las pantallas de Notificaciones, Configuraciones, Ayuda y Acerca de
- Integrar Gemini AI en la pantalla principal o en una pantalla de chat
- Agregar funcionalidad de cambio de contraseña
- Implementar recuperación de contraseña
- Agregar persistencia de preferencias del usuario
- Entrenar modelo PyTorch real con más datos
- Implementar predicciones en tiempo real

## Notas

- La app verifica automáticamente el estado de autenticación al iniciar
- Las imágenes se suben a Firebase Storage en `profile_images/{userId}.jpg`
- El modelo Gemini se inicializa de forma lazy (solo cuando se usa)
- El sistema predictivo incluye 9 ejemplos de entrenamiento reales
- PyTorch Mobile está integrado para predicciones avanzadas
- Todas las pantallas de menú excepto Perfil y Sistema Predictivo son placeholders

