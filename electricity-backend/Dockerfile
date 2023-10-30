


# Stage 1: Build ứng dụng
FROM maven:3.6-jdk-11 AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Tạo hình ảnh chứa ứng dụng đã build
FROM openjdk:11
COPY --from=build target/*.jar /usr/app/app.jar
WORKDIR /usr/app
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]