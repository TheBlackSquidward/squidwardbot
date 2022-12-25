FROM gradle:7.5.1-jdk18 as builder
WORKDIR /home/gradle/source/

COPY ./ ./
RUN gradle jar
RUN ls -Rla

FROM eclipse-temurin:18-jre
WORKDIR /opt/SquidwardBot/

COPY --from=builder /home/gradle/source/build/libs/SquidwardBot.jar ./

ENTRYPOINT ["java", "-jar", "SquidwardBot.jar"]