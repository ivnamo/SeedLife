# SeedLife

[Descripción del proyecto - ajustar según el propósito específico de SeedLife]

## 🚀 Características

- Interfaz moderna con Jetpack Compose
- Material Design 3 con soporte para tema dinámico
- Modo oscuro/claro automático
- Arquitectura MVVM implementada
- Autenticación con Firebase Authentication
- Base de datos en la nube con Cloud Firestore
- Gestión de estado con ViewModel y StateFlow
- Repository Pattern para acceso a datos

## 📋 Requisitos Previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 11 o superior
- Android SDK 36
- Gradle 8.13.2

## 🛠️ Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/SeedLife.git
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

- **Versión actual**: 1.0
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
- **Arquitectura**: MVVM con ViewModel y StateFlow
- **Backend**: Firebase (Authentication + Cloud Firestore)
- **Build**: Android Gradle Plugin 8.13.2, Gradle con Version Catalog
- **AndroidX**: Core KTX, Lifecycle Runtime KTX, Lifecycle ViewModel Compose
- **Google Services**: Google Services Plugin 4.4.2

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

---

**Nota**: Este proyecto está en fase inicial de desarrollo. Las funcionalidades principales se implementarán progresivamente.
