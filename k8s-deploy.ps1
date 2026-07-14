# =====================================================================
#  Despliega todo en Kubernetes (local o en la nube: es el mismo YAML).
#  Requisitos:
#    1) Kubernetes activado en Docker Desktop (Settings > Kubernetes)
#    2) .\build-jars.ps1        (los JAR)
#    3) .\k8s-build-images.ps1  (las imagenes)
#    4) k8s\01-secret.yaml creado a partir de 01-secret.yaml.example
# =====================================================================
$raiz = $PSScriptRoot
$k8s  = Join-Path $raiz "k8s"

if (-not (Test-Path (Join-Path $k8s "01-secret.yaml"))) {
    Write-Host ""
    Write-Host "FALTA k8s\01-secret.yaml" -ForegroundColor Red
    Write-Host "Copialo de la plantilla y pon las credenciales reales:" -ForegroundColor Yellow
    Write-Host "  copy k8s\01-secret.yaml.example k8s\01-secret.yaml" -ForegroundColor Yellow
    Write-Host "  notepad k8s\01-secret.yaml" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "=== Contexto actual de kubectl ===" -ForegroundColor Cyan
kubectl config current-context

Write-Host ""
Write-Host "=== Aplicando manifiestos ===" -ForegroundColor Cyan
kubectl apply -f (Join-Path $k8s "00-namespace.yaml")
kubectl apply -f $k8s

Write-Host ""
Write-Host "=== Estado (puede tardar 2-3 min en estabilizarse) ===" -ForegroundColor Cyan
kubectl get pods -n siderurgica

Write-Host ""
Write-Host "Seguir el arranque en vivo con:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n siderurgica -w" -ForegroundColor Yellow
Write-Host ""
Write-Host "Cuando todos digan Running 1/1, verificar con:" -ForegroundColor Yellow
Write-Host "  .\verificar-servicios.ps1 -Usuario admin -Password 123456" -ForegroundColor Yellow
Write-Host ""
