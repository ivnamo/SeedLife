# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [Unreleased]

### Cambios Planificados
- Navegación entre pantallas con Compose Navigation
- Inyección de dependencias (Hilt/Koin)
- Mejoras en manejo de errores

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
