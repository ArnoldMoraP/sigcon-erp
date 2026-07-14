# =====================================================================
#  Verificacion rapida de los 6 servicios - Caso Siderurgica Peru
#  Uso:  .\verificar-servicios.ps1 -Usuario admin -Password 123456
# =====================================================================
param(
    [string]$Usuario  = "admin",
    [string]$Password = "123456",
    [string]$Gateway  = "http://localhost:8080"
)

$ErrorActionPreference = "SilentlyContinue"

function Test-Puerto($nombre, $puerto) {
    $ok = Test-NetConnection -ComputerName localhost -Port $puerto -InformationLevel Quiet -WarningAction SilentlyContinue
    if ($ok) {
        Write-Host ("  [OK]   {0,-16} puerto {1}" -f $nombre, $puerto) -ForegroundColor Green
    } else {
        Write-Host ("  [FAIL] {0,-16} puerto {1} : el proceso NO esta arriba" -f $nombre, $puerto) -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== 1) Procesos escuchando en su puerto ===" -ForegroundColor Cyan
Test-Puerto "eureka-server"  8761
Test-Puerto "api-gateway"    8080
Test-Puerto "ms-auth"        8081
Test-Puerto "ms-ventas"      8082
Test-Puerto "ms-inventario"  8083
Test-Puerto "ms-rrhh"        8084
Test-Puerto "mysql-rrhh"     3306

Write-Host ""
Write-Host "=== 2) Registrados en Eureka ===" -ForegroundColor Cyan
try {
    $r = Invoke-RestMethod "http://localhost:8761/eureka/apps" -Headers @{Accept="application/json"}
    $registrados = @($r.applications.application | ForEach-Object { $_.name })
    foreach ($esperado in @("API-GATEWAY","MS-AUTH","MS-VENTAS","MS-INVENTARIO","MS-RRHH")) {
        if ($registrados -contains $esperado) {
            Write-Host ("  [OK]   {0} registrado" -f $esperado) -ForegroundColor Green
        } else {
            Write-Host ("  [FAIL] {0} NO aparece en Eureka" -f $esperado) -ForegroundColor Red
        }
    }
} catch {
    Write-Host "  [FAIL] Eureka no responde en el puerto 8761" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 3) Login por el gateway (JWT) ===" -ForegroundColor Cyan
$token = $null
try {
    $body  = @{ username = $Usuario; password = $Password } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$Gateway/auth/login" -Method Post -Body $body -ContentType "application/json"
    $token = $login.token
    if (-not $token) { $token = $login.accessToken }
    if ($token) {
        Write-Host "  [OK]   JWT obtenido" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Login respondio pero no encuentro el campo del token. Revisa el JSON de respuesta." -ForegroundColor Yellow
    }
} catch {
    Write-Host ("  [FAIL] No se pudo hacer login: {0}" -f $_.Exception.Message) -ForegroundColor Red
}

if (-not $token) {
    Write-Host ""
    Write-Host "Sin token no puedo probar los endpoints de negocio." -ForegroundColor Yellow
    Write-Host ""
    exit
}
$h = @{ Authorization = "Bearer $token" }

Write-Host ""
Write-Host "=== 4) Endpoints de negocio via gateway (con JWT) ===" -ForegroundColor Cyan
$rutas = @(
    @{ ms = "ms-ventas";     url = "$Gateway/cotizaciones" },
    @{ ms = "ms-ventas";     url = "$Gateway/pedidos" },
    @{ ms = "ms-ventas";     url = "$Gateway/ventas" },
    @{ ms = "ms-ventas";     url = "$Gateway/despacho" },
    @{ ms = "ms-inventario"; url = "$Gateway/inventario" },
    @{ ms = "ms-inventario"; url = "$Gateway/proveedores" },
    @{ ms = "ms-inventario"; url = "$Gateway/ordenes-compra" },
    @{ ms = "ms-rrhh";       url = "$Gateway/rrhh/empleados" },
    @{ ms = "ms-rrhh";       url = "$Gateway/rrhh/planillas" }
)
foreach ($ruta in $rutas) {
    try {
        $resp = Invoke-WebRequest -Uri $ruta.url -Headers $h -Method Get -UseBasicParsing
        Write-Host ("  [OK]   {0,-14} {1}  ({2})" -f $ruta.ms, $ruta.url, $resp.StatusCode) -ForegroundColor Green
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        Write-Host ("  [FAIL] {0,-14} {1}  ({2})" -f $ruta.ms, $ruta.url, $code) -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== 5) Comunicacion REST entre microservicios (prueba clave) ===" -ForegroundColor Cyan
Write-Host "  Valida que ms-ventas puede llamar a ms-inventario via Eureka." -ForegroundColor Gray
try {
    $inv = Invoke-RestMethod -Uri "$Gateway/inventario" -Headers $h
    if (-not $inv -or $inv.Count -eq 0) {
        Write-Host "  [WARN] almacen.inventario esta vacio. No se puede probar el descuento de stock." -ForegroundColor Yellow
    } else {
        $p = $inv[0]
        Write-Host ("  Producto de prueba: '{0}' (stock actual: {1})" -f $p.producto, $p.stock) -ForegroundColor Gray

        # Prueba negativa: pedir mas stock del que hay. Debe responder 400.
        $bodyNeg = @{ producto = $p.producto; cantidad = ([int]$p.stock + 9999) } | ConvertTo-Json
        try {
            Invoke-RestMethod -Uri "$Gateway/inventario/descontar-stock" -Method Patch -Body $bodyNeg -ContentType "application/json" -Headers $h | Out-Null
            Write-Host "  [FAIL] El descuento con stock insuficiente NO fue rechazado" -ForegroundColor Red
        } catch {
            $code = $_.Exception.Response.StatusCode.value__
            if ($code -eq 400) {
                Write-Host "  [OK]   Stock insuficiente rechazado con 400 (contrato correcto)" -ForegroundColor Green
            } else {
                Write-Host ("  [FAIL] Esperaba 400 y recibi {0}" -f $code) -ForegroundColor Red
            }
        }

        # Prueba positiva: descontar 1 unidad real.
        $bodyPos = @{ producto = $p.producto; cantidad = 1 } | ConvertTo-Json
        try {
            $res = Invoke-RestMethod -Uri "$Gateway/inventario/descontar-stock" -Method Patch -Body $bodyPos -ContentType "application/json" -Headers $h
            Write-Host ("  [OK]   Descuento aplicado: stockNuevo={0} bajoStock={1}" -f $res.stockNuevo, $res.bajoStock) -ForegroundColor Green
            Write-Host "         OJO: descuenta 1 unidad real. Repon con PATCH /inventario/{id}/reponer" -ForegroundColor Gray
        } catch {
            Write-Host ("  [FAIL] El descuento de stock fallo: {0}" -f $_.Exception.Message) -ForegroundColor Red
        }
    }
} catch {
    Write-Host "  [FAIL] No pude leer /inventario" -ForegroundColor Red
}

Write-Host ""
Write-Host "Listo. Todo en verde = los 6 servicios corriendo e integrados." -ForegroundColor Cyan
Write-Host ""