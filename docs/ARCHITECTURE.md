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
2. Navegación a `SeedDetailScreen` con `seedId` como parámetro mediante `NavController.navigate()`
3. `SeedDetailViewModel` inicializa con `seedId` y comienza a observar datos usando `observeSeed()` y `observeWaterings()`
4. `SeedRepository` crea listeners en tiempo real con `addSnapshotListener` encapsulados en `callbackFlow`
5. Cambios en Firestore se propagan automáticamente a través de los flows reactivos
6. La UI se actualiza reactivamente usando `collectAsStateWithLifecycle()` en los Composables
7. Usuario añade un riego → `SeedDetailViewModel.addWatering()` → `SeedRepository.addWatering()` 
8. `SeedRepository.addWatering()`:
   - Crea documento de riego en subcolección `/waterings/{wateringId}`
   - Calcula nuevo nivel: `minOf(5, 1 + (totalWaterings / 3))`
   - Actualiza documento del seed con nuevo `level` y `lastWateredAt`
9. Los listeners detectan cambios y actualizan los StateFlows automáticamente
10. La UI se recompone con los nuevos datos sin intervención manual

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
- **Unit Testing**:
  - `junit:junit:4.13.2` - Framework básico de testing
  - `io.mockk:mockk:1.13.8` - Mocking framework para Kotlin (mockear Firebase, ViewModels, etc.)
  - `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` - Testing de coroutines con `TestDispatcher`
  - `app.cash.turbine:turbine:1.0.0` - Testing de Flows con API declarativa
- **UI Testing**:
  - `androidx.test.ext:junit:1.1.5` - Extensiones de JUnit para Android
  - `androidx.test.espresso:espresso-core:3.5.1` - Testing de UI tradicional
  - `androidx.compose.ui:ui-test-junit4` - Testing específico para Compose
  - `androidx.compose.ui:ui-test-manifest` - Manifest para tests de Compose
- **Estrategia de Testing**:
  - Tests unitarios para ViewModels, Repositories y utilidades
  - Tests de UI para pantallas principales (AuthScreen, HomeScreen, SeedDetailScreen)
  - Mocking de Firebase para aislar la lógica de negocio
  - Testing de Flows con Turbine para validar estados reactivos

## 🚀 Próximos Pasos Arquitectónicos

### Fase 1: Estructura Base ✅
- [x] Implementar ViewModels para manejo de estado
- [x] Crear modelos de datos (User, Seed, Watering)
- [x] Establecer navegación con Compose Navigation

### Fase 2: Capa de Datos ✅
- [x] Implementar repositorios (AuthRepository, SeedRepository)
- [x] Integrar API remota (Firebase Authentication + Firestore)
- [x] Observación en tiempo real de datos con snapshot listeners
- [x] Operaciones CRUD para Seeds y Waterings
- [ ] Integrar base de datos local (Room) - para cache offline
- [ ] Implementar sincronización local-remota

### Fase 3: Funcionalidades Avanzadas
- [ ] Implementar inyección de dependencias (Hilt/Koin)
- [x] Añadir navegación entre pantallas ✅
- [x] Añadir validación de formularios ✅
- [x] Implementar sistema de niveles automático para seeds ✅
- [x] Implementar sistema de estados de ánimo para riegos ✅
- [x] Implementar modo invitado completo ✅
- [ ] Implementar manejo de errores global con UI de errores
- [ ] Implementar logging y analytics con Firebase Analytics
- [ ] Añadir creación/edición de seeds desde la app
- [ ] Implementar búsqueda y filtrado de seeds
- [ ] Añadir estadísticas y gráficos de riegos
- [ ] Implementar notificaciones para recordatorios de riego
- [ ] Añadir exportación de datos (CSV/JSON)

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

### Autenticación y Autorización
- Las contraseñas se manejan exclusivamente a través de Firebase Authentication (nunca almacenadas localmente ni en texto plano)
- Autenticación por email/contraseña con validación del lado del cliente
- Modo invitado implementado para acceso sin autenticación (datos locales temporales)
- Verificación de sesión automática al iniciar la app

### Datos y Privacidad
- Cada usuario solo puede acceder a sus propios datos (seeds y waterings) mediante `uid` en Firestore
- Implementar reglas de seguridad en Firestore para proteger datos de usuarios:
  - Validar que `request.auth.uid == resource.data.userId` para operaciones de lectura/escritura
  - Restringir acceso a subcolecciones de otros usuarios
- No hardcodear credenciales o API keys en el código
- El archivo `google-services.json` contiene configuraciones sensibles (verificar antes de commitear en repos públicos)

### Validación y Sanitización
- Validar y sanitizar todas las entradas del usuario antes de enviarlas a Firebase
- `ValidationUtils` valida formato de email, longitud de contraseña, y nombres
- Validación del lado del cliente antes de llamadas a Firebase

### Build y Despliegue
- Usar `local.properties` o BuildConfig para configuraciones locales sensibles
- Considerar ProGuard/R8 para ofuscar código en builds de release
- No incluir `google-services.json` en repositorios públicos sin verificar contenido
- Revisar reglas de seguridad de Firestore antes de cada despliegue

## 🔥 Configuración de Firebase

### Proyecto Firebase
- **Project ID**: `seedlife-3a4d8`
- **Project Number**: `795193838714`
- **Storage Bucket**: `seedlife-3a4d8.firebasestorage.app`

### Servicios Configurados
- **Firebase Authentication**: 
  - Autenticación con email/contraseña
  - Modo invitado (Anonymous Authentication) para usuarios no registrados
  - Verificación de sesión persistente
- **Cloud Firestore**: 
  - Base de datos NoSQL para almacenar datos estructurados
  - Estructura jerárquica por usuario (users/{uid}/seeds/{seedId}/waterings/{wateringId})
  - Observación en tiempo real con snapshot listeners

### Modo Invitado
- Permite usar la aplicación sin registro previo
- Firebase crea una cuenta anónima temporal con `uid` único
- Los datos del invitado se almacenan en Firestore bajo su `uid` anónimo
- El usuario puede registrarse más tarde para convertir la cuenta anónima en permanente
- Implementado en `AuthViewModel` con método `signInAsGuest()`
- Todas las pantallas (HomeScreen, SeedDetailScreen) funcionan con modo invitado

### Estructura de Firestore

#### Colección de Usuarios
```
/users/{uid}
  ├── name: String (nombre del usuario)
  ├── email: String (email del usuario, sincronizado con Firebase Auth)
```

#### Colección de Seeds (subcolección de users)
```
/users/{uid}/seeds/{seedId}
  ├── title: String (título de la seed)
  ├── description: String? (descripción opcional)
  ├── level: Int (nivel actual 1-5, calculado automáticamente)
  ├── lastWateredAt: Timestamp (última vez que se regó)
  ├── createdAt: Timestamp (fecha de creación, @ServerTimestamp)
```

#### Colección de Waterings (subcolección de seeds)
```
/users/{uid}/seeds/{seedId}/waterings/{wateringId}
  ├── id: String (ID único del riego)
  ├── mood: String (WateringMood: "GOOD", "OK", o "BAD")
  ├── note: String? (nota opcional del usuario)
  ├── date: Timestamp (fecha del riego)
  └── createdAt: Timestamp (fecha de creación, @ServerTimestamp)
```

### Modelos de Datos

#### User.kt
- Representa un usuario de la aplicación
- Sincronizado con Firebase Authentication
- Contiene información básica: name, email

#### Seed.kt
- Representa una "semilla" o elemento a cuidar
- Tiene un sistema de niveles (1-5) que crece con los riegos
- Campos principales: id, title, description, level, lastWateredAt, createdAt

#### Watering.kt
- Representa un evento de riego/cuidado
- Incluye un estado de ánimo (WateringMood) para expresar cómo se sintió el usuario
- Campos: id, mood (enum), note (opcional), date, createdAt

#### WateringMood (Enum)
- `GOOD`: Estado positivo, se sintió bien
- `OK`: Estado neutral, normal
- `BAD`: Estado negativo, se sintió mal

### Sistema de Levels
- Los seeds tienen un nivel de 1 a 5 que representa su "madurez" o crecimiento
- El nivel se calcula automáticamente: `level = minOf(5, 1 + (totalWaterings / 3))`
  - Nivel 1: 0-2 riegos
  - Nivel 2: 3-5 riegos
  - Nivel 3: 6-8 riegos
  - Nivel 4: 9-11 riegos
  - Nivel 5: 12+ riegos
- Se actualiza automáticamente cada vez que se añade un nuevo riego a través de `SeedRepository.addWatering()`
- El cálculo se realiza en el servidor (Firestore) para mantener consistencia
- `lastWateredAt` se actualiza con cada riego para tracking de actividad

### Archivo de Configuración
- `google-services.json` en la raíz del proyecto (incluido en build.gradle.kts)

---

**Nota**: Este documento se actualizará a medida que la arquitectura evolucione.
