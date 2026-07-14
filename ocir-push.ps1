# =====================================================================
#  Sube las 6 imagenes a OCIR (Oracle Cloud Infrastructure Registry).
#
#  Requisitos previos:
#    - .\build-jars.ps1  y  .\k8s-build-images.ps1  ya ejecutados
#    - docker login iad.ocir.io  con Login Succeeded
# =====================================================================
$REGISTRO  = "iad.ocir.io"
$NAMESPACE = "idxarx3sn3vb"
$PROYECTO  = "siderurgica"

$servicios = @("eureka-server", "api-gateway", "ms-auth", "ms-ventas", "ms-inventario", "ms-rrhh")
$fallos = @()

foreach ($s in $servicios) {
    $local  = "siderurgica/$s`:1.0"
    $remoto = "$REGISTRO/$NAMESPACE/$PROYECTO/$s`:1.0"

    Write-Host ""
    Write-Host "=== $s ===" -ForegroundColor Cyan
    Write-Host "  $local  ->  $remoto" -ForegroundColor Gray

    docker tag $local $remoto
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [FAIL] no existe la imagen local $local" -ForegroundColor Red
        Write-Host "         corre primero: .\k8s-build-images.ps1" -ForegroundColor Yellow
        $fallos += $s
        continue
    }

    docker push $remoto
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK]   subida" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] no se pudo subir" -ForegroundColor Red
        $fallos += $s
    }
}

Write-Host ""
if ($fallos.Count -eq 0) {
    Write-Host "Las 6 imagenes estan en OCIR." -ForegroundColor Cyan
    Write-Host "IMPORTANTE: en la consola de Oracle, ponlas como PUBLIC:" -ForegroundColor Yellow
    Write-Host "  Developer Services > Container Registry > cada repo > Actions > Change to public" -ForegroundColor Yellow
    Write-Host "Asi el cluster las descarga sin necesitar un imagePullSecret." -ForegroundColor Yellow
} else {
    Write-Host ("Fallaron: " + ($fallos -join ", ")) -ForegroundColor Red
}
Write-Host ""
