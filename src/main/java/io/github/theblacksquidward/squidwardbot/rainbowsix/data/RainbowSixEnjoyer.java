package io.github.theblacksquidward.squidwardbot.rainbowsix.data;

import io.github.theblacksquidward.squidwardbot.rainbowsix.commands.JsonBrowser;
import java.time.Duration;
import java.time.Instant;

// TODO rename
public class RainbowSixEnjoyer {

  private final String username;
  // TODO change this to an enum
  private final String platform;
  private final String ubisoftId;
  private final String uplayId;
  private final String avatarUrl;
  private final String lastUpdated;
  // TODO aliases
  private final String level;
  private final String totalXp;

  private final String wins;
  private final String playtime;
  private final String losses;
  private final String kills;
  private final String kd;
  private final String matchesPlayed;
  private final String deaths;

  public RainbowSixEnjoyer(JsonBrowser jsonBrowser) {
    JsonBrowser data = jsonBrowser.get("data");
    username = data.get("username").text();
    platform = data.get("platform").text();
    ubisoftId = data.get("ubisoft_id").text();
    uplayId = data.get("uplay_id").text();
    avatarUrl = data.get("avatar_url_256").text();
    lastUpdated = data.get("last_updated").text();
    level = data.get("progression").get("level").text();
    totalXp = data.get("progression").get("total_xp").text();

    JsonBrowser generalStats = data.get("stats").index(0).get("general");
    wins = generalStats.get("wins").text();
    playtime = generalStats.get("playtime").text();
    losses = generalStats.get("losses").text();
    kills = generalStats.get("kills").text();
    kd = generalStats.get("kd").text();
    matchesPlayed = generalStats.get("games_played").text();
    deaths = generalStats.get("deaths").text();
  }

  public String getUsername() {
    return username;
  }

  public String getPlatform() {
    return platform;
  }

  public String getUbisoftId() {
    return ubisoftId;
  }

  public String getUplayId() {
    return uplayId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getLastUpdatedAsString() {
    return lastUpdated;
  }

  public Instant getLastUpdatedAsInstant() {
    return Instant.parse(getLastUpdatedAsString());
  }

  public String getTimeSinceLastUpdate() {
    Duration duration = Duration.between(Instant.parse(getLastUpdatedAsString()), Instant.now());
    long days = duration.toDaysPart();
    long hours = duration.toHoursPart();
    long minutes = duration.toMinutesPart();
    return String.format(
            "%s %s %s",
            days > 0 ? String.format("%dd", days) : "",
            hours > 0 ? String.format("%dh", hours) : "",
            minutes > 0 ? String.format("%dm", minutes) : "")
        .trim();
  }

  public String getLevel() {
    return level;
  }

  public String getTotalXp() {
    return totalXp;
  }

  public String getWins() {
    return wins;
  }

  public String getRawPlaytime() {
    return playtime;
  }

  public String getPrettyPlaytime() {
    Duration duration = Duration.ofSeconds(Long.parseLong(getRawPlaytime()));
    return String.format(
        "%dd %dh %02dm", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart());
  }

  public String getLosses() {
    return losses;
  }

  public String getKills() {
    return kills;
  }

  public String getKd() {
    return kd;
  }

  public String getMatchesPlayed() {
    return matchesPlayed;
  }

  public String getDeaths() {
    return deaths;
  }

  public String getKillsPerMatch() {
    return String.format(
        "%.2f", Double.parseDouble(getKills()) / Double.parseDouble(getMatchesPlayed()));
  }

  public String getWinRate() {
    double wins = Double.parseDouble(getWins());
    double losses = Double.parseDouble(getLosses());
    return String.format("%.2f", wins * 100 / (wins + losses)) + "%";
  }
}
