#!/bin/bash
mv src/main/resources/application.yml ./backup.yml
./gradlew clean bootJar
mv ./backup.yml src/main/resource/application.yml
docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t kangdroid/navi-storage:b1cfaacd --push .
mv backup.yml src/main/resources/application.yml