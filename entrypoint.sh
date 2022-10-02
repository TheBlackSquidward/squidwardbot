#!/bin/sh
exec java -jar SquidwardBot.jar --discordBotAccessToken $discordToken --spotifyClientId $spotifyClientId --spotifyClientSecret $spotifyClientSecret
