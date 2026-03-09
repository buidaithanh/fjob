# FROM maven:3-openjdk-17 AS build
# WORKDIR /app

# COPY . .
# RUN mvn clean package -DskipTests


# # Run stage

# FROM openjdk:17-jdk-slim
# WORKDIR /app

# COPY --from=build /app/target/fjob-0.0.1-SNAPSHOT.war fjob.war
# EXPOSE 8080 

# ENTRYPOINT ["java","-jar","fjob.war"]

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/*.war app.war

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.war"]