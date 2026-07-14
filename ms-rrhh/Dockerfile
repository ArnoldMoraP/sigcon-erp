FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# La imagen alpine arranca la JVM con charset ASCII (POSIX), no UTF-8.
# Sin esto, los acentos se corrompen al leer de Postgres y al serializar a JSON
# ("Pacifico" sale como "PacAfico"). Estas 3 variables fuerzan UTF-8 en toda la JVM.
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

COPY target/ms-rrhh-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
