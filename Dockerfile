## === BUILD STAGE ===
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

COPY src src

RUN ./mvnw install -DskipTests

## === EXECUTION STAGE ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /workspace/app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]