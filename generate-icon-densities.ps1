# Script para generar versiones del icono en diferentes densidades
# Requiere ImageMagick instalado: https://imagemagick.org/script/download.php
# O puedes usar Android Studio Image Asset Studio: Right-click res > New > Image Asset

Write-Host "Generador de iconos para Android" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
Write-Host ""

$sourceImage = "app\src\main\res\drawable\planta.png"

if (-not (Test-Path $sourceImage)) {
    Write-Host "Error: No se encontró $sourceImage" -ForegroundColor Red
    exit 1
}

Write-Host "Imagen fuente: $sourceImage" -ForegroundColor Yellow
Write-Host ""

# Tamaños para Adaptive Icon foreground (108dp = 432px para xxxhdpi)
# Los tamaños se calculan para el área segura del icono (72dp)
$sizes = @{
    "mdpi" = 108    # 1x
    "hdpi" = 162    # 1.5x
    "xhdpi" = 216   # 2x
    "xxhdpi" = 324  # 3x
    "xxxhdpi" = 432 # 4x
}

# Verificar si ImageMagick está instalado
$magick = Get-Command magick -ErrorAction SilentlyContinue

if (-not $magick) {
    Write-Host "ImageMagick no está instalado." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opciones:" -ForegroundColor Cyan
    Write-Host "1. Instalar ImageMagick desde: https://imagemagick.org/script/download.php"
    Write-Host "2. Usar Android Studio Image Asset Studio:" -ForegroundColor Cyan
    Write-Host "   - Right-click en 'res' > New > Image Asset"
    Write-Host "   - Selecciona 'Launcher Icons (Adaptive and Legacy)'"
    Write-Host "   - Foreground Layer: Selecciona planta.png"
    Write-Host "   - Background Layer: Color #F1F8E9"
    Write-Host ""
    exit 0
}

Write-Host "Generando versiones en diferentes densidades..." -ForegroundColor Green
Write-Host ""

foreach ($density in $sizes.Keys) {
    $size = $sizes[$density]
    $outputDir = "app\src\main\res\drawable-$density"
    $outputFile = "$outputDir\ic_launcher_foreground.png"
    
    # Crear directorio si no existe
    if (-not (Test-Path $outputDir)) {
        New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    }
    
    Write-Host "Generando $density (${size}x${size}px)..." -ForegroundColor Yellow
    
    # Redimensionar y centrar la imagen
    magick $sourceImage -resize "${size}x${size}" -gravity center -extent "${size}x${size}" $outputFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ Creado: $outputFile" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Error al crear $outputFile" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "¡Completado!" -ForegroundColor Green
Write-Host ""
Write-Host "Nota: Los Adaptive Icons usan el archivo XML en mipmap-anydpi-v26" -ForegroundColor Cyan
Write-Host "que ya está configurado para usar ic_launcher_foreground." -ForegroundColor Cyan
