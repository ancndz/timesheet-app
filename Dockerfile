FROM bellsoft/liberica-openjdk-alpine:17-cds-aarch64
WORKDIR /app
COPY target/timesheet-app-*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=80", "--spring.profiles.active=prod,postgres"]