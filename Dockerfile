FROM eclipse-temurin:11-jre-focal AS PUBLISH
WORKDIR /runnable

ENV SPRING_CONFIG_NAME application
ENV SPRING_CONFIG_LOCATION /settings/

COPY ["./build/libs/Navi-Storage-1.0-SNAPSHOT.jar", "."]

ENTRYPOINT ["java", "-jar", "Navi-Storage-1.0-SNAPSHOT.jar"]