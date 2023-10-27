
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn clean install -DskipTests

#
# Package stage
#
FROM openjdk:11.0-jdk
COPY --from=build /home/app/target/electricity-backend-0.0.1-SNAPSHOT.jar /usr/app/app.jar
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]
