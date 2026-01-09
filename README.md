# SeedLife

[Descripción del proyecto - ajustar según el propósito específico de SeedLife]

## 🚀 Características

- Interfaz moderna con Jetpack Compose
- Material Design 3 con soporte para tema dinámico
- Modo oscuro/claro automático
- Arquitectura MVVM implementada completamente
- Navegación con Jetpack Navigation Compose
- Autenticación con Firebase Authentication
- Base de datos en la nube con Cloud Firestore
- Gestión de estado reactivo con ViewModel y StateFlow
- Repository Pattern para acceso a datos
- Sistema de gestión de Seeds (semillas) con niveles
- Sistema de riegos (Waterings) con estados de ánimo
- Modo invitado para uso sin autenticación
- Validación de formularios con utilidades integradas
- Observación en tiempo real de datos desde Firestore

## 📋 Requisitos Previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 11 o superior
- Android SDK 36
- Gradle 8.13.2

## 🛠️ Instalación

1. Clona el repositorio:
   ```bash
   git clone [url-del-repositorio]
   cd SeedLife
   ```

2. Abre el proyecto en Android Studio

3. **Configura Firebase** (si aún no está configurado):
   - El archivo `google-services.json` ya está incluido en el proyecto
   - Asegúrate de que el proyecto de Firebase esté correctamente vinculado

4. Sincroniza las dependencias de Gradle (automático o manualmente con `./gradlew build`)

5. Configura un dispositivo virtual o conecta un dispositivo físico

6. Ejecuta la aplicación (Shift+F10 o botón Run)

## 📱 Especificaciones

- **Versión actual**: 1.2.0 (ver [CHANGELOG.md](docs/CHANGELOG.md) para historial completo)
- **Version Code**: 1
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Package**: `com.example.seedlife`

## 🏗️ Arquitectura

Este proyecto utiliza:
- **Jetpack Compose** para la UI moderna y declarativa
- **Material Design 3** para componentes y temas
- **Kotlin 2.0.21** como lenguaje principal
- **MVVM (Model-View-ViewModel)** para separación de responsabilidades
- **Repository Pattern** para abstracción de datos
- **Firebase** como backend (Authentication y Firestore)
- **StateFlow** para manejo reactivo de estado

Para más detalles sobre la arquitectura, consulta [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## 📚 Tecnologías Principales

- **Lenguaje**: Kotlin 2.0.21
- **UI**: Jetpack Compose (BOM 2024.09.00) + Material Design 3
- **Navegación**: Jetpack Navigation Compose 2.8.4
- **Arquitectura**: MVVM con ViewModel y StateFlow
- **Backend**: Firebase (Authentication + Cloud Firestore)
- **Reactive Streams**: Kotlin Coroutines y Flow para operaciones asíncronas
- **Build**: Android Gradle Plugin 8.13.2, Gradle con Version Catalog
- **AndroidX**: Core KTX, Lifecycle Runtime KTX, Lifecycle ViewModel Compose
- **Google Services**: Google Services Plugin 4.4.2
- **Testing**: JUnit, MockK, Turbine, Coroutines Test

## 🔄 Historial de Cambios

Para ver el historial completo de versiones, consulta [docs/CHANGELOG.md](docs/CHANGELOG.md).

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:
1. Revisa las convenciones de código
2. Crea una rama para tu funcionalidad
3. Realiza un Pull Request con una descripción clara

## 📝 Licencia

[Añadir información de licencia según corresponda]

## 📧 Contacto

[Añadir información de contacto si es necesario]

## 🎮 Funcionalidades Principales

- **Autenticación**: Registro, login, logout y modo invitado
- **Gestión de Seeds**: Visualización y detalle de semillas con sistema de niveles (1-5)
- **Sistema de Riegos**: Registro de riegos con estados de ánimo (GOOD, OK, BAD) y notas
- **Navegación**: Flujo completo entre Auth → Home → Seed Detail
- **Tiempo Real**: Actualización automática de datos desde Firestore
- **Validación**: Validación de email, contraseña y nombres en formularios

---

**Nota**: El proyecto está en desarrollo activo. Consulta [CHANGELOG.md](docs/CHANGELOG.md) para ver las últimas actualizaciones.
