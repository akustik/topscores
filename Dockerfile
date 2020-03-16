FROM openjdk:8-alpine

COPY build/libs/topscores-1.0.jar /usr/src/topscores/topscores.jar

WORKDIR /usr/src/topscores

CMD ["java", "-jar", "topscores.jar"]