package io.github.theblacksquidward.squidwardbot.audio.source.applemusic;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.github.theblacksquidward.squidwardbot.audio.source.mirror.MirroringAudioTrack;

public class AppleMusicAudioTrack extends MirroringAudioTrack {

  public AppleMusicAudioTrack(AudioTrackInfo trackInfo, AppleMusicSourceManager sourceManager) {
    super(trackInfo, sourceManager);
  }

  @Override
  protected AudioTrack makeShallowClone() {
    return new AppleMusicAudioTrack(trackInfo, (AppleMusicSourceManager) sourceManager);
  }
}
