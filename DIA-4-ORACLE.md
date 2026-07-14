# Despliegue en Oracle OKE

Tus datos (ya verificados):

| Dato | Valor |
|---|---|
| Región | US East (Ashburn) → código `iad` |
| Registro (OCIR) | `iad.ocir.io` |
| Namespace | `idxarx3sn3vb` |
| Usuario de login | `idxarx3sn3vb/juniorgamio14@gmail.com` |
| Ruta de imágenes | `iad.ocir.io/idxarx3sn3vb/siderurgica/<servicio>:1.0` |
| Clúster | `siderurgica-cluster` (ACTIVE) |

`k8s-oracle/` son **los mismos YAML** que `k8s/`, con dos únicos cambios:
`image:` apunta a OCIR, e `imagePullPolicy: Always` (en vez de `IfNotPresent`).
Todo lo demás — StatefulSet, PVC, Secrets, probes, initContainers — es idéntico.

---

## 1. Subir las imágenes a OCIR

```powershell
docker login iad.ocir.io      # ya lo hiciste: Login Succeeded
.\ocir-push.ps1
```

Sube las 6. Tarda unos minutos (son ~200 MB cada una).

## 2. Hacer públicos los repositorios

Por defecto OCIR crea los repos **privados**, y entonces el clúster no puede descargarlos
(los pods quedarían en `ImagePullBackOff`). Hay dos caminos; el simple es hacerlos públicos:

Consola de Oracle → ☰ → **Developer Services** → **Container Registry**

Por cada uno de los 6 repos (`siderurgica/eureka-server`, `siderurgica/ms-auth`, etc.):
→ seleccionarlo → **Actions** → **Change to public**

> La alternativa "correcta" para producción sería crear un `imagePullSecret` con el Auth Token
> y referenciarlo en cada Deployment. Para un proyecto de clase, público es suficiente y evita
> una fuente entera de errores.

## 3. Apuntar kubectl al clúster de Oracle

Necesitas el **OCID del clúster** (consola → Kubernetes Clusters → tu clúster → botón **Copy** junto a Cluster ID):

```powershell
oci ce cluster create-kubeconfig `
  --cluster-id ocid1.cluster.oc1.iad.PEGA_AQUI_EL_OCID_COMPLETO `
  --file $HOME/.kube/config `
  --region us-ashburn-1 `
  --token-version 2.0.0 `
  --kube-endpoint PUBLIC_ENDPOINT
```

Esto **añade** el contexto de Oracle sin borrar el de `docker-desktop`. Verifica:

```powershell
kubectl config get-contexts       # deben aparecer los dos
kubectl get nodes                 # los 2 nodos de Oracle en Ready
```

Para saltar entre clústeres:

```powershell
kubectl config use-context docker-desktop     # local
kubectl config use-context <contexto-oracle>  # Oracle
```

## 4. El Secret

```powershell
copy k8s\01-secret.yaml k8s-oracle\01-secret.yaml
```

Es el mismo (mismas credenciales de Neon y mismo `JWT_SECRET`). Ya está en el `.gitignore`.

## 5. Desplegar

```powershell
.\oracle-deploy.ps1
kubectl get pods -n siderurgica -w
```

El script se niega a correr si `kubectl` está apuntando a `docker-desktop`, para que no
despliegues en el sitio equivocado por error.

## 6. La IP pública

Oracle provisiona un Load Balancer real. Tarda **2-5 minutos** en asignar la IP:

```powershell
kubectl get svc api-gateway -n siderurgica -w
```

Cuando `EXTERNAL-IP` deje de decir `<pending>` y muestre una IP:

```powershell
.\verificar-servicios.ps1 -Gateway http://<LA-IP>:8080 -Usuario admin -Password 123456
```

> Los pasos 1 y 2 del script darán FAIL (los puertos internos no están expuestos, y eso es
> lo correcto). Lo que importa son los pasos 3, 4 y 5.

## 7. El frontend

En `environment.ts` del Angular local, apunta `apiUrl` a `http://<LA-IP>:8080` y corre `ng serve`.

**No uses el frontend de Vercel** contra esta IP: Vercel sirve por HTTPS y el navegador bloquea
las llamadas a un backend HTTP plano (*mixed content*). Por eso la demo va con `ng serve` local.

---

## Problemas frecuentes

- **`ImagePullBackOff`** → los repos de OCIR siguen privados. Paso 2.
- **Pods en `Pending`** → no hay recursos en los nodos. `kubectl describe pod <pod> -n siderurgica`
  lo dice al final. Solución: bajar los `resources.requests` en los YAML.
- **`EXTERNAL-IP` en `<pending>` para siempre** → revisa que el clúster tenga la subnet de
  Load Balancer (en tu caso sí: `oke-svclbsubnet-quick-siderurgica-cluster-...`).
- **`Unauthorized` al usar kubectl** → el token del kubeconfig expiró. Vuelve a correr el
  `create-kubeconfig` del paso 3.

---

## ⚠️ Al terminar el proyecto: BORRA EL CLÚSTER

Los 2 nodos `VM.Standard.E3.Flex` consumen crédito continuamente (~$2-4 por día).

Consola → **Kubernetes Clusters** → tu clúster → **Delete**

Y borra también el Load Balancer si queda huérfano (Networking → Load Balancers).
