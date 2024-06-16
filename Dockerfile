FROM openjdk:17-slim AS builder
WORKDIR /app

COPY ./target/*.jar ./app.jar

RUN java -Djarmode=layertools -jar app.jar extract

ENV JAVA_TOOL_OPTIONS="-Xmx512m -Dserver.port=8080 -Djava.io.tmpdir=/dev/shm -Djava.security.egd=file:/dev/./urandom"

COPY --from=builder app/dependencies/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/internal-dependencies/ ./
COPY --from=builder app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
