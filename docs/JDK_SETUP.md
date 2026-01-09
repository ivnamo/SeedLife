# 🔧 Guía de Configuración de JDK 11

Este proyecto requiere **JDK 11 o superior** para compilar y ejecutar tests. Actualmente tu sistema tiene Java 8, por lo que necesitas actualizar.

## 📋 Opciones para Obtener JDK 11

### Opción 1: Usar el JDK de Android Studio (Recomendado)

Android Studio incluye un JDK embebido (JBR - JetBrains Runtime) que puedes usar:

1. **Ubicación típica del JDK de Android Studio:**
   ```
   C:\Users\34692\AppData\Local\Android\Android Studio\jbr
   ```

2. **Configurar en gradle.properties:**
   - Abre `gradle.properties`
   - Descomenta y ajusta esta línea:
   ```properties
   org.gradle.java.home=C:\\Users\\34692\\AppData\\Local\\Android\\Android Studio\\jbr
   ```

3. **O configurar en Android Studio:**
   - File → Project Structure → SDK Location
   - En "JDK location", selecciona el JDK de Android Studio

### Opción 2: Instalar JDK 11 Manualmente

#### Opción 2A: Oracle JDK 11 (Requiere cuenta Oracle)

1. **Descargar:**
   - Visita: https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
   - Descarga "Windows x64 Installer" para JDK 11

2. **Instalar:**
   - Ejecuta el instalador
   - Anota la ruta de instalación (típicamente: `C:\Program Files\Java\jdk-11.x.x`)

3. **Configurar variables de entorno:**
   - Panel de Control → Sistema → Configuración avanzada del sistema
   - Variables de entorno → Variables del sistema
   - Crear/editar `JAVA_HOME` = `C:\Program Files\Java\jdk-11.x.x`
   - Editar `Path` → Agregar `%JAVA_HOME%\bin`

#### Opción 2B: OpenJDK 11 (Gratis, Recomendado)

1. **Descargar desde Adoptium (Eclipse Temurin):**
   - Visita: https://adoptium.net/temurin/releases/?version=11
   - Selecciona:
     - Version: 11
     - Operating System: Windows
     - Architecture: x64
     - Package Type: JDK
   - Descarga el instalador `.msi`

2. **Instalar:**
   - Ejecuta el instalador `.msi`
   - Sigue el asistente (se configuran las variables automáticamente)
   - Anota la ruta de instalación (típicamente: `C:\Program Files\Eclipse Adoptium\jdk-11.x.x-hotspot`)

3. **Verificar instalación:**
   ```powershell
   java -version
   javac -version
   ```

4. **Configurar en gradle.properties:**
   ```properties
   org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-11.x.x-hotspot
   ```

#### Opción 2C: Microsoft Build of OpenJDK

1. **Descargar:**
   - Visita: https://www.microsoft.com/openjdk
   - Descarga JDK 11 para Windows

2. **Instalar y configurar** (similar a Opción 2B)

## ✅ Verificar la Configuración

### 1. Verificar versión de Java:
```powershell
java -version
```
Deberías ver algo como:
```
openjdk version "11.0.x" ...
```

### 2. Verificar JAVA_HOME:
```powershell
echo $env:JAVA_HOME
```

### 3. Probar Gradle:
```powershell
cd C:\Users\34692\Desktop\repos\SeedLife
.\gradlew.bat --version
```

### 4. Ejecutar tests:
```powershell
.\gradlew.bat test
```

## 🔧 Configuración en Android Studio

Si prefieres usar Android Studio para todo:

1. **File → Project Structure → SDK Location**
2. En "JDK location", selecciona:
   - El JDK embebido de Android Studio, O
   - La ruta a tu JDK 11 instalado manualmente

3. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
4. En "Gradle JDK", selecciona el mismo JDK

## 🚨 Solución de Problemas

### Error: "No Java compiler found"
**Solución:**
- Verifica que `JAVA_HOME` apunte a JDK 11
- Reinicia la terminal/Android Studio después de cambiar variables de entorno
- Verifica que `gradle.properties` tenga la ruta correcta

### Error: "Unsupported class file major version"
**Solución:**
- Asegúrate de usar JDK 11 o superior
- Limpia el proyecto: `.\gradlew.bat clean`

### Gradle sigue usando Java 8
**Solución:**
1. Detén el daemon de Gradle:
   ```powershell
   .\gradlew.bat --stop
   ```
2. Configura `org.gradle.java.home` en `gradle.properties`
3. Reinicia Android Studio

## 📝 Notas

- **JDK vs JRE**: Necesitas JDK (Java Development Kit), no solo JRE
- **Versión mínima**: JDK 11 es el mínimo requerido
- **Versión recomendada**: JDK 17 LTS o JDK 21 LTS (versiones más recientes)
- El proyecto está configurado para usar Java 11 en `build.gradle.kts`

## 🔗 Enlaces Útiles

- **Adoptium (OpenJDK)**: https://adoptium.net/
- **Oracle JDK**: https://www.oracle.com/java/
- **Microsoft OpenJDK**: https://www.microsoft.com/openjdk
- **Documentación Gradle**: https://docs.gradle.org/current/userguide/build_environment.html

---

**Recomendación**: Usa el JDK embebido de Android Studio (Opción 1) si solo desarrollas para Android. Es la opción más simple y no requiere instalación adicional.
