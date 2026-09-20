# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies separately so code-only changes don't re-download the internet
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src src
RUN mvn -q -B clean package && \
    mv target/srcarcare-app.jar /app/app.jar

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -m srcarcare
COPY --from=build /app/app.jar /app/app.jar

# Railway mounts persistent volumes as root. Startup prepares the writable
# directories, then drops privileges before launching the application.
RUN mkdir -p /data && chown -R srcarcare:srcarcare /data /app
ENV SRCARCARE_DATA_DIR=/data

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "mkdir -p /data/db /data/uploads && chown -R srcarcare:srcarcare /data && exec runuser -u srcarcare -- java -jar /app/app.jar"]
