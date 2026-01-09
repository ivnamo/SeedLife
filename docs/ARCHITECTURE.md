# Arquitectura de SeedLife

## 🏛️ Estructura del Proyecto

```
app/src/main/java/com/example/seedlife/
├── MainActivity.kt                    # Actividad principal de la aplicación
├── data/
│   ├── model/
│   │   ├── User.kt                   # Modelo de datos del usuario
│   │   ├── Seed.kt                   # Modelo de datos de una seed (semilla)
│   │   └── Watering.kt               # Modelo de datos de un riego (con WateringMood enum)
│   └── repository/
│       ├── AuthRepository.kt         # Repositorio de autenticación
│       └── SeedRepository.kt         # Repositorio para gestión de seeds y waterings
├── navigation/
│   └── NavGraph.kt                   # Configuración de navegación con Screen sealed class
├── ui/
│   ├── auth/
│   │   ├── AuthScreen.kt             # Pantalla de autenticación
│   │   └── AuthViewModel.kt          # ViewModel para autenticación
│   ├── home/
│   │   └── HomeScreen.kt             # Pantalla principal con lista de seeds
│   ├── seeddetail/
│   │   ├── SeedDetailScreen.kt       # Pantalla de detalle de seed
│   │   └── SeedDetailViewModel.kt    # ViewModel para detalle de seed
│   └── theme/
│       ├── Color.kt                  # Definiciones de colores
│       ├── Theme.kt                  # Configuración del tema Material
│       └── Type.kt                   # Configuración de tipografía
└── util/
    └── ValidationUtils.kt            # Utilidades para validación de formularios
```

## 🎯 Principios Arquitectónicos

### UI Layer
- **Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Patrón**: Composición declarativa con funciones `@Composable`

### Estado Actual
El proyecto implementa:
- Una actividad principal (`MainActivity`) con navegación integrada
- Sistema de temas configurado con soporte dinámico
- Arquitectura MVVM completa con ViewModel y StateFlow
- Repository Pattern para acceso a datos (AuthRepository, SeedRepository)
- Integración completa con Firebase (Authentication y Firestore)
- Navegación con Jetpack Navigation Compose entre pantallas
- Pantallas implementadas: `AuthScreen`, `HomeScreen`, `SeedDetailScreen`
- Gestión de estado reactivo con Kotlin Coroutines y Flow
- Sistema de Seeds con niveles (1-5) basado en riegos
- Sistema de Waterings con estados de ánimo (GOOD, OK, BAD)
- Modo invitado para uso sin autenticación
- Validación de formularios con `ValidationUtils`
- Observación en tiempo real de datos desde Firestore usando `addSnapshotListener`

## 📐 Patrones de Diseño Implementados

### MVVM (Model-View-ViewModel) ✅
- **View**: Composables de Jetpack Compose (`AuthScreen`, `HomeScreen`, `SeedDetailScreen`)
- **ViewModel**: AndroidX ViewModel para manejo de estado (`AuthViewModel`, `SeedDetailViewModel`)
- **Model**: Clases de datos (`User.kt`, `Seed.kt`, `Watering.kt`) y lógica de negocio

### Repository Pattern ✅
- Repositorios para abstraer fuentes de datos (`AuthRepository`, `SeedRepository`)
- Integración con Firebase como fuente de datos remota
- Uso de Kotlin Coroutines para operaciones asíncronas
- Manejo de resultados con `Result<T>` para control de errores
- Observación en tiempo real usando `callbackFlow` y `addSnapshotListener`
- Operaciones CRUD para Seeds y Waterings

### State Management
- **StateFlow** para estado reactivo observable
- Estados sellados (`sealed class AuthState`) para manejo de estados de UI
- ViewModelScope para gestión de coroutines ligadas al ciclo de vida
- Flujos reactivos con Flow para observación de datos en tiempo real
- Estados locales con `remember` y `mutableStateOf` en Compose

### Navigation Pattern ✅
- **Navigation Compose** 2.8.4 para navegación declarativa
- Sealed class `Screen` para definición de rutas type-safe
- NavGraph centralizado con argumentos tipados
- Navegación entre Auth → Home → SeedDetail con parámetros

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

### Flujo General:
```
UI (Compose - AuthScreen/HomeScreen/SeedDetailScreen)
    ↓ (eventos de usuario)
ViewModel (AuthViewModel/SeedDetailViewModel)
    ↓ (llamadas a métodos)
Repository (AuthRepository/SeedRepository)
    ↓ (operaciones asíncronas)
Firebase (Authentication + Firestore)
    ↓ (resultados / snapshots en tiempo real)
StateFlow / Flow (AuthState, Seed, Waterings)
    ↓ (observación reactiva)
UI (actualización automática)
```

### Ejemplo de Flujo de Autenticación:
1. Usuario ingresa credenciales en `AuthScreen`
2. `AuthViewModel` valida datos con `ValidationUtils`
3. `AuthViewModel` llama a `AuthRepository.login()`
4. `AuthRepository` realiza la autenticación con Firebase Authentication
5. Si es exitoso, obtiene los datos del usuario desde Firestore
6. El resultado actualiza el `StateFlow<AuthState>` en el ViewModel
7. La UI reacciona automáticamente y navega a `HomeScreen`

### Ejemplo de Flujo de Seed Detail:
1. Usuario hace clic en una seed en `HomeScreen`
2. Navegación a `SeedDetailScreen` con `seedId` como parámetro
3. `SeedDetailViewModel` observa la seed y waterings desde Firestore usando `observeSeed()` y `observeWaterings()`
4. `SeedRepository` crea listeners en tiempo real con `addSnapshotListener`
5. Cambios en Firestore se propagan automáticamente a través de `callbackFlow`
6. La UI se actualiza reactivamente con los nuevos datos
7. Usuario añade un riego → `SeedRepository.addWatering()` actualiza Firestore y calcula nuevo level
8. Los listeners detectan cambios y actualizan la UI automáticamente

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

### Navigation
- `androidx.navigation:navigation-compose:2.8.4`

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
- `io.mockk:mockk:1.13.8` (Mocking para tests unitarios)
- `app.cash.turbine:turbine:1.0.0` (Testing de Flows)
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` (Testing de coroutines)

## 🚀 Próximos Pasos Arquitectónicos

### Fase 1: Estructura Base ✅
- [x] Implementar ViewModels para manejo de estado
- [x] Crear modelos de datos (User, Seed, Watering)
- [x] Establecer navegación con Compose Navigation

### Fase 2: Capa de Datos ✅
- [x] Implementar repositorios (AuthRepository, SeedRepository)
- [x] Integrar API remota (Firebase)
- [x] Observación en tiempo real de datos
- [ ] Integrar base de datos local (Room) - para cache offline
- [ ] Implementar sincronización local-remota

### Fase 3: Funcionalidades Avanzadas
- [ ] Implementar inyección de dependencias (Hilt/Koin)
- [x] Añadir navegación entre pantallas ✅
- [x] Añadir validación de formularios ✅
- [ ] Implementar manejo de errores global
- [ ] Implementar logging y analytics con Firebase Analytics
- [ ] Añadir creación/edición de seeds desde la app
- [ ] Implementar búsqueda y filtrado de seeds
- [ ] Añadir estadísticas y gráficos de riegos

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
- **Firebase Authentication**: Autenticación con email/contraseña y modo invitado
- **Cloud Firestore**: Base de datos NoSQL para almacenar datos estructurados

### Estructura de Firestore
```
/users/{uid}
  ├── name: String
  ├── email: String
  └── seeds/{seedId}
      ├── title: String
      ├── description: String
      ├── level: Int (1-5)
      ├── lastWateredAt: Date
      ├── createdAt: Date
      └── waterings/{wateringId}
          ├── id: String
          ├── mood: String (GOOD/OK/BAD)
          ├── note: String?
          ├── date: Date
          └── createdAt: Date
```

### Sistema de Levels
- Los seeds tienen un nivel de 1 a 5
- El nivel se calcula automáticamente: `level = minOf(5, 1 + (totalWaterings / 3))`
- Se actualiza cada vez que se añade un nuevo riego

### Archivo de Configuración
- `google-services.json` en la raíz del proyecto (incluido en build.gradle.kts)

---

**Nota**: Este documento se actualizará a medida que la arquitectura evolucione.
