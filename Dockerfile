# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies separately so code-only changes don't re-download the internet
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src src
RUN mvn -q -B -DskipTests clean package && \
    mv target/srcarcare-app.jar /app/app.jar

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -m srcarcare
COPY --from=build /app/app.jar /app/app.jar

# Persistent data directory - mount a volume here on the hosting platform
# so the H2 database file and uploaded photos survive restarts/redeploys.
RUN mkdir -p /data && chown -R srcarcare:srcarcare /data /app
ENV SRCARCARE_DATA_DIR=/data

USER srcarcare
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
