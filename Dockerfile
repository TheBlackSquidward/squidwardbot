FROM gradle:7.5.1-jdk18 as builder
WORKDIR /home/gradle/source/

COPY ./ ./
RUN gradle fatJar
RUN ls -Rla

FROM eclipse-temurin:18-jre
WORKDIR /opt/SquidwardBot/

COPY --from=builder /home/gradle/source/build/libs/SquidwardBot.jar ./
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]