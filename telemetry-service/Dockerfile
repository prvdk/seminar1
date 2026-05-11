# Build stage
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace

COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY src src

RUN gradle bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

USER appuser
EXPOSE 8082
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
