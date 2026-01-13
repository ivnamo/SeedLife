# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

### Cambios Planificados
- Inyección de dependencias (Hilt/Koin) para mejor gestión de dependencias
- Integración de Room para cache offline adicional (actualmente se usa persistencia offline de Firestore)
- Mejoras en manejo de errores global con UI de errores consistente
- Gráficos avanzados de estadísticas (actualmente hay estadísticas básicas)
- Notificaciones push para recordatorios de riego
- Exportación de datos (CSV/JSON) para respaldo
- Mejoras en UI/UX: animaciones, transiciones suaves
- Sistema de logros/badges basado en niveles de seeds

---

## [1.4.0] - 2025-01-09

### Agregado
- **Sistema de búsqueda y filtrado completo** en HomeScreen con funcionalidades avanzadas
- Búsqueda por texto (título y descripción de seeds) con barra de búsqueda integrada
- Filtrado por nivel (rango mínimo y máximo) mediante diálogo de filtros
- Sistema de ordenamiento con 8 opciones: fecha de creación (asc/desc), nivel (asc/desc), título (A-Z/Z-A), última fecha de riego (asc/desc)
- `SearchFilters` data class para gestión de filtros con StateFlow
- `SortOption` enum para opciones de ordenamiento type-safe
- `FilterDialog` y `FilterChips` componentes para UI de filtros
- Barra de búsqueda expandible con icono de búsqueda en TopBar
- `EmptySearchState` componente para estado cuando no hay resultados de búsqueda
- **Recursos visuales mejorados**:
  - `empty_garden.png` - Ilustración para estado vacío (sin seeds)
  - `splash_illustration.png` - Ilustración para pantalla de splash
  - `seed_bag.png` - Imagen para seeds nivel 1
  - `planta.png` - Imagen para seeds nivel 2
  - `plantas.png` - Imagen para seeds nivel 3 o superior
  - `ic_filter_list.xml` - Icono de filtros para TopBar
- Sistema de imágenes dinámicas según nivel (`getLevelImageResource()`) en HomeScreen
- **Nueva paleta de colores** temática de jardín/naturaleza:
  - Colores Light: LeafGreen, SoftGreen, EarthBrown, LightGreenBg, WhiteSurface
  - Colores Dark: LeafGreenDark, SoftGreenDark, EarthBrownDark, DarkGreenBg, DarkSurface
- Eliminación de seeds con diálogo de confirmación (`DeleteSeedDialog`)
- Snackbar integrado para mensajes de éxito/error en operaciones
- Estado expandible/colapsable para items de seeds en lista
- Mejoras en UI de estado vacío con ilustración y mensaje descriptivo

### Cambios
- Actualización completa de paleta de colores de púrpura/rosa a tema jardín/naturaleza (verdes y marrones)
- `LightColorScheme` y `DarkColorScheme` actualizados con nueva paleta
- Tema dinámico desactivado por defecto para usar paleta personalizada
- `HomeScreen` mejorado con funcionalidades de búsqueda, filtrado y ordenamiento
- `HomeViewModel` ampliado con lógica de filtrado y ordenamiento usando `combine` de Flows
- SplashScreen actualizado con ilustración personalizada y animación de fade
- Mejora de experiencia de usuario con estados visuales mejorados (vacío, búsqueda sin resultados)

### Recursos Añadidos
- Drawables: `empty_garden.png`, `splash_illustration.png`, `seed_bag.png`, `planta.png`, `plantas.png`, `ic_filter_list.xml`

### Dependencias Añadidas
- `androidx.core:core-splashscreen` (SplashScreen API)

---

## [1.3.0] - 2025-01-09

### Agregado
- **SplashScreen** con animación usando SplashScreen API (Android 12+)
- **SessionViewModel** para gestión de sesión global separada de autenticación
- **SeedEditorScreen** y **SeedEditorViewModel** para crear y editar seeds
- **StatsScreen** para visualización de estadísticas del usuario (total de seeds y waterings)
- **ProfileScreen** para gestión de perfil de usuario
- **UserRepository** para operaciones de perfil de usuario con observación en tiempo real
- **StatsRepository** para gestión y cálculo de estadísticas del usuario
- **UserProfile** modelo de datos para perfiles de usuario
- **FirebaseErrorMapper** utilidad para mapear excepciones de Firebase a mensajes amigables en español
- **FirestoreConfig** configuración de persistencia offline de Firestore
- **UiState** clase común para estados de UI
- **Bottom Navigation Bar** con tres secciones: Garden (Home), Stats, Profile
- Navegación mejorada con `AppScreen` sealed class para rutas type-safe
- Sistema de estadísticas con estructura en Firestore (`/users/{uid}/stats/summary`)
- Observación de perfiles de usuario en tiempo real
- Sincronización automática de estado entre `AuthViewModel` y `SessionViewModel`
- Validación de formularios en `SeedEditorScreen` con `ValidationUtils`

### Cambios
- Actualización de `NavGraph` con navegación separada para Auth y App
- `HomeScreen` renombrado conceptualmente a "Garden" en la navegación
- Mejora de estructura de navegación con Bottom Navigation
- Refactorización de gestión de sesión separando `SessionViewModel` de `AuthViewModel`
- Actualización de `MainActivity` para incluir SplashScreen y gestión de sesión mejorada

### Dependencias Añadidas
- N/A (mismas dependencias de 1.2.0)

### Estructura de Datos
- Nueva colección de estadísticas: `/users/{uid}/stats/summary`
- Modelo `UserProfile` para perfiles de usuario

---

## [1.2.0] - 2025-01-09

### Agregado
- **Navegación completa** con Jetpack Navigation Compose 2.8.4
- `NavGraph.kt` con sistema de navegación centralizado y type-safe
- Sealed class `Screen` para definición de rutas con argumentos tipados
- Pantalla de detalle de seed (`SeedDetailScreen`) con información completa
- `SeedDetailViewModel` para gestión de estado del detalle con observación reactiva
- `SeedRepository` para operaciones CRUD de seeds y waterings
- Modelo de datos `Seed` con campos: title, description, level (1-5), lastWateredAt, createdAt
- Modelo de datos `Watering` con estados de ánimo (GOOD, OK, BAD) y notas opcionales
- Enum `WateringMood` para tipado seguro de estados de riego
- Sistema de niveles automático (1-5) basado en cantidad de riegos: `level = minOf(5, 1 + (totalWaterings / 3))`
- Observación en tiempo real de seeds y waterings desde Firestore usando `addSnapshotListener`
- Funcionalidad de añadir riegos con mood, notas opcionales y actualización automática de nivel
- `ValidationUtils` para validación de email, contraseña y nombres con funciones reutilizables
- Soporte completo para modo invitado en todas las pantallas (Auth, Home, SeedDetail)
- `SeedDetailViewModelFactory` para crear ViewModel con parámetros (seedId)
- Navegación con argumentos entre Home y SeedDetail usando NavBackStackEntry
- Estructura jerárquica de Firestore: `/users/{uid}/seeds/{seedId}/waterings/{wateringId}`
- Dependencias de testing avanzadas: MockK (mocking), Turbine (Flow testing), Coroutines Test
- Gestión de ciclo de vida de coroutines con ViewModelScope
- Manejo de resultados con `Result<T>` en repositorios para control de errores

### Cambios
- Actualización de `HomeScreen` para mostrar lista de seeds con navegación
- Mejora de `AuthViewModel` con validación usando `ValidationUtils`
- Refactorización de flujos de datos para usar observación en tiempo real
- Actualización de `MainActivity` para usar `SeedLifeNavGraph`

### Dependencias Añadidas
- `androidx.navigation:navigation-compose:2.8.4`
- `io.mockk:mockk:1.13.8`
- `app.cash.turbine:turbine:1.0.0`
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3`

### Estructura de Datos
- Collections de Firestore: `/users/{uid}/seeds` y `/users/{uid}/seeds/{seedId}/waterings`
- Sistema de cálculo de nivel: `level = minOf(5, 1 + (totalWaterings / 3))`
- Timestamps automáticos con `@ServerTimestamp` en Firestore

---

## [1.1.0] - 2025-01-09

### Agregado
- Integración completa de Firebase Authentication
- Integración de Cloud Firestore para almacenamiento de datos
- Implementación de arquitectura MVVM con ViewModel
- `AuthViewModel` para gestión de estado de autenticación
- `AuthRepository` implementando Repository Pattern
- Modelo de datos `User` para Firestore
- Pantalla de autenticación (`AuthScreen`)
- Pantalla principal (`HomeScreen`)
- Gestión de estado reactivo con StateFlow y sealed classes
- Soporte para autenticación como invitado
- Funcionalidades de registro, login, logout y verificación de sesión
- Google Services Plugin 4.4.2
- Firebase BOM 33.7.0 para gestión de versiones
- ViewModel Compose dependency para integración con Compose

### Configuración Técnica
- Firebase Authentication habilitado
- Cloud Firestore configurado
- Proyecto Firebase: `seedlife-3a4d8`
- Archivo `google-services.json` configurado

### Dependencias Añadidas
- `com.google.firebase:firebase-bom:33.7.0`
- `com.google.firebase:firebase-auth`
- `com.google.firebase:firebase-firestore`
- `com.google.gms:google-services:4.4.2` (plugin)
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1`

### Cambios
- Actualización de estructura de proyecto con carpetas `data/` y organización por features
- Migración de arquitectura básica a MVVM completa

---

## [1.0.0] - 2025-01-09

### Agregado
- Proyecto inicial configurado con Android Gradle Plugin 8.13.2
- Integración de Jetpack Compose con Material Design 3
- Configuración básica de Material Design 3
- Estructura de tema con soporte para modo oscuro y claro
- Tema dinámico para dispositivos Android 12+ (API 31+)
- `MainActivity` con estructura básica usando Compose
- Scaffold básico para la interfaz principal
- Configuración de Edge-to-Edge
- Definiciones de color (Purple y Pink) para tema claro y oscuro
- Soporte para previews de Compose
- Configuración de Gradle con Version Catalog (`libs.versions.toml`)
- Estructura de carpetas básica para tema UI
- Configuración de pruebas unitarias e instrumentadas
- Documentación inicial en carpeta `docs/`

### Configuración Técnica
- Kotlin 2.0.21
- Compose BOM 2024.09.00
- Min SDK: 24 (Android 7.0 Nougat)
- Target SDK: 36
- Compile SDK: 36
- JDK 11
- Package name: `com.example.seedlife`

### Dependencias Incluidas
- androidx.core:core-ktx:1.10.1
- androidx.lifecycle:lifecycle-runtime-ktx:2.6.1
- androidx.activity:activity-compose:1.8.0
- androidx.compose (UI, Material3, Tooling)
- JUnit 4.13.2 para tests unitarios
- Espresso 3.5.1 para tests de UI

### Cambios
- N/A (versión inicial)

### Eliminado
- N/A (versión inicial)

### Corregido
- N/A (versión inicial)

### Seguridad
- N/A (versión inicial)

---

## Formato de Cambios Futuros

### [Versión] - Fecha

#### Agregado
- Nueva funcionalidad

#### Cambios
- Cambios en funcionalidad existente

#### Deprecado
- Funcionalidad que será eliminada en versiones futuras

#### Eliminado
- Funcionalidad eliminada

#### Corregido
- Correcciones de bugs

#### Seguridad
- Vulnerabilidades corregidas

---

**Nota**: Los cambios se documentarán siguiendo este formato en todas las versiones.
