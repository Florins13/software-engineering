FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -Dquarkus.package.jar.type=fast-jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ ./
EXPOSE 8082
CMD ["java", "-jar", "quarkus-run.jar"]
