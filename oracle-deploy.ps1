# =====================================================================
#  Despliega en Oracle OKE.
#
#  Antes de correr esto:
#    1) .\ocir-push.ps1                    (las 6 imagenes en OCIR)
#    2) los repos de OCIR puestos como PUBLIC en la consola
#    3) kubeconfig de Oracle generado (ver DIA-4-ORACLE.md, paso 3)
#    4) k8s-oracle\01-secret.yaml creado con las credenciales reales
# =====================================================================
$raiz = $PSScriptRoot
$k8s  = Join-Path $raiz "k8s-oracle"

if (-not (Test-Path (Join-Path $k8s "01-secret.yaml"))) {
    Write-Host ""
    Write-Host "FALTA k8s-oracle\01-secret.yaml" -ForegroundColor Red
    Write-Host "  copy k8s\01-secret.yaml k8s-oracle\01-secret.yaml" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "=== Contexto de kubectl ===" -ForegroundColor Cyan
$ctx = kubectl config current-context
Write-Host "  $ctx"

if ($ctx -like "*docker-desktop*") {
    Write-Host ""
    Write-Host "CUIDADO: estas apuntando a Kubernetes LOCAL, no a Oracle." -ForegroundColor Red
    Write-Host "Cambia de contexto con:" -ForegroundColor Yellow
    Write-Host "  kubectl config get-contexts" -ForegroundColor Yellow
    Write-Host "  kubectl config use-context <el-contexto-de-oracle>" -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "=== Nodos del cluster ===" -ForegroundColor Cyan
kubectl get nodes

Write-Host ""
Write-Host "=== Aplicando manifiestos ===" -ForegroundColor Cyan
kubectl apply -f (Join-Path $k8s "00-namespace.yaml")
kubectl apply -f $k8s

Write-Host ""
Write-Host "=== Pods ===" -ForegroundColor Cyan
kubectl get pods -n siderurgica

Write-Host ""
Write-Host "Seguir el arranque:" -ForegroundColor Yellow
Write-Host "  kubectl get pods -n siderurgica -w" -ForegroundColor Yellow
Write-Host ""
Write-Host "La IP PUBLICA del gateway tarda 2-5 min en aparecer (Oracle crea un Load Balancer):" -ForegroundColor Yellow
Write-Host "  kubectl get svc api-gateway -n siderurgica -w" -ForegroundColor Yellow
Write-Host ""
Write-Host "Cuando EXTERNAL-IP deje de decir <pending>, probar:" -ForegroundColor Yellow
Write-Host "  .\verificar-servicios.ps1 -Gateway http://<IP>:8080 -Usuario admin -Password 123456" -ForegroundColor Yellow
Write-Host ""
