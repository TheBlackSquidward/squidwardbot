FROM gradle:8.0.2-jdk19 as builder
WORKDIR /home/gradle/source/

COPY ./ ./
RUN gradle jar
RUN ls -Rla

FROM eclipse-temurin:19.0.2_7-jre
WORKDIR /opt/SquidwardBot/

COPY --from=builder /home/gradle/source/build/libs/SquidwardBot.jar ./

ENTRYPOINT ["java", "-jar", "SquidwardBot.jar"]