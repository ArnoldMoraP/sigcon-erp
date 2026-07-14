# =====================================================================
#  Construye las 6 imagenes con nombres limpios (siderurgica/xxx:1.0),
#  que es como las referencian los YAML de k8s/.
#  Requiere que los JAR ya existan:  .\build-jars.ps1
# =====================================================================
$servicios = @("eureka-server", "api-gateway", "ms-auth", "ms-ventas", "ms-inventario", "ms-rrhh")
$raiz = $PSScriptRoot
$fallos = @()

foreach ($s in $servicios) {
    Write-Host ""
    Write-Host "=== Construyendo imagen siderurgica/$s`:1.0 ===" -ForegroundColor Cyan
    $ruta = Join-Path $raiz $s

    if (-not (Test-Path (Join-Path $ruta "target"))) {
        Write-Host "  [FAIL] no existe $s\target : corre primero .\build-jars.ps1" -ForegroundColor Red
        $fallos += $s
        continue
    }

    docker build -t "siderurgica/$s`:1.0" $ruta

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK]   siderurgica/$s`:1.0" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] $s" -ForegroundColor Red
        $fallos += $s
    }
}

Write-Host ""
if ($fallos.Count -eq 0) {
    Write-Host "Las 6 imagenes estan listas. Ahora: .\k8s-deploy.ps1" -ForegroundColor Cyan
} else {
    Write-Host ("Fallaron: " + ($fallos -join ", ")) -ForegroundColor Red
}
Write-Host ""
