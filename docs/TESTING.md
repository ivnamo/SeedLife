# 📋 Guía de Testing - SeedLife

Este documento describe todos los tests de validación implementados en el proyecto SeedLife.

## 📊 Resumen de Tests Implementados

### Tests Unitarios (Local - JVM)
- **Total de tests unitarios**: 30 tests
- **Ubicación**: `app/src/test/java/com/example/seedlife/`

### Tests Instrumentados (Android Device/Emulator)
- **Total de tests instrumentados**: 9 tests
- **Ubicación**: `app/src/androidTest/java/com/example/seedlife/`

---

## 🧪 Tests Unitarios

### 1. ValidationUtilsTest (21 tests)
**Archivo**: `app/src/test/java/com/example/seedlife/util/ValidationUtilsTest.kt`

#### Tests de Validación de Email (4 tests)
- ✅ `email válido debe pasar validación` - Verifica emails válidos
- ✅ `email inválido debe fallar validación` - Verifica emails con formato incorrecto
- ✅ `email vacío debe fallar validación` - Verifica emails vacíos o con solo espacios
- ✅ `email con espacios debe fallar validación` - Verifica emails con espacios

#### Tests de Validación de Contraseña (4 tests)
- ✅ `contraseña válida debe pasar validación` - Verifica contraseñas con longitud suficiente
- ✅ `contraseña corta debe fallar validación` - Verifica contraseñas menores a 6 caracteres
- ✅ `contraseña vacía debe fallar validación` - Verifica contraseñas vacías
- ✅ `contraseña con longitud mínima personalizada` - Verifica longitud personalizada

#### Tests de Validación de Nombre (4 tests)
- ✅ `nombre válido debe pasar validación` - Verifica nombres válidos
- ✅ `nombre corto debe fallar validación` - Verifica nombres menores a 2 caracteres
- ✅ `nombre vacío debe fallar validación` - Verifica nombres vacíos
- ✅ `nombre con espacios debe ser válido después de trim` - Verifica trim de espacios

#### Tests de Mensajes de Error (9 tests)
- ✅ `getEmailError retorna null para email válido`
- ✅ `getEmailError retorna mensaje para email vacío`
- ✅ `getEmailError retorna mensaje para email inválido`
- ✅ `getPasswordError retorna null para contraseña válida`
- ✅ `getPasswordError retorna mensaje para contraseña vacía`
- ✅ `getPasswordError retorna mensaje para contraseña corta`
- ✅ `getNameError retorna null para nombre válido`
- ✅ `getNameError retorna mensaje para nombre vacío`
- ✅ `getNameError retorna mensaje para nombre corto`

### 2. AuthViewModelTest (8 tests)
**Archivo**: `app/src/test/java/com/example/seedlife/ui/auth/AuthViewModelTest.kt`

#### Tests de Login (2 tests)
- ✅ `login exitoso actualiza estado a Success` - Verifica flujo exitoso de login
- ✅ `login fallido actualiza estado a Error` - Verifica manejo de errores en login

#### Tests de Registro (2 tests)
- ✅ `registro exitoso actualiza estado a Success` - Verifica flujo exitoso de registro
- ✅ `registro fallido actualiza estado a Error` - Verifica manejo de errores en registro

#### Tests de Guest (1 test)
- ✅ `enterAsGuest actualiza estado a Success con isGuest true` - Verifica modo invitado

#### Tests de SignOut (1 test)
- ✅ `signOut resetea estado a Idle` - Verifica cierre de sesión

#### Tests de ClearError (2 tests)
- ✅ `clearError resetea estado de Error a Idle` - Verifica limpieza de errores
- ✅ `clearError no hace nada si el estado no es Error` - Verifica que no afecta otros estados

### 3. ExampleUnitTest (1 test)
**Archivo**: `app/src/test/java/com/example/seedlife/ExampleUnitTest.kt`
- ✅ Test de ejemplo básico

---

## 📱 Tests Instrumentados (UI Tests)

### 1. AuthScreenTest (8 tests)
**Archivo**: `app/src/androidTest/java/com/example/seedlife/ui/auth/AuthScreenTest.kt`

#### Tests de Visualización (2 tests)
- ✅ `pantalla de login muestra campos de email y contraseña` - Verifica UI de login
- ✅ `pantalla de registro muestra campo de nombre adicional` - Verifica UI de registro

#### Tests de Validación de Botones (4 tests)
- ✅ `botón de login está deshabilitado cuando campos están vacíos`
- ✅ `botón de login se habilita cuando email y contraseña tienen contenido`
- ✅ `botón de registro está deshabilitado cuando nombre está vacío`
- ✅ `botón de registro se habilita cuando todos los campos están completos`

#### Tests de Navegación (2 tests)
- ✅ `cambiar entre login y registro actualiza la UI correctamente`
- ✅ `botón entrar como invitado está siempre habilitado cuando no hay loading`

### 2. ExampleInstrumentedTest (1 test)
**Archivo**: `app/src/androidTest/java/com/example/seedlife/ExampleInstrumentedTest.kt`
- ✅ Test de ejemplo básico

---

## 🚀 Cómo Ejecutar los Tests

### Prerrequisitos
- **JDK 11 o superior** (requerido por el proyecto)
- Android Studio instalado
- Emulador o dispositivo Android (para tests instrumentados)

### Opción 1: Desde Android Studio (Recomendado)

#### Ejecutar Tests Unitarios:
1. Abre Android Studio
2. Navega a `app/src/test/java/com/example/seedlife/`
3. Click derecho en la carpeta o archivo de test
4. Selecciona "Run Tests" o "Run 'Tests in...'"

#### Ejecutar Tests Instrumentados:
1. Asegúrate de tener un emulador o dispositivo conectado
2. Navega a `app/src/androidTest/java/com/example/seedlife/`
3. Click derecho en la carpeta o archivo de test
4. Selecciona "Run Tests" o "Run 'Tests in...'"

### Opción 2: Desde Terminal/Gradle

#### Tests Unitarios:
```bash
# Windows
.\gradlew.bat test

# Linux/Mac
./gradlew test
```

#### Tests Instrumentados:
```bash
# Windows (requiere emulador/dispositivo conectado)
.\gradlew.bat connectedAndroidTest

# Linux/Mac
./gradlew connectedAndroidTest
```

#### Ejecutar un test específico:
```bash
# Test unitario específico
.\gradlew.bat test --tests "com.example.seedlife.util.ValidationUtilsTest"

# Test instrumentado específico
.\gradlew.bat connectedAndroidTest --tests "com.example.seedlife.ui.auth.AuthScreenTest"
```

---

## 📦 Dependencias de Testing

Las siguientes dependencias están configuradas en `app/build.gradle.kts`:

### Tests Unitarios:
- **JUnit 4.13.2** - Framework de testing
- **MockK 1.13.8** - Mocking library para Kotlin
- **Kotlinx Coroutines Test 1.7.3** - Testing de coroutines
- **Turbine 1.0.0** - Testing de Flows

### Tests Instrumentados:
- **AndroidX Test JUnit 1.1.5** - Extensión de JUnit para Android
- **Espresso Core 3.5.1** - UI testing framework
- **Compose UI Test JUnit4** - Testing de Jetpack Compose

---

## ✅ Cobertura de Tests

### Funcionalidades Cubiertas:
- ✅ Validación de emails (formato, vacíos, espacios)
- ✅ Validación de contraseñas (longitud mínima, vacías)
- ✅ Validación de nombres (longitud mínima, vacíos)
- ✅ Mensajes de error para todas las validaciones
- ✅ Flujo completo de login (éxito y error)
- ✅ Flujo completo de registro (éxito y error)
- ✅ Modo invitado
- ✅ Cierre de sesión
- ✅ Manejo de errores
- ✅ UI de autenticación (campos, botones, navegación)

### Áreas que Podrían Necesitar Más Tests:
- 🔄 Tests de integración con Firebase (requieren configuración especial)
- 🔄 Tests de navegación completa entre pantallas
- 🔄 Tests de persistencia de datos
- 🔄 Tests de casos edge adicionales

---

## 🐛 Solución de Problemas

### Error: "No Java compiler found"
**Solución**: Configura JDK 11 o superior en Android Studio:
1. File → Project Structure → SDK Location
2. Selecciona JDK 11 o superior
3. Asegúrate de que JAVA_HOME apunte al JDK correcto

### Tests instrumentados no se ejecutan
**Solución**: 
1. Verifica que tengas un emulador o dispositivo conectado
2. Ejecuta `adb devices` para verificar conexión
3. Asegúrate de que el dispositivo tenga Android 7.0 (API 24) o superior

### Tests fallan con errores de MockK
**Solución**: 
1. Verifica que las dependencias estén sincronizadas
2. Ejecuta `.\gradlew.bat clean build` para limpiar el proyecto

---

## 📝 Notas Adicionales

- Los tests unitarios se ejecutan en la JVM local (rápidos)
- Los tests instrumentados requieren un dispositivo/emulador (más lentos)
- Los tests usan mocks para evitar dependencias de Firebase en tests unitarios
- Los tests de UI usan Compose Testing para interactuar con la interfaz

---

**Última actualización**: Tests implementados y listos para ejecución
**Total de tests**: 39 tests (30 unitarios + 9 instrumentados)
