package io.github.theblacksquidward.squidwardbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAudioLoadResultImpl implements AudioLoadResultHandler {

  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseAudioLoadResultImpl.class);

  protected final TrackScheduler trackScheduler;

  public BaseAudioLoadResultImpl(TrackScheduler trackScheduler) {
    this.trackScheduler = trackScheduler;
  }

  @Override
  public void trackLoaded(AudioTrack track) {}

  @Override
  public void playlistLoaded(AudioPlaylist playlist) {}

  @Override
  public void noMatches() {}

  @Override
  public void loadFailed(FriendlyException exception) {
    LOGGER.error(
        "Caught an exception when trying to load an audio track. {}", exception.getStackTrace());
  }
}
