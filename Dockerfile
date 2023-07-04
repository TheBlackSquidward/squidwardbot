FROM openjdk:20
WORKDIR /opt/SquidwardBot/

COPY /build/libs/SquidwardBot.jar SquidwardBot.jar

ENTRYPOINT ["java", "-jar", "SquidwardBot.jar"]