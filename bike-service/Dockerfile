FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081 5005
ENV JAVA_DEBUG_OPTS=""
CMD java ${JAVA_DEBUG_OPTS} -jar app.jar
