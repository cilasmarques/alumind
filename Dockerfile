FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copia os arquivos de configuração do Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copia o código-fonte
COPY src src

# Compila a aplicação
RUN ./mvnw install -DskipTests

# Estágio final com apenas o JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]