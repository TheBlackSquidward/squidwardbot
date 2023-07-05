FROM openjdk:20
WORKDIR /opt/SquidwardBot/
COPY /build/libs/SquidwardBot.jar SquidwardBot.jar
VOLUME env
ENTRYPOINT ["java", "-jar", "SquidwardBot.jar", "--env", "./env/.env"]