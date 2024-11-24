FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY src ./src
COPY gradle/wrapper ./gradle/wrapper

RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:17-jre

COPY --from=build /build/libs/*.jar KudaGoApp.jar

ENTRYPOINT ["java", "-jar", "KudaGoApp.jar"]