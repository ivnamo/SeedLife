# Documentación Técnica y Funcional Completa - SeedLife

## 1. Visión General de la Aplicación

### 1.1 Nombre de la Aplicación
**SeedLife** (también referida como "LifeSeeds" en la interfaz de usuario)

### 1.2 Temática
SeedLife es una aplicación móvil Android de gestión de "semillas" (seeds) que permite a los usuarios llevar un registro de elementos personales que requieren cuidado y atención periódica. La aplicación utiliza una metáfora de jardinería donde cada "seed" representa un elemento que el usuario desea cultivar o cuidar, y los "riegos" (waterings) representan acciones o momentos de atención dedicados a ese elemento.

### 1.3 Problema que Resuelve
La aplicación resuelve la necesidad de:
- **Seguimiento personalizado**: Permite a los usuarios crear y gestionar múltiples "semillas" que representan diferentes aspectos de su vida (hábitos, proyectos, relaciones, objetivos, etc.)
- **Registro de progreso**: Sistema de niveles (1-5) que crece automáticamente según la frecuencia de "riegos", proporcionando feedback visual del progreso
- **Registro emocional**: Cada riego incluye un estado de ánimo (GOOD, OK, BAD) que permite al usuario expresar cómo se sintió durante esa interacción
- **Persistencia y sincronización**: Los datos se almacenan en la nube (Firebase) permitiendo acceso desde múltiples dispositivos y funcionamiento offline
- **Accesibilidad**: Modo invitado que permite probar la aplicación sin registro previo

### 1.4 Público Objetivo
- Usuarios que buscan herramientas de autotracking y desarrollo personal
- Personas interesadas en gamificación de hábitos
- Usuarios que necesitan un sistema simple pero efectivo para registrar progreso en múltiples áreas de su vida
- Público general que valora aplicaciones con diseño moderno y experiencia de usuario fluida

### 1.5 Funcionalidades Principales
1. **Autenticación de Usuarios**
   - Registro con email y contraseña
   - Inicio de sesión
   - Modo invitado (sin autenticación)
   - Gestión de sesión persistente

2. **Gestión de Seeds (Semillas)**
   - Creación de nuevas seeds con título y descripción
   - Edición de seeds existentes
   - Eliminación de seeds (con confirmación)
   - Visualización de lista de todas las seeds del usuario
   - Sistema de niveles automático (1-5) basado en cantidad de riegos

3. **Sistema de Riegos (Waterings)**
   - Registro de riegos con estado de ánimo (GOOD, OK, BAD)
   - Notas opcionales en cada riego (máximo 250 caracteres)
   - Historial completo de riegos por seed
   - Actualización automática del nivel de la seed tras cada riego

4. **Búsqueda y Filtrado**
   - Búsqueda por texto (título y descripción)
   - Filtrado por rango de niveles (mínimo y máximo)
   - 8 opciones de ordenamiento:
     - Fecha de creación (ascendente/descendente)
     - Nivel (ascendente/descendente)
     - Título (A-Z / Z-A)
     - Última fecha de riego (ascendente/descendente)

5. **Estadísticas**
   - Total de seeds del usuario
   - Total de waterings de todas las seeds
   - Visualización en tiempo real

6. **Perfil de Usuario**
   - Visualización de información del usuario (nombre, email)
   - Cierre de sesión
   - Gestión de modo oscuro (UI preparada, funcionalidad pendiente)

7. **Recursos Visuales**
   - Imágenes dinámicas según nivel de seed:
     - Nivel 1: `seed_bag.png` (bolsa de semillas)
     - Nivel 2: `planta.png` (planta joven)
     - Nivel 3+: `plantas.png` (plantas maduras)
   - Ilustraciones para estados vacíos
   - Splash screen con animación

8. **Funcionalidades Adicionales**
   - Subida de fotos para seeds (Firebase Storage)
   - Persistencia offline (Firestore)
   - Sincronización automática cuando se restaura la conexión
   - Validación de formularios en tiempo real
   - Manejo de errores con mensajes amigables en español

---

## 2. Arquitectura de la Aplicación

### 2.1 Patrón Arquitectónico

**MVVM (Model-View-ViewModel)** - Justificación desde el código:

La aplicación implementa el patrón MVVM de forma completa y consistente, como se evidencia en:

1. **Separación de responsabilidades**:
   - **View**: Composables de Jetpack Compose (`AuthScreen`, `HomeScreen`, `SeedDetailScreen`, etc.)
   - **ViewModel**: Clases que extienden `androidx.lifecycle.ViewModel` (`AuthViewModel`, `HomeViewModel`, `SeedDetailViewModel`, etc.)
   - **Model**: Data classes (`Seed`, `Watering`, `User`, `UserProfile`) y lógica de negocio en repositorios

2. **Observación reactiva**: Los ViewModels exponen `StateFlow` que son observados por las Views usando `collectAsState()`:
   ```kotlin
   val authState by viewModel.authState.collectAsState()
   ```

3. **Ciclo de vida**: Los ViewModels están ligados al ciclo de vida de la Activity mediante `viewModel()` de Compose, garantizando que sobrevivan a cambios de configuración.

4. **Repository Pattern**: Los ViewModels no acceden directamente a Firebase, sino a través de repositorios que abstraen la fuente de datos.

### 2.2 Capas de la Arquitectura

#### 2.2.1 Capa de UI (Presentation Layer)
**Ubicación**: `app/src/main/java/com/example/seedlife/ui/`

**Componentes**:
- **Screens**: Composables que representan pantallas completas
  - `AuthScreen.kt` - Autenticación
  - `HomeScreen.kt` - Lista principal de seeds
  - `SeedDetailScreen.kt` - Detalle de una seed
  - `SeedEditorScreen.kt` - Crear/editar seed
  - `StatsScreen.kt` - Estadísticas
  - `ProfileScreen.kt` - Perfil de usuario
  - `SplashScreen.kt` - Pantalla de inicio

- **ViewModels**: Gestión de estado y lógica de presentación
  - `AuthViewModel.kt`
  - `HomeViewModel.kt`
  - `SeedDetailViewModel.kt`
  - `SeedEditorViewModel.kt` (incluido en `SeedEditorScreen.kt`)
  - `StatsViewModel.kt` (incluido en `StatsScreen.kt`)
  - `SessionViewModel.kt` - Gestión global de sesión

- **Common**: Componentes y utilidades compartidas
  - `UiState.kt` - Sealed class para estados de UI (Loading, Success, Error)

- **Theme**: Sistema de diseño
  - `Color.kt` - Paleta de colores
  - `Theme.kt` - Configuración de Material Theme
  - `Type.kt` - Tipografía

#### 2.2.2 Capa de Dominio (Domain Layer)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/model/`

**Modelos de Datos**:
- `Seed.kt` - Modelo de una semilla
- `Watering.kt` - Modelo de un riego
- `User.kt` - Modelo de usuario
- `UserProfile.kt` - Modelo de perfil de usuario

**Enums**:
- `WateringMood` - Estados de ánimo (GOOD, OK, BAD)

#### 2.2.3 Capa de Datos (Data Layer)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/`

**Repositorios** (`repository/`):
- `AuthRepository.kt` - Autenticación y operaciones de usuario
- `SeedRepository.kt` - Operaciones CRUD de seeds y waterings
- `UserRepository.kt` - Gestión de perfiles de usuario
- `StatsRepository.kt` - Cálculo y observación de estadísticas

**Configuración**:
- `FirestoreConfig.kt` - Configuración de persistencia offline

**Utilidades** (`util/`):
- `ValidationUtils.kt` - Validación de formularios
- `FirebaseErrorMapper.kt` - Mapeo de errores de Firebase a mensajes amigables

#### 2.2.4 Capa de Navegación
**Ubicación**: `app/src/main/java/com/example/seedlife/navigation/`

- `NavGraph.kt` - Configuración completa de navegación con:
  - `AuthNavGraph` - Navegación de autenticación
  - `AppNavGraph` - Navegación principal con Bottom Navigation
  - Sealed classes `AuthScreen` y `AppScreen` para rutas type-safe

### 2.3 Flujo de Datos

El flujo de datos sigue un patrón unidireccional reactivo:

```
Usuario interactúa con UI (Compose)
    ↓
Evento de usuario (onClick, onValueChange, etc.)
    ↓
ViewModel recibe el evento y procesa la lógica
    ↓
ViewModel llama a Repository
    ↓
Repository realiza operación asíncrona (Firebase)
    ↓
Resultado (Result<T> o Flow<T>)
    ↓
ViewModel actualiza StateFlow
    ↓
UI reacciona automáticamente (collectAsState)
    ↓
Recomposición de Compose
```

**Ejemplo concreto - Añadir un riego**:

1. Usuario hace clic en FAB en `SeedDetailScreen`
2. Se muestra `AddWateringDialog`
3. Usuario selecciona mood y opcionalmente añade nota
4. `SeedDetailViewModel.addWatering(mood, note)` es llamado
5. `SeedRepository.addWatering(uid, seedId, mood, note)` es invocado
6. Repository:
   - Crea documento de watering en subcolección `/users/{uid}/seeds/{seedId}/waterings/{wateringId}`
   - Cuenta total de waterings
   - Calcula nuevo nivel: `minOf(5, 1 + (totalWaterings / 3))`
   - Actualiza documento de seed con nuevo `level` y `lastWateredAt`
7. Los listeners en tiempo real detectan cambios
8. `observeSeed()` y `observeWaterings()` emiten nuevos valores
9. Los StateFlows en ViewModel se actualizan
10. La UI se recompone automáticamente mostrando el nuevo riego y nivel actualizado

### 2.4 Gestión del Estado

#### 2.4.1 StateFlow para Estado Reactivo
Todos los ViewModels utilizan `StateFlow` para exponer estado observable:

```kotlin
private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
val authState: StateFlow<AuthState> = _authState.asStateFlow()
```

#### 2.4.2 Sealed Classes para Estados
Se utilizan sealed classes para representar estados finitos:

- `AuthState`: `Idle`, `Loading`, `Success`, `Error`
- `UiState<T>`: `Loading`, `Success<T>`, `Error`

#### 2.4.3 Estado Local en Compose
Para estado que no necesita persistir entre recomposiciones, se usa `remember` y `mutableStateOf`:

```kotlin
var showDialog by remember { mutableStateOf(false) }
```

#### 2.4.4 Gestión de Sesión Global
`SessionViewModel` gestiona el estado global de la sesión separado de la autenticación:
- Sincroniza con `AuthViewModel` cuando hay cambios de autenticación
- Observa el perfil del usuario en tiempo real
- Expone `SessionState` con `uid`, `isGuest`, y `userProfile`

### 2.5 Navegación entre Pantallas

#### 2.5.1 Jetpack Navigation Compose
La aplicación utiliza **Navigation Compose 2.8.4** para navegación declarativa.

#### 2.5.2 Estructura de Navegación

**Dos NavGraphs separados**:

1. **AuthNavGraph**: Para flujo de autenticación
   - Ruta: `auth/login`
   - Pantalla: `AuthScreen`

2. **AppNavGraph**: Para aplicación principal
   - Rutas principales (con Bottom Navigation):
     - `app/garden` - HomeScreen (Jardín)
     - `app/stats` - StatsScreen (Estadísticas)
     - `app/profile` - ProfileScreen (Perfil)
   - Rutas secundarias (sin Bottom Navigation):
     - `app/seed_detail/{seedId}` - SeedDetailScreen
     - `app/seed_editor/{seedId}` - SeedEditorScreen

#### 2.5.3 Bottom Navigation Bar
Se muestra solo en las tres pantallas principales (Garden, Stats, Profile) y se oculta en pantallas de detalle/edición.

#### 2.5.4 Navegación Type-Safe
Se utilizan sealed classes con funciones `createRoute()` para navegación type-safe:

```kotlin
data class SeedDetail(val seedId: String = "{seedId}") : AppScreen(...) {
    fun createRoute(seedId: String) = "app/seed_detail/$seedId"
}
```

#### 2.5.5 Flujo de Navegación

```
SplashScreen (2 segundos)
    ↓
AuthNavGraph (si no hay sesión)
    ↓ Auth exitosa
AppNavGraph
    ├─ Garden (HomeScreen) ←→ SeedDetail ←→ SeedEditor
    ├─ Stats (StatsScreen)
    └─ Profile (ProfileScreen)
```

---

## 3. Pantallas de la Aplicación (MUY DETALLADO)

### 3.1 SplashScreen

**Tipo**: Pantalla de inicio/splash

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/splash/SplashScreen.kt`

**Qué muestra**:
- Ilustración grande (`splash_illustration.png`) de 300dp
- Nombre de la aplicación "LifeSeeds" con animación de fade infinito (alterna entre alpha 1.0 y 0.3 cada segundo)
- Fondo con color `LightGreenBg` (verde muy claro)

**Qué acciones puede hacer el usuario**:
- Ninguna (pantalla no interactiva)

**Desde dónde se accede**:
- Se muestra automáticamente al iniciar la aplicación
- Se muestra antes de cualquier otra pantalla

**A qué pantallas navega**:
- Automáticamente navega a `AuthNavGraph` o `AppNavGraph` después de 2 segundos

**ViewModel asociado**:
- Ninguno (pantalla puramente visual)

**Datos que consume/modifica**:
- Ninguno

**Características técnicas**:
- Utiliza `AnimatedVisibility` con `fadeOut` para transición
- Animación de alpha con `animateFloatAsState` y `tween(1000ms)`
- `LaunchedEffect` para controlar el tiempo de visualización

---

### 3.2 AuthScreen (Login/Registro)

**Tipo**: Pantalla de autenticación (login y registro combinados)

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/auth/AuthScreen.kt`

**Qué muestra**:
- Título dinámico: "Iniciar Sesión" o "Registrarse" según el modo
- Campos de formulario:
  - **Modo Registro**: Nombre, Email, Contraseña, Confirmar Contraseña
  - **Modo Login**: Email, Contraseña
- Validación en tiempo real con mensajes de error
- Botón principal: "Iniciar Sesión" o "Registrarse"
- Botón secundario: Alternar entre login y registro
- Botón de modo invitado: "Entrar como invitado"
- Mensajes de error de Firebase (si existen)

**Qué acciones puede hacer el usuario**:
1. **Alternar entre Login y Registro**: Click en "¿No tienes cuenta? Regístrate" / "¿Ya tienes cuenta? Inicia sesión"
2. **Registrarse**: Completar formulario de registro y click en "Registrarse"
3. **Iniciar Sesión**: Completar formulario de login y click en "Iniciar Sesión"
4. **Entrar como Invitado**: Click en "Entrar como invitado"
5. **Escribir en campos**: Validación en tiempo real mientras escribe

**Desde dónde se accede**:
- Automáticamente si no hay sesión activa
- Desde `ProfileScreen` al cerrar sesión

**A qué pantallas navega**:
- Automáticamente a `AppNavGraph` (HomeScreen) cuando la autenticación es exitosa
- A `AppNavGraph` en modo invitado

**ViewModel asociado**:
- `AuthViewModel` (`app/src/main/java/com/example/seedlife/ui/auth/AuthViewModel.kt`)

**Datos que consume/modifica**:
- **Consume**:
  - `authState: StateFlow<AuthState>` - Estado de autenticación
- **Modifica** (a través del ViewModel):
  - Crea usuario en Firebase Authentication
  - Crea documento de usuario en Firestore (`/users/{uid}`)
  - Inicia sesión en Firebase Authentication
  - Establece modo invitado (sin datos en Firebase)

**Estados de la pantalla**:
- `AuthState.Idle` - Estado inicial
- `AuthState.Loading` - Muestra `CircularProgressIndicator` en el botón
- `AuthState.Success` - Navega automáticamente a HomeScreen
- `AuthState.Error` - Muestra mensaje de error

**Validaciones implementadas**:
- Email: Formato válido (regex)
- Contraseña: Mínimo 6 caracteres
- Confirmar Contraseña: Debe coincidir con contraseña
- Nombre: No puede estar vacío (solo en registro)

**Características técnicas**:
- Validación en tiempo real con `LaunchedEffect` que observa cambios en los campos
- Limpieza automática de errores al escribir
- Deshabilitación de botones durante carga
- Navegación automática mediante `LaunchedEffect(authState)`

---

### 3.3 HomeScreen (Garden - Jardín)

**Tipo**: Pantalla de listado principal

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/home/HomeScreen.kt`

**Qué muestra**:
- **TopBar**:
  - Título: "Mi Jardín"
  - Icono de búsqueda (abre barra de búsqueda)
  - Icono de filtros (abre diálogo de filtros)
  - Barra de búsqueda expandible (cuando se activa)
- **Contenido principal**:
  - **Estado vacío**: Si no hay seeds
    - Ilustración `empty_garden.png`
    - Texto: "No hay semillas"
    - Texto: "Toca el botón + para crear tu primera semilla"
  - **Lista de seeds**: Si hay seeds
    - Cards con información de cada seed:
      - Imagen (thumbnail desde `photoUrl` o placeholder)
      - Título
      - Descripción
      - Imagen de nivel (según `getLevelImageResource()`)
      - Texto "Nivel: X"
      - Menú de opciones (tres puntos verticales)
  - **Estado de búsqueda vacía**: Si hay filtros activos pero no hay resultados
    - Texto: "No se encontraron semillas"
    - Texto: "Intenta ajustar los filtros de búsqueda"
- **Filtros activos**: Chips mostrando filtros aplicados (si hay)
- **FloatingActionButton**: Botón "+" para crear nueva seed
- **Snackbar**: Para mensajes de éxito/error

**Qué acciones puede hacer el usuario**:
1. **Buscar**: Click en icono de búsqueda → escribir en barra de búsqueda
2. **Filtrar**: Click en icono de filtros → abrir diálogo de filtros
3. **Ver detalle**: Click en un card de seed → navegar a `SeedDetailScreen`
4. **Editar seed**: Click en menú (tres puntos) → "Editar" → navegar a `SeedEditorScreen`
5. **Eliminar seed**: Click en menú → "Eliminar" → confirmar en diálogo
6. **Crear nueva seed**: Click en FAB → navegar a `SeedEditorScreen` con `seedId` vacío
7. **Limpiar filtros**: Click en "Limpiar" en los chips de filtros
8. **Expandir/colapsar seed**: Click en icono de tres puntos para ver menú

**Desde dónde se accede**:
- Desde `AppNavGraph` como pantalla principal (ruta `app/garden`)
- Desde `SeedDetailScreen` al presionar back
- Desde `SeedEditorScreen` al presionar back

**A qué pantallas navega**:
- `SeedDetailScreen` - Al hacer click en un seed
- `SeedEditorScreen` - Al crear nueva seed o editar existente

**ViewModel asociado**:
- `HomeViewModel` (`app/src/main/java/com/example/seedlife/ui/home/HomeViewModel.kt`)

**Datos que consume/modifica**:
- **Consume**:
  - `uiState: StateFlow<UiState<List<Seed>>>` - Lista de seeds
  - `filteredSeeds: StateFlow<List<Seed>>` - Seeds filtradas
  - `searchFilters: StateFlow<SearchFilters>` - Filtros activos
  - `snackbarMessage: StateFlow<String?>` - Mensajes para snackbar
- **Modifica** (a través del ViewModel):
  - Observa seeds desde Firestore en tiempo real (`observeSeeds()`)
  - Elimina seed (`deleteSeed()`)
  - Actualiza filtros de búsqueda (`updateSearchQuery()`, `updateLevelFilter()`, `updateSortOption()`)

**Sistema de búsqueda y filtrado**:
- **Búsqueda por texto**: Filtra por título y descripción (case-insensitive)
- **Filtro por nivel**: Rango mínimo y máximo (1-5)
- **Ordenamiento** (8 opciones):
  - `CREATED_DATE_DESC` - Más recientes primero
  - `CREATED_DATE_ASC` - Más antiguas primero
  - `LEVEL_DESC` - Mayor nivel primero
  - `LEVEL_ASC` - Menor nivel primero
  - `TITLE_ASC` - Título A-Z
  - `TITLE_DESC` - Título Z-A
  - `LAST_WATERED_DESC` - Regadas recientemente
  - `LAST_WATERED_ASC` - Regadas hace tiempo

**Características técnicas**:
- Usa `LazyColumn` para lista eficiente
- `combine` de Flows para aplicar filtros reactivamente
- Modo invitado: Seeds almacenadas en memoria (`guestSeeds`)
- Diálogos: `DeleteSeedDialog`, `FilterDialog`
- Componentes reutilizables: `SeedItem`, `SearchBar`, `FilterChips`, `EmptySearchState`

---

### 3.4 SeedDetailScreen (Detalle de Semilla)

**Tipo**: Pantalla de detalle

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/seeddetail/SeedDetailScreen.kt`

**Qué muestra**:
- **TopBar**:
  - Título: "Detalle de Semilla"
  - Botón "Volver" (flecha hacia atrás)
  - Botón "Editar"
  - Botón "Eliminar" (en rojo)
- **Card principal con información de la seed**:
  - Imagen grande (200dp de altura) desde `photoUrl` o placeholder
  - Botón "Añadir foto" (solo si no es invitado)
  - Título de la seed
  - Descripción
  - Nivel actual
  - Última fecha de riego (si existe)
- **Sección "Historial de Riegos"**:
  - Lista de waterings ordenados por fecha descendente
  - Cada watering muestra:
    - Estado de ánimo (GOOD/OK/BAD) con emoji y color
    - Fecha del riego
    - Nota (si existe)
- **FloatingActionButton**: Botón "+" para añadir nuevo riego
- **Diálogos**:
  - `AddWateringDialog` - Para añadir riego
  - `DeleteSeedDialog` - Para confirmar eliminación

**Qué acciones puede hacer el usuario**:
1. **Volver**: Click en flecha atrás → navegar a `HomeScreen`
2. **Editar seed**: Click en "Editar" → navegar a `SeedEditorScreen`
3. **Eliminar seed**: Click en "Eliminar" → confirmar → eliminar y volver a HomeScreen
4. **Añadir riego**: Click en FAB → seleccionar mood → opcionalmente añadir nota → guardar
5. **Añadir foto**: Click en "Añadir foto" → pedir permiso de cámara → tomar foto → subir a Firebase Storage

**Desde dónde se accede**:
- Desde `HomeScreen` al hacer click en un seed
- Navegación con parámetro `seedId` en la ruta: `app/seed_detail/{seedId}`

**A qué pantallas navega**:
- `HomeScreen` - Al presionar back o eliminar seed
- `SeedEditorScreen` - Al hacer click en "Editar"

**ViewModel asociado**:
- `SeedDetailViewModel` (`app/src/main/java/com/example/seedlife/ui/seeddetail/SeedDetailViewModel.kt`)

**Datos que consume/modifica**:
- **Consume**:
  - `seed: StateFlow<UiState<Seed?>>` - Seed actual (observado en tiempo real)
  - `waterings: StateFlow<UiState<List<Watering>>>` - Lista de waterings (observada en tiempo real)
  - `isLoading: StateFlow<Boolean>` - Estado de carga
  - `snackbarMessage: StateFlow<String?>` - Mensajes
- **Modifica** (a través del ViewModel):
  - Añade watering (`addWatering()`)
  - Elimina seed (`deleteSeed()`)
  - Sube foto (`uploadSeedPhoto()`)

**Características técnicas**:
- Observación en tiempo real de seed y waterings mediante `observeSeed()` y `observeWaterings()`
- Cálculo automático de nivel al añadir riego: `minOf(5, 1 + (totalWaterings / 3))`
- Integración con cámara usando `ActivityResultContracts.TakePicture()`
- Permisos de cámara con `ActivityResultContracts.RequestPermission()`
- Subida de imágenes a Firebase Storage
- Modo invitado: Datos en memoria, sin funcionalidad de fotos

**Sistema de niveles**:
- Nivel 1: 0-2 riegos
- Nivel 2: 3-5 riegos
- Nivel 3: 6-8 riegos
- Nivel 4: 9-11 riegos
- Nivel 5: 12+ riegos

---

### 3.5 SeedEditorScreen (Editor de Semilla)

**Tipo**: Pantalla de formulario (crear/editar)

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/seededitor/SeedEditorScreen.kt`

**Qué muestra**:
- **TopBar**:
  - Título dinámico: "Nueva Semilla" o "Editar Semilla"
  - Botón "Volver" (flecha hacia atrás)
- **Formulario**:
  - Campo "Título" (OutlinedTextField, singleLine)
  - Campo "Descripción" (OutlinedTextField, maxLines 10)
  - Contador de caracteres para descripción (X/200)
  - Botón "Guardar"
- **Validación en tiempo real**:
  - Mensajes de error bajo cada campo
  - Título: mínimo 3 caracteres, no puede estar vacío
  - Descripción: máximo 200 caracteres
- **Snackbar**: Para mensajes de éxito/error

**Qué acciones puede hacer el usuario**:
1. **Escribir título**: Validación en tiempo real
2. **Escribir descripción**: Validación en tiempo real con contador
3. **Guardar**: Click en "Guardar" → validar → crear/actualizar seed → navegar atrás
4. **Cancelar**: Click en flecha atrás → navegar atrás sin guardar

**Desde dónde se accede**:
- Desde `HomeScreen` al crear nueva seed (FAB o menú "Editar" con seedId vacío)
- Desde `SeedDetailScreen` al hacer click en "Editar"
- Navegación con parámetro `seedId`: `app/seed_editor/{seedId}` (vacío = crear nueva)

**A qué pantallas navega**:
- `HomeScreen` - Al guardar o cancelar
- `SeedDetailScreen` - Si se editó desde detalle (comportamiento actual: vuelve a Home)

**ViewModel asociado**:
- `SeedEditorViewModel` (incluido en el mismo archivo)

**Datos que consume/modifica**:
- **Consume**:
  - `title: StateFlow<String>` - Título actual
  - `description: StateFlow<String>` - Descripción actual
  - `isLoading: StateFlow<Boolean>` - Estado de carga
  - `errorMessage: StateFlow<String?>` - Errores
  - `snackbarMessage: StateFlow<String?>` - Mensajes
- **Modifica** (a través del ViewModel):
  - Crea seed (`createSeed()`)
  - Actualiza seed (`updateSeed()`)
  - Si está editando, observa seed existente para cargar datos

**Características técnicas**:
- Factory pattern para crear ViewModel con parámetros (`SeedEditorViewModelFactory`)
- Validación con `ValidationUtils.validateSeedTitle()` y `validateSeedDescription()`
- Modo invitado: Retorna éxito sin operación en Firebase
- Carga automática de datos si está editando (observa seed desde Firestore)

---

### 3.6 StatsScreen (Estadísticas)

**Tipo**: Pantalla de información/estadísticas

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/stats/StatsScreen.kt`

**Qué muestra**:
- **Título**: "Estadísticas"
- **Modo Invitado**:
  - Card informativo: "No disponible en modo invitado"
  - Texto: "Inicia sesión para ver tus estadísticas"
- **Modo Autenticado**:
  - Card "Total de Semillas": Número grande con valor
  - Card "Total de Riegos": Número grande con valor
  - Indicador de carga mientras se calculan

**Qué acciones puede hacer el usuario**:
- Ninguna (pantalla de solo lectura)

**Desde dónde se accede**:
- Desde `AppNavGraph` como una de las tres pantallas principales
- Ruta: `app/stats`
- Acceso desde Bottom Navigation Bar

**A qué pantallas navega**:
- Ninguna (pantalla terminal)

**ViewModel asociado**:
- `StatsViewModel` (incluido en el mismo archivo)

**Datos que consume/modifica**:
- **Consume**:
  - `stats: StateFlow<UserStats>` - Estadísticas (observadas en tiempo real)
  - `isLoading: StateFlow<Boolean>` - Estado de carga
  - `SessionViewModel.sessionState` - Para obtener `uid` y verificar si es invitado
- **Modifica** (a través del ViewModel):
  - Calcula estadísticas iniciales (`calculateStats()`)
  - Observa estadísticas en tiempo real desde Firestore (`observeStats()`)
  - Actualiza estadísticas en Firestore (`updateStats()`)

**Características técnicas**:
- Estadísticas almacenadas en `/users/{uid}/stats/summary`
- Cálculo inicial desde colecciones reales (cuenta seeds y waterings)
- Observación en tiempo real de cambios en estadísticas
- Modo invitado: Muestra mensaje informativo, no calcula nada

---

### 3.7 ProfileScreen (Perfil)

**Tipo**: Pantalla de perfil y configuración

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/profile/ProfileScreen.kt`

**Qué muestra**:
- **Título**: "Perfil"
- **Modo Invitado**:
  - Card: "Modo Invitado"
  - Texto explicativo sobre crear cuenta
  - Botón: "Crear cuenta / Iniciar sesión"
- **Modo Autenticado**:
  - Card "Información del Usuario":
    - Nombre
    - Email
  - Botón "Cerrar Sesión" (en rojo)
- **Sección de Configuración**:
  - Card con toggle "Modo Oscuro" (UI preparada, funcionalidad pendiente)

**Qué acciones puede hacer el usuario**:
1. **Crear cuenta / Iniciar sesión** (modo invitado): Click en botón → navegar a `AuthNavGraph`
2. **Cerrar sesión** (modo autenticado): Click en botón → cerrar sesión → navegar a `AuthNavGraph`
3. **Toggle modo oscuro**: Click en switch (funcionalidad pendiente)

**Desde dónde se accede**:
- Desde `AppNavGraph` como una de las tres pantallas principales
- Ruta: `app/profile`
- Acceso desde Bottom Navigation Bar

**A qué pantallas navega**:
- `AuthNavGraph` - Al cerrar sesión o al intentar crear cuenta desde modo invitado

**ViewModel asociado**:
- `SessionViewModel` (compartido globalmente)

**Datos que consume/modifica**:
- **Consume**:
  - `SessionViewModel.sessionState` - Estado de sesión con `userProfile`
- **Modifica** (a través del ViewModel):
  - Cierra sesión (`signOut()`)

**Características técnicas**:
- Acceso a `SessionViewModel` global para obtener perfil del usuario
- Observación en tiempo real del perfil mediante `UserRepository.observeUserProfile()`
- Modo invitado: Muestra opción para crear cuenta

---

## 4. Modelo de Datos y Base de Datos

### 4.1 Modelos de Datos Locales

#### 4.1.1 Seed (Semilla)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/model/Seed.kt`

**Estructura**:
```kotlin
data class Seed(
    var id: String = "",                    // ID del documento en Firestore
    var title: String = "",                 // Título de la seed
    var description: String = "",           // Descripción opcional
    var level: Int = 1,                     // Nivel actual (1-5)
    var lastWateredAt: Date? = null,        // Última fecha de riego
    var photoUrl: String? = null,           // URL de la foto en Firebase Storage
    var createdAt: Date? = null             // Fecha de creación (@ServerTimestamp)
)
```

**Anotaciones de Firestore**:
- `@PropertyName`: Mapeo explícito de nombres de campos
- `@ServerTimestamp`: `createdAt` se establece automáticamente por el servidor

**Relaciones**:
- Una Seed tiene múltiples Waterings (subcolección)

#### 4.1.2 Watering (Riego)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/model/Watering.kt`

**Estructura**:
```kotlin
data class Watering(
    var id: String = "",                    // ID del documento en Firestore
    var mood: String = WateringMood.OK.name, // Estado de ánimo (GOOD, OK, BAD)
    var note: String? = null,               // Nota opcional (máx 250 caracteres)
    var date: Date? = null,                 // Fecha del riego (@ServerTimestamp)
    var createdAt: Date? = null             // Fecha de creación (@ServerTimestamp)
)
```

**Métodos auxiliares**:
- `getMoodEnum()`: Convierte `mood` (String) a `WateringMood` (Enum)

**Enum WateringMood**:
```kotlin
enum class WateringMood {
    GOOD,  // Estado positivo
    OK,    // Estado neutral
    BAD    // Estado negativo
}
```

#### 4.1.3 User (Usuario)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/model/User.kt`

**Estructura**:
```kotlin
data class User(
    var name: String = "",                  // Nombre del usuario
    var email: String = "",                  // Email del usuario
    var createdAt: Long = System.currentTimeMillis() // Timestamp de creación
)
```

**Uso**: Almacenado en Firestore al registrar usuario, sincronizado con Firebase Authentication.

#### 4.1.4 UserProfile (Perfil de Usuario)
**Ubicación**: `app/src/main/java/com/example/seedlife/data/model/UserProfile.kt`

**Estructura**:
```kotlin
data class UserProfile(
    var name: String = "",                  // Nombre del usuario
    var email: String = "",                  // Email del usuario
    var createdAt: Long = System.currentTimeMillis() // Timestamp de creación
)
```

**Uso**: Similar a `User`, pero usado específicamente para observación en tiempo real del perfil en `SessionViewModel`.

### 4.2 Base de Datos: Cloud Firestore

#### 4.2.1 Configuración
- **Proyecto Firebase**: `seedlife-3a4d8`
- **Persistencia Offline**: Habilitada mediante `FirestoreConfig.enablePersistence()`
- **Configuración**: Se llama en `MainActivity.onCreate()` ANTES de cualquier uso de Firestore

#### 4.2.2 Estructura de Colecciones

```
/users/{uid}
  ├── name: String
  ├── email: String
  ├── createdAt: Long
  │
  ├── /seeds/{seedId}
  │   ├── title: String
  │   ├── description: String
  │   ├── level: Int (1-5)
  │   ├── lastWateredAt: Timestamp
  │   ├── photoUrl: String? (URL de Firebase Storage)
  │   ├── createdAt: Timestamp (@ServerTimestamp)
  │   │
  │   └── /waterings/{wateringId}
  │       ├── id: String
  │       ├── mood: String (GOOD/OK/BAD)
  │       ├── note: String?
  │       ├── date: Timestamp (@ServerTimestamp)
  │       └── createdAt: Timestamp (@ServerTimestamp)
  │
  └── /stats/summary
      ├── totalSeeds: Int
      └── totalWaterings: Int
```

#### 4.2.3 Reglas de Seguridad (Recomendadas)
Las reglas de Firestore deberían implementarse para:
- Validar que `request.auth.uid == resource.data.userId` para operaciones de lectura/escritura
- Restringir acceso a subcolecciones de otros usuarios
- Permitir lectura/escritura solo del propio usuario

#### 4.2.4 Observación en Tiempo Real
Todos los repositorios utilizan `addSnapshotListener` encapsulado en `callbackFlow` para observación reactiva:
- `observeSeeds()`: Observa lista de seeds
- `observeSeed()`: Observa una seed específica
- `observeWaterings()`: Observa waterings de una seed
- `observeUserProfile()`: Observa perfil del usuario
- `observeStats()`: Observa estadísticas

#### 4.2.5 Operaciones CRUD

**Create**:
- `createSeed()`: Crea nueva seed en `/users/{uid}/seeds/{seedId}`
- `addWatering()`: Crea watering en subcolección y actualiza nivel de seed

**Read**:
- Todas las operaciones de lectura usan observación en tiempo real

**Update**:
- `updateSeed()`: Actualiza título y descripción
- `addWatering()`: Actualiza `level` y `lastWateredAt` de la seed
- `uploadSeedPhoto()`: Actualiza `photoUrl` de la seed

**Delete**:
- `deleteSeed()`: Elimina seed y todos sus waterings (borrado en cascada usando batch)

### 4.3 Firebase Storage

**Bucket**: `gs://seedlife-3a4d8.firebasestorage.app`

**Estructura**:
```
users/{uid}/seeds/{seedId}/cover_{timestamp}.jpg
```

**Operaciones**:
- `uploadSeedPhoto()`: Sube imagen desde URI local, obtiene URL de descarga, actualiza `photoUrl` en Firestore

**Permisos**:
- Requiere permiso `CAMERA` del dispositivo
- Requiere reglas de Storage configuradas en Firebase Console

### 4.4 Firebase Authentication

**Métodos de autenticación**:
- Email/Contraseña (registro e inicio de sesión)
- Modo invitado (sin autenticación Firebase, solo local)

**Operaciones**:
- `register()`: Crea usuario en Firebase Auth y documento en Firestore
- `login()`: Inicia sesión con email/contraseña
- `signOut()`: Cierra sesión
- `isUserLoggedIn()`: Verifica si hay sesión activa
- `getCurrentUserId()`: Obtiene UID del usuario actual

---

## 5. Repositorios y Lógica de Negocio

### 5.1 AuthRepository

**Ubicación**: `app/src/main/java/com/example/seedlife/data/repository/AuthRepository.kt`

**Responsabilidades**:
- Autenticación con Firebase Authentication
- Creación de documentos de usuario en Firestore
- Obtención de datos de usuario
- Gestión de sesión

**Métodos principales**:
- `register(email, password, name)`: Registra usuario y crea documento en Firestore
- `login(email, password)`: Inicia sesión
- `getUserData(uid)`: Obtiene datos del usuario desde Firestore
- `signOut()`: Cierra sesión
- `isUserLoggedIn()`: Verifica sesión
- `getCurrentUserId()`: Obtiene UID actual

**Manejo de errores**: Utiliza `FirebaseErrorMapper` para mensajes amigables.

### 5.2 SeedRepository

**Ubicación**: `app/src/main/java/com/example/seedlife/data/repository/SeedRepository.kt`

**Responsabilidades**:
- Operaciones CRUD de Seeds
- Operaciones CRUD de Waterings
- Cálculo automático de niveles
- Subida de fotos a Firebase Storage

**Métodos principales**:
- `observeSeeds(uid)`: Observa lista de seeds en tiempo real
- `observeSeed(uid, seedId)`: Observa una seed específica
- `observeWaterings(uid, seedId)`: Observa waterings de una seed
- `createSeed(uid, title, description)`: Crea nueva seed
- `updateSeed(uid, seedId, title, description)`: Actualiza seed
- `deleteSeed(uid, seedId)`: Elimina seed y waterings (cascada)
- `addWatering(uid, seedId, mood, note)`: Añade riego y actualiza nivel
- `uploadSeedPhoto(uid, seedId, uri)`: Sube foto y actualiza `photoUrl`

**Lógica de negocio**:
- **Cálculo de nivel**: `minOf(5, 1 + (totalWaterings / 3))`
- **Borrado en cascada**: Elimina todos los waterings antes de eliminar seed (usando batch)

### 5.3 UserRepository

**Ubicación**: `app/src/main/java/com/example/seedlife/data/repository/UserRepository.kt`

**Responsabilidades**:
- Observación de perfil de usuario en tiempo real
- Gestión de sesión

**Métodos principales**:
- `observeUserProfile(uid)`: Observa perfil en tiempo real
- `signOut()`: Cierra sesión

### 5.4 StatsRepository

**Ubicación**: `app/src/main/java/com/example/seedlife/data/repository/StatsRepository.kt`

**Responsabilidades**:
- Cálculo de estadísticas del usuario
- Observación de estadísticas en tiempo real
- Actualización de estadísticas en Firestore

**Métodos principales**:
- `observeStats(uid)`: Observa estadísticas en tiempo real
- `calculateStats(uid)`: Calcula estadísticas desde colecciones reales
- `updateStats(uid, totalSeeds, totalWaterings)`: Actualiza estadísticas en Firestore

**Modelo de datos**:
```kotlin
data class UserStats(
    val totalSeeds: Int = 0,
    val totalWaterings: Int = 0
)
```

---

## 6. Utilidades y Componentes Compartidos

### 6.1 ValidationUtils

**Ubicación**: `app/src/main/java/com/example/seedlife/util/ValidationUtils.kt`

**Funciones de validación**:
- `isValidEmail(email)`: Valida formato de email (regex)
- `isValidPassword(password, minLength)`: Valida longitud mínima (default 6)
- `isValidName(name, minLength)`: Valida longitud mínima (default 2)
- `validateSeedTitle(title)`: Valida título (mín 3 caracteres, no vacío)
- `validateSeedDescription(description)`: Valida descripción (máx 200 caracteres)
- `validateWateringNote(note)`: Valida nota (máx 250 caracteres)

**Retorno**: `ValidationResult(isValid: Boolean, errorMessage: String?)`

### 6.2 FirebaseErrorMapper

**Ubicación**: `app/src/main/java/com/example/seedlife/util/FirebaseErrorMapper.kt`

**Responsabilidad**: Mapea excepciones de Firebase a mensajes amigables en español.

**Mapeos principales**:
- **Firestore**: `PERMISSION_DENIED`, `UNAVAILABLE`, `DEADLINE_EXCEEDED`, etc.
- **Authentication**: `ERROR_EMAIL_ALREADY_IN_USE`, `ERROR_WRONG_PASSWORD`, `ERROR_USER_NOT_FOUND`, etc.

### 6.3 UiState

**Ubicación**: `app/src/main/java/com/example/seedlife/ui/common/UiState.kt`

**Estructura**:
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
}
```

**Uso**: Patrón común para representar estados de carga, éxito y error en todas las pantallas.

---

## 7. Dependencias y Tecnologías

### 7.1 Dependencias Principales

**UI y Compose**:
- `androidx.compose.ui:ui` - Compose UI
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.compose.ui:ui-tooling-preview` - Preview tools
- `androidx.activity:activity-compose:1.8.0` - Activity Compose
- `androidx.core:core-splashscreen:1.0.1` - SplashScreen API

**Arquitectura**:
- `androidx.lifecycle:lifecycle-runtime-ktx:2.6.1` - Lifecycle
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1` - ViewModel para Compose

**Navegación**:
- `androidx.navigation:navigation-compose:2.8.4` - Navigation Compose

**Firebase**:
- `com.google.firebase:firebase-bom:33.7.0` - BOM (Bill of Materials)
- `com.google.firebase:firebase-auth` - Authentication
- `com.google.firebase:firebase-firestore` - Cloud Firestore
- `com.google.firebase:firebase-storage` - Firebase Storage
- `com.google.gms:google-services:4.4.2` - Google Services Plugin

**Imágenes**:
- `io.coil-kt:coil-compose:2.5.0` - Coil para cargar imágenes

**Coroutines**:
- `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3` - Coroutines para Firebase

**Testing**:
- `junit:junit:4.13.2` - JUnit
- `io.mockk:mockk:1.13.8` - MockK para mocking
- `app.cash.turbine:turbine:1.0.0` - Testing de Flows
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3` - Testing de Coroutines

### 7.2 Versiones Clave

- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.13.2
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 24 (Android 7.0 Nougat)
- **JDK**: 11

---

## 8. Características Técnicas Avanzadas

### 8.1 Persistencia Offline

**Implementación**: `FirestoreConfig.enablePersistence()`

**Características**:
- Cache local automático de consultas recientes
- Funcionamiento sin conexión
- Sincronización automática cuando se restaura la conexión
- Se habilita ANTES de cualquier uso de Firestore (en `MainActivity.onCreate()`)

### 8.2 Observación en Tiempo Real

**Patrón**: `callbackFlow` con `addSnapshotListener`

**Ejemplo**:
```kotlin
fun observeSeeds(uid: String): Flow<List<Seed>> = callbackFlow {
    val listenerRegistration = collection.addSnapshotListener { snapshot, error ->
        // Procesar cambios
        trySend(seeds)
    }
    awaitClose { listenerRegistration.remove() }
}
```

**Ventajas**:
- Actualización automática de UI cuando hay cambios en Firestore
- No requiere polling manual
- Eficiente en uso de recursos

### 8.3 Manejo de Errores

**Estrategia**:
1. Repositorios capturan excepciones y retornan `Result<T>`
2. `FirebaseErrorMapper` convierte excepciones a mensajes amigables
3. ViewModels exponen errores en `StateFlow`
4. UI muestra errores con opción de retry

### 8.4 Modo Invitado

**Implementación**:
- Datos almacenados en memoria (`guestSeeds`, `guestWaterings`)
- No se realizan operaciones en Firebase
- Funcionalidad limitada (sin fotos, sin estadísticas)
- Permite probar la app sin registro

### 8.5 Factory Pattern para ViewModels

**Uso**: `ViewModelProvider.Factory` para crear ViewModels con parámetros:

```kotlin
class HomeViewModelFactory(
    private val uid: String,
    private val isGuest: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(uid, isGuest) as T
    }
}
```

**Razón**: Los ViewModels necesitan parámetros (uid, seedId, etc.) que no pueden pasarse directamente al constructor.

---

## 9. Flujos de Usuario Principales

### 9.1 Flujo de Registro e Inicio de Sesión

1. Usuario abre la app → `SplashScreen` (2 segundos)
2. Si no hay sesión → `AuthScreen`
3. Usuario elige Login o Registro
4. Completa formulario con validación en tiempo real
5. Click en botón → `AuthViewModel` procesa
6. `AuthRepository` realiza operación en Firebase
7. Si éxito → `SessionViewModel` actualiza estado
8. Navegación automática a `HomeScreen`

### 9.2 Flujo de Crear Seed

1. Usuario en `HomeScreen` → Click en FAB "+"
2. Navegación a `SeedEditorScreen` con `seedId` vacío
3. Usuario completa título y descripción (validación en tiempo real)
4. Click en "Guardar" → `SeedEditorViewModel.save()`
5. `SeedRepository.createSeed()` crea documento en Firestore
6. Navegación de vuelta a `HomeScreen`
7. `HomeViewModel` observa cambios y actualiza lista automáticamente

### 9.3 Flujo de Añadir Riego

1. Usuario en `SeedDetailScreen` → Click en FAB "+"
2. Se muestra `AddWateringDialog`
3. Usuario selecciona mood (GOOD/OK/BAD) y opcionalmente añade nota
4. Click en "Guardar" → `SeedDetailViewModel.addWatering()`
5. `SeedRepository.addWatering()`:
   - Crea documento de watering
   - Cuenta total de waterings
   - Calcula nuevo nivel
   - Actualiza seed con nuevo nivel y `lastWateredAt`
6. Los listeners detectan cambios
7. UI se actualiza automáticamente mostrando nuevo riego y nivel

### 9.4 Flujo de Búsqueda y Filtrado

1. Usuario en `HomeScreen` → Click en icono de búsqueda
2. Se expande barra de búsqueda
3. Usuario escribe → `HomeViewModel.updateSearchQuery()` actualiza filtros
4. `combine(_uiState, _searchFilters)` aplica filtros reactivamente
5. `filteredSeeds` se actualiza automáticamente
6. UI muestra solo seeds que coinciden con filtros
7. Usuario puede añadir filtros de nivel y ordenamiento desde diálogo de filtros

---

## 10. Consideraciones de Seguridad

### 10.1 Autenticación

- Contraseñas nunca se almacenan localmente ni en texto plano
- Firebase Authentication maneja el hashing y almacenamiento seguro
- Verificación de sesión al iniciar la app

### 10.2 Datos de Usuario

- Cada usuario solo puede acceder a sus propios datos (mediante `uid`)
- Estructura de Firestore separa datos por usuario
- Reglas de seguridad de Firestore deben validar `request.auth.uid`

### 10.3 Validación

- Validación del lado del cliente antes de enviar a Firebase
- Sanitización de entradas (trim de strings)
- Límites de longitud en campos de texto

### 10.4 Permisos

- Permiso de cámara solo se solicita cuando el usuario intenta tomar foto
- Manejo de denegación de permisos con mensajes informativos

---

## 11. Testing

### 11.1 Estrategia de Testing

**Unit Tests**:
- ViewModels con MockK para mockear repositorios
- Repositorios con mock de Firebase
- Utilidades (ValidationUtils, FirebaseErrorMapper)

**UI Tests**:
- Tests de Compose con `androidx.compose.ui.test`
- Tests de navegación
- Tests de interacción de usuario

**Flow Testing**:
- Turbine para testing de Flows
- Coroutines Test para testing asíncrono

### 11.2 Dependencias de Testing

- `junit:junit:4.13.2`
- `io.mockk:mockk:1.13.8`
- `app.cash.turbine:turbine:1.0.0`
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3`
- `androidx.compose.ui:ui-test-junit4`

---

## 12. Mejoras Futuras y Roadmap

### 12.1 Funcionalidades Pendientes

- Inyección de dependencias (Hilt/Koin)
- Integración de Room para cache offline adicional
- Gráficos avanzados de estadísticas
- Notificaciones push para recordatorios de riego
- Exportación de datos (CSV/JSON)
- Sistema de logros/badges
- Búsqueda avanzada con más filtros
- Compartir seeds con otros usuarios
- Modo oscuro funcional (UI preparada)

### 12.2 Optimizaciones

- Paginación de seeds en HomeScreen
- Lazy loading de imágenes
- Optimización de queries de Firestore
- Cache de imágenes con Coil

---

## Conclusión

SeedLife es una aplicación Android moderna que implementa las mejores prácticas de desarrollo:
- **Arquitectura MVVM** clara y mantenible
- **Jetpack Compose** para UI declarativa y moderna
- **Firebase** como backend completo (Auth, Firestore, Storage)
- **Observación en tiempo real** para experiencia reactiva
- **Persistencia offline** para funcionamiento sin conexión
- **Validación robusta** y manejo de errores amigable
- **Modo invitado** para accesibilidad

La aplicación demuestra competencia en:
- Gestión de estado reactivo con StateFlow
- Navegación type-safe con Navigation Compose
- Integración completa con Firebase
- Diseño de UI moderna con Material Design 3
- Buenas prácticas de Android development

---

**Versión del documento**: 1.0  
**Fecha**: 2025-01-09  
**Proyecto**: SeedLife  
**Package**: `com.example.seedlife`
