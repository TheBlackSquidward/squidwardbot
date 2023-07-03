FROM gradle:8.1.1-jdk17 as builder
WORKDIR /home/gradle/source/

COPY ./ ./
RUN gradle jar

FROM eclipse-temurin:17.0.7_7-jre
WORKDIR /opt/SquidwardBot/

COPY --from=builder /home/gradle/source/build/libs/SquidwardBot.jar ./

ENTRYPOINT ["java", "-jar", "SquidwardBot.jar"]