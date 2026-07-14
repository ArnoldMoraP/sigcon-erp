# Día 4 — Kubernetes

Los mismos YAML sirven para **Kubernetes local (Docker Desktop)** y para **Oracle OKE**.
Primero lo dejamos corriendo local (asegura el requisito), después se despliega a Oracle.

---

## Qué hay en `k8s/`

| Archivo | Recurso | Por qué |
|---|---|---|
| `00-namespace.yaml` | Namespace `siderurgica` | Aísla el proyecto del resto del clúster |
| `01-secret.yaml.example` | Secret (plantilla) | Credenciales de Neon + `JWT_SECRET` + MySQL. **El real no se commitea** |
| `02-configmap.yaml` | ConfigMap | `EUREKA_URL` (config no sensible) |
| `03-mysql-init-configmap.yaml` | ConfigMap | El `init-mysql.sql` con las 6 tablas de RRHH |
| `04-mysql-statefulset.yaml` | **StatefulSet + PVC** + Service headless | MySQL con almacenamiento persistente |
| `05-eureka.yaml` | Deployment + Service | Service discovery |
| `06` a `09` | Deployment + Service | Los 4 microservicios |
| `10-api-gateway.yaml` | Deployment + **Service LoadBalancer** | La única puerta de entrada |

### Decisiones que conviene saber explicar

- **MySQL es `StatefulSet`, no `Deployment`.** Una BD tiene estado: necesita identidad de red estable
  y su propio volumen. El `volumeClaimTemplate` genera un **PersistentVolumeClaim** cuyo contenido
  sobrevive al reinicio del pod. Es la parte que las rúbricas más valoran.
- **`initContainer` esperando a Eureka.** Ningún microservicio arranca hasta que Eureka responda en
  el 8761. Es el equivalente en K8s del `depends_on: service_healthy` de Docker Compose.
  `ms-rrhh` tiene además un segundo initContainer que espera a MySQL.
- **Probes `tcpSocket`, no `httpGet /actuator/health`.** Los microservicios **no** tienen la
  dependencia `spring-boot-starter-actuator`: un `httpGet` a `/actuator/health` daría 404 y
  Kubernetes reiniciaría los pods en bucle. Eureka sí tiene actuator, así que ese usa `httpGet`.
- **Solo el gateway es `LoadBalancer`.** Todo lo demás es `ClusterIP` (interno). Misma arquitectura
  que ya tenían: una sola puerta de entrada con validación JWT.
- **Los servicios se llaman por su nombre de Service** (`eureka-server`, `mysql-rrhh`), igual que en
  Docker Compose se llamaban por el nombre del contenedor. Por eso `${EUREKA_URL}` sigue funcionando.

---

## Parte A — Kubernetes local (Docker Desktop)

### 1. Activar Kubernetes

Docker Desktop → ⚙️ Settings → **Kubernetes** → marcar **Enable Kubernetes** → **Apply & Restart**.
Esperar a que el indicador quede en verde. Verificar:

```powershell
kubectl config current-context     # debe decir: docker-desktop
kubectl get nodes                  # un nodo en Ready
```

> Si `kubectl config current-context` dice otra cosa: `kubectl config use-context docker-desktop`

### 2. Crear el Secret real

```powershell
copy k8s\01-secret.yaml.example k8s\01-secret.yaml
notepad k8s\01-secret.yaml
```

Poner los mismos valores del `.env` (`DB_PASSWORD` y `JWT_SECRET`). Ya está en el `.gitignore`.

### 3. Bajar Docker Compose

Kubernetes va a usar los mismos puertos:

```powershell
docker compose down
```

### 4. Construir las imágenes

Los JAR ya existen; solo hay que reetiquetar las imágenes con el nombre que usan los YAML:

```powershell
.\build-jars.ps1          # si cambiaste código; si no, se puede saltar
.\k8s-build-images.ps1    # crea siderurgica/ms-auth:1.0, etc.
```

> `imagePullPolicy: IfNotPresent` hace que Kubernetes use la imagen local sin intentar
> descargarla de un registro. Por eso funciona sin subir nada a internet.

### 5. Desplegar

```powershell
.\k8s-deploy.ps1
```

Seguir el arranque:

```powershell
kubectl get pods -n siderurgica -w
```

Tarda 2-3 minutos. Verás los pods en `Init:0/1` (esperando a Eureka) y luego pasando a `Running 1/1`.
`Ctrl+C` para salir del modo watch.

### 6. Verificar

```powershell
kubectl get pods -n siderurgica
kubectl get svc -n siderurgica
.\verificar-servicios.ps1 -Usuario admin -Password 123456
```

El script funciona igual porque el `LoadBalancer` del gateway queda en `localhost:8080`.

> Los puertos 8081-8084 y 8761 **no** están expuestos (son `ClusterIP`), así que el paso 1 del
> script mostrará FAIL en esos. **Es correcto y es lo deseable**: en Kubernetes solo el gateway
> debe ser accesible desde fuera. Lo que importa son los pasos 3, 4 y 5.
> Para ver el dashboard de Eureka: `kubectl port-forward -n siderurgica svc/eureka-server 8761:8761`

---

## Comandos útiles

```powershell
kubectl get pods -n siderurgica                    # estado
kubectl get all -n siderurgica                     # todo
kubectl get pvc -n siderurgica                     # el volumen de MySQL
kubectl logs -n siderurgica deploy/ms-inventario   # logs de un servicio
kubectl logs -n siderurgica statefulset/mysql-rrhh # logs de MySQL
kubectl describe pod -n siderurgica <pod>          # por qué un pod no arranca
kubectl rollout restart -n siderurgica deploy/ms-ventas   # reiniciar uno
kubectl delete namespace siderurgica               # borrar todo
```

### Si un pod no arranca

- **`ImagePullBackOff`** → la imagen no existe localmente. Corre `.\k8s-build-images.ps1`.
- **`Init:0/1` eternamente** → el initContainer no ve a Eureka. `kubectl logs <pod> -c esperar-eureka`
- **`CrashLoopBackOff`** → la app arranca y muere. `kubectl logs <pod>`. Suele ser una variable de
  entorno mal puesta en el Secret.
- **`Pending`** → falta CPU/memoria en el nodo. En Docker Desktop: subir los recursos asignados
  (Settings → Resources) a al menos 6 GB de RAM.

---

## Evidencia para la entrega

1. `kubectl get pods -n siderurgica` — todos `Running`
2. `kubectl get svc -n siderurgica` — el gateway como `LoadBalancer`
3. `kubectl get pvc -n siderurgica` — el volumen persistente de MySQL
4. `verificar-servicios.ps1` en verde **sobre Kubernetes**
5. Postman: flujo completo contra el gateway

---

## Parte B — Oracle OKE (cuando la cuenta esté lista)

Los YAML son **los mismos**. Solo cambian tres cosas:

1. **Crear el clúster** en la consola de OCI (Kubernetes → Clusters → Create → Quick Create).
2. **Subir las imágenes a OCIR** (el registro de Oracle), y en los YAML cambiar
   `image: siderurgica/ms-auth:1.0` por la ruta completa de OCIR
   (`<region>.ocir.io/<tenancy>/siderurgica/ms-auth:1.0`), además de `imagePullPolicy: Always`
   y un `imagePullSecret`.
3. **`kubectl config`** apuntando al clúster de Oracle en vez de a `docker-desktop`.

El `LoadBalancer` del gateway pasa de `localhost` a una **IP pública real** que provisiona Oracle,
y esa es la IP que iría en el `environment.prod.ts` del frontend.

Todo lo demás — StatefulSet, PVC, Secrets, ConfigMaps, probes, initContainers — queda idéntico.
