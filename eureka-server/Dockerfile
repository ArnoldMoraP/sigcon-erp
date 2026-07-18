# ============================================================================
#  eureka-server - Dockerfile multi-stage (FASE 5)
#
#  Antes: single-stage que hacia COPY target/*.jar -> exigia compilar el JAR
#  ANTES con build-jars.ps1 (Maven en el host). Si alguien clonaba el repo y
#  corria 'docker compose up --build' sin Maven/JDK, el build fallaba.
#
#  Ahora: la etapa 'build' compila el JAR DENTRO de Docker. Con esto
#  'docker compose up --build' funciona solo, sin Maven/JDK en el host y sin
#  correr build-jars.ps1. (build-jars.ps1 queda opcional, ya no es obligatorio.)
# ============================================================================

# ---------- Etapa 1: compilar con Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# 1) copiar solo el pom y bajar dependencias (capa cacheable: no se re-baja
#    en cada cambio de codigo, solo si cambia el pom.xml)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
# 2) copiar el codigo y empaquetar
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---------- Etapa 2: imagen final, solo el JRE + el JAR ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# La imagen alpine arranca la JVM con charset ASCII (POSIX), no UTF-8.
# Sin esto, los acentos se corrompen al leer de Postgres y al serializar a JSON
# ("Pacifico" sale como "PacAfico"). Estas 3 variables fuerzan UTF-8 en toda la JVM.
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# wget viene en la imagen alpine (busybox); lo usa el healthcheck del compose.
COPY --from=build /app/target/*-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
