FROM gradle:7.5.1-jdk18 as builder
WORKDIR /home/gradle/source/

COPY ./ ./
RUN gradle fatJar
RUN ls -Rla

FROM eclipse-temurin:18-jre
WORKDIR /opt/SquidwardBot/

ARG DISCORD_TOKEN
ARG SPOTIFY_CLIENT_ID
ARG SPOTIFY_CLIENT_SECRET

ENV discordToken = $DISCORD_TOKEN
ENV spotifyClientId = $SPOTIFY_CLIENT_ID
ENV spotifyClientSecret = $SPOTIFY_CLIENT_SECRET

COPY --from=builder /home/gradle/source/build/libs/SquidwardBot.jar ./
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]