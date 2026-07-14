# =====================================================================
#  Compila los 6 JAR que los Dockerfile van a copiar.
#  Correr SIEMPRE antes de "docker compose up --build".
#  Uso:  .\build-jars.ps1
# =====================================================================
$servicios = @("eureka-server", "api-gateway", "ms-auth", "ms-ventas", "ms-inventario", "ms-rrhh")
$raiz = $PSScriptRoot
$fallos = @()

foreach ($s in $servicios) {
    Write-Host ""
    Write-Host "=== Compilando $s ===" -ForegroundColor Cyan
    Push-Location (Join-Path $raiz $s)

    # -DskipTests: no hay tests y agilizamos el build
    mvn clean package -DskipTests -q

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK]   $s compilado" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] $s no compilo" -ForegroundColor Red
        $fallos += $s
    }
    Pop-Location
}

Write-Host ""
if ($fallos.Count -eq 0) {
    Write-Host "Los 6 JAR estan listos. Ahora: docker compose up --build" -ForegroundColor Cyan
} else {
    Write-Host ("Fallaron: " + ($fallos -join ", ")) -ForegroundColor Red
}
Write-Host ""
