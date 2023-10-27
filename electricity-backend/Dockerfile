FROM openjdk:11.0-jdk
ENV TZ "Asia/Ho_Chi_Minh"
RUN rm -rf /var/cache/apk/*
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /usr/app/
WORKDIR /usr/app
ENTRYPOINT exec java $JAR_OPTS -jar $JAR_ARGS electricity-backend-0.0.1-SNAPSHOT.jar