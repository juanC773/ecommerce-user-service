FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre
ARG PROJECT_VERSION=0.1.0
WORKDIR /app
COPY --from=build /app/target/user-service-*.jar user-service.jar
ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8700
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "user-service.jar"]


