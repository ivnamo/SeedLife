# Script de PowerShell para verificar y configurar JDK 11
# Ejecutar: .\setup-jdk.ps1

Write-Host "=== Verificación de JDK ===" -ForegroundColor Cyan
Write-Host ""

# Verificar versión actual de Java
Write-Host "Versión actual de Java:" -ForegroundColor Yellow
java -version 2>&1
Write-Host ""

# Verificar JAVA_HOME
Write-Host "JAVA_HOME actual:" -ForegroundColor Yellow
if ($env:JAVA_HOME) {
    Write-Host $env:JAVA_HOME -ForegroundColor Green
} else {
    Write-Host "No configurado" -ForegroundColor Red
}
Write-Host ""

# Buscar JDK 11 o superior
Write-Host "=== Buscando JDK 11 o superior ===" -ForegroundColor Cyan
Write-Host ""

$jdkPaths = @(
    "$env:LOCALAPPDATA\Android\Android Studio\jbr",
    "$env:ProgramFiles\Java\jdk-11*",
    "$env:ProgramFiles\Java\jdk-17*",
    "$env:ProgramFiles\Java\jdk-21*",
    "$env:ProgramFiles\Eclipse Adoptium\jdk-11*",
    "$env:ProgramFiles\Eclipse Adoptium\jdk-17*",
    "$env:ProgramFiles\Eclipse Adoptium\jdk-21*",
    "$env:ProgramFiles (x86)\Java\jdk-11*",
    "$env:ProgramFiles (x86)\Java\jdk-17*",
    "$env:ProgramFiles (x86)\Java\jdk-21*"
)

$foundJdks = @()

foreach ($path in $jdkPaths) {
    $resolved = Resolve-Path $path -ErrorAction SilentlyContinue
    if ($resolved) {
        foreach ($jdk in $resolved) {
            $javaExe = Join-Path $jdk "bin\java.exe"
            if (Test-Path $javaExe) {
                $version = & $javaExe -version 2>&1 | Select-Object -First 1
                $foundJdks += [PSCustomObject]@{
                    Path = $jdk
                    Version = $version
                }
            }
        }
    }
}

if ($foundJdks.Count -gt 0) {
    Write-Host "JDKs encontrados:" -ForegroundColor Green
    $index = 1
    foreach ($jdk in $foundJdks) {
        Write-Host "$index. $($jdk.Path)" -ForegroundColor White
        Write-Host "   Versión: $($jdk.Version)" -ForegroundColor Gray
        $index++
    }
    Write-Host ""
    
    # Sugerir el primero encontrado
    $suggestedJdk = $foundJdks[0].Path
    Write-Host "JDK sugerido para usar:" -ForegroundColor Yellow
    Write-Host $suggestedJdk -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Para configurarlo en gradle.properties, agrega:" -ForegroundColor Cyan
    Write-Host "org.gradle.java.home=$($suggestedJdk.Replace('\', '\\'))" -ForegroundColor White
} else {
    Write-Host "No se encontró JDK 11 o superior instalado." -ForegroundColor Red
    Write-Host ""
    Write-Host "Opciones:" -ForegroundColor Yellow
    Write-Host "1. Usar el JDK de Android Studio (si está instalado)" -ForegroundColor White
    Write-Host "2. Descargar e instalar JDK 11 desde:" -ForegroundColor White
    Write-Host "   https://adoptium.net/temurin/releases/?version=11" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Ver docs/JDK_SETUP.md para más detalles." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Verificación de Gradle ===" -ForegroundColor Cyan
if (Test-Path ".\gradlew.bat") {
    Write-Host "Ejecutando: .\gradlew.bat --version" -ForegroundColor Yellow
    & .\gradlew.bat --version 2>&1 | Select-Object -First 5
} else {
    Write-Host "gradlew.bat no encontrado en el directorio actual." -ForegroundColor Red
}
