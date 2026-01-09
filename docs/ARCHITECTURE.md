# Arquitectura de SeedLife

## 🏛️ Estructura del Proyecto

```
app/src/main/java/com/example/seedlife/
├── MainActivity.kt              # Actividad principal de la aplicación
├── data/
│   ├── model/
│   │   └── User.kt             # Modelo de datos del usuario
│   └── repository/
│       └── AuthRepository.kt   # Repositorio de autenticación
└── ui/
    ├── auth/
    │   ├── AuthScreen.kt       # Pantalla de autenticación
    │   └── AuthViewModel.kt    # ViewModel para autenticación
    ├── home/
    │   └── HomeScreen.kt       # Pantalla principal
    └── theme/
        ├── Color.kt            # Definiciones de colores
        ├── Theme.kt            # Configuración del tema Material
        └── Type.kt             # Configuración de tipografía
```

## 🎯 Principios Arquitectónicos

### UI Layer
- **Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Patrón**: Composición declarativa con funciones `@Composable`

### Estado Actual
El proyecto implementa:
- Una actividad principal (`MainActivity`)
- Sistema de temas configurado con soporte dinámico
- Arquitectura MVVM con ViewModel y StateFlow
- Repository Pattern para acceso a datos
- Integración con Firebase (Authentication y Firestore)
- Pantallas de autenticación (`AuthScreen`) y home (`HomeScreen`)
- Gestión de estado reactivo con Kotlin Coroutines y Flow

## 📐 Patrones de Diseño Implementados

### MVVM (Model-View-ViewModel) ✅
- **View**: Composables de Jetpack Compose (`AuthScreen`, `HomeScreen`)
- **ViewModel**: AndroidX ViewModel para manejo de estado (`AuthViewModel`)
- **Model**: Clases de datos (`User.kt`) y lógica de negocio

### Repository Pattern ✅
- Repositorios para abstraer fuentes de datos (`AuthRepository`)
- Integración con Firebase como fuente de datos remota
- Uso de Kotlin Coroutines para operaciones asíncronas
- Manejo de resultados con `Result<T>` para control de errores

### State Management
- **StateFlow** para estado reactivo observable
- Estados sellados (`sealed class AuthState`) para manejo de estados de UI
- ViewModelScope para gestión de coroutines ligadas al ciclo de vida

## 🎨 Sistema de Temas

### Configuración Actual
- Soporte para tema dinámico en Android 12+ (API 31+)
- Fallback a esquemas de color personalizados
- Modo oscuro/claro basado en configuración del sistema

### Colores
- **Light Theme**: Purple40, PurpleGrey40, Pink40
- **Dark Theme**: Purple80, PurpleGrey80, Pink80

### Personalización
Los colores están definidos en `ui/theme/Color.kt` y pueden ajustarse según las necesidades del proyecto.

## 🔄 Flujo de Datos

```
UI (Compose - AuthScreen/HomeScreen)
    ↓ (eventos de usuario)
ViewModel (AuthViewModel)
    ↓ (llamadas a métodos)
Repository (AuthRepository)
    ↓ (operaciones asíncronas)
Firebase (Authentication + Firestore)
    ↓ (resultados)
StateFlow (AuthState)
    ↓ (observación reactiva)
UI (actualización automática)
```

### Ejemplo de Flujo de Autenticación:
1. Usuario ingresa credenciales en `AuthScreen`
2. `AuthViewModel` recibe el evento y llama a `AuthRepository.login()`
3. `AuthRepository` realiza la autenticación con Firebase Authentication
4. Si es exitoso, obtiene los datos del usuario desde Firestore
5. El resultado actualiza el `StateFlow<AuthState>` en el ViewModel
6. La UI reacciona automáticamente a los cambios de estado

## 📚 Dependencias Actuales

### UI
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `androidx.compose.ui:ui-tooling-preview`

### Core & Arquitectura
- `androidx.core:core-ktx:1.10.1`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.6.1`
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1`
- `androidx.activity:activity-compose:1.8.0`

### Firebase
- `com.google.firebase:firebase-bom:33.7.0` (BOM para gestión de versiones)
- `com.google.firebase:firebase-auth` (Autenticación)
- `com.google.firebase:firebase-firestore` (Base de datos)
- `com.google.gms:google-services:4.4.2` (Plugin)

### Testing
- `junit:junit:4.13.2`
- `androidx.test.ext:junit:1.1.5`
- `androidx.test.espresso:espresso-core:3.5.1`
- `androidx.compose.ui:ui-test-junit4`

## 🚀 Próximos Pasos Arquitectónicos

### Fase 1: Estructura Base ✅
- [x] Implementar ViewModels para manejo de estado
- [x] Crear modelos de datos
- [ ] Establecer navegación con Compose Navigation

### Fase 2: Capa de Datos ✅ (Parcialmente)
- [x] Implementar repositorios (AuthRepository)
- [x] Integrar API remota (Firebase)
- [ ] Integrar base de datos local (Room) - para cache offline
- [ ] Implementar sincronización local-remota

### Fase 3: Funcionalidades Avanzadas
- [ ] Implementar inyección de dependencias (Hilt/Koin)
- [ ] Añadir navegación entre pantallas
- [ ] Implementar manejo de errores global
- [ ] Añadir validación de formularios
- [ ] Implementar logging y analytics con Firebase Analytics

## 📝 Convenciones de Código

### Kotlin
- Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Usar `camelCase` para variables y funciones
- Usar `PascalCase` para clases y tipos

### Compose
- Componentes `@Composable` con nombre en PascalCase
- Modifier como primer parámetro opcional
- Separar lógica de presentación en ViewModels

### Nomenclatura de Archivos
- ViewModels: `[Feature]ViewModel.kt`
- Composables: `[Component]Screen.kt` o `[Component]Composable.kt`
- Models: `[Entity].kt` o `[Entity]Model.kt`
- Repositories: `[Entity]Repository.kt`

## 🔒 Consideraciones de Seguridad

- Manejar datos sensibles de forma segura
- No hardcodear credenciales o API keys
- El archivo `google-services.json` contiene configuraciones sensibles (no committear en repos públicos sin verificar)
- Usar `local.properties` o BuildConfig para configuraciones locales
- Implementar reglas de seguridad en Firestore para proteger datos de usuarios
- Las contraseñas se manejan a través de Firebase Authentication (nunca almacenadas localmente)
- Considerar ProGuard/R8 para ofuscar código en release
- Validar y sanitizar todas las entradas del usuario antes de enviarlas a Firebase

## 🔥 Configuración de Firebase

### Proyecto Firebase
- **Project ID**: `seedlife-3a4d8`
- **Project Number**: `795193838714`
- **Storage Bucket**: `seedlife-3a4d8.firebasestorage.app`

### Servicios Configurados
- **Firebase Authentication**: Autenticación con email/contraseña
- **Cloud Firestore**: Base de datos NoSQL para almacenar datos de usuarios
- **Collections**: `/users/{uid}` - Documentos de usuarios

### Archivo de Configuración
- `google-services.json` en la raíz del proyecto (incluido en build.gradle.kts)

---

**Nota**: Este documento se actualizará a medida que la arquitectura evolucione.
