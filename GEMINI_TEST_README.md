# Test Gemini AI - Efiteck App

## ✅ Botón de Test Implementado

Se ha agregado un botón de test para probar Gemini AI en la aplicación.

### 📱 Cómo usar:

1. **Abre la aplicación** en tu dispositivo
2. **Inicia sesión** o regístrate con email/password
3. **En la pantalla principal** verás:

#### 🔧 Botón en TopBar
- Icono de robot 🤖 en la esquina superior derecha
- Presiona para probar Gemini AI instantáneamente

#### 🎯 Botón Principal
- Botón "Test Gemini AI" en el centro de la pantalla
- Genera un mensaje personalizado con tu nombre

#### 📊 Estado de Gemini
- Card que muestra el estado de inicialización
- Modelo: `gemini-2.5-flash`
- ✅ Inicializado / ❌ No inicializado

### 🧪 Funcionalidad del Test:

**Prompt de prueba:**
```
"Hola, soy [Tu Nombre]. Escribe un mensaje de bienvenida corto y amigable."
```

**Ejemplo de respuesta esperada:**
```
"¡Hola [Tu Nombre]! 👋 Bienvenido a la aplicación. Me alegra verte por aquí. ¿En qué puedo ayudarte hoy?"
```

### 🔧 Configuración Firebase:

**Para que funcione necesitas:**

1. **Firebase Console** → Tu proyecto `efiteck`
2. **Authentication** → Email/Password habilitado ✅
3. **Vertex AI** → API habilitada ✅
4. **google-services.json** → Configurado correctamente ✅

### 📋 Archivos modificados:

- `HomeScreen.kt` - Agregado botón de test y dialog
- `GeminiManager.kt` - Funciones de IA implementadas (Modelo: gemini-2.5-flash)
- `build.gradle.kts` - Dependencias de Firebase Vertex AI

### 🚀 Próximos pasos:

- Probar diferentes prompts personalizados
- Integrar Gemini AI en otras pantallas
- Agregar funcionalidades de chat
- Implementar prompts basados en datos del usuario

### 🔍 Debug:

Si el test falla, verifica:
- Conexión a internet
- Configuración de Firebase en consola
- Permisos de API en Firebase Console
- Logs de la aplicación en Android Studio

¡La aplicación está lista para usar! 🎉
