package io.github.theblacksquidward.squidwardbot.audio.track;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

/**
 * A custom implementation of an AudioPlaylist that includes additional metadata.
 */
public class CustomAudioPlaylist implements AudioPlaylist {

    private final String name;
    private final List<AudioTrack> tracks;
    private final AudioTrack selectedTrack;
    private final boolean isSearchResult;

    private final AudioPlaylistInfo audioPlaylistInfo;

    /**
     * @param name Name of the playlist
     * @param tracks List of tracks in the playlist
     * @param selectedTrack Track that is explicitly selected
     * @param isSearchResult True if the playlist was created from search results
     */
    public CustomAudioPlaylist(String name, List<AudioTrack> tracks, AudioTrack selectedTrack, boolean isSearchResult, AudioPlaylistInfo audioPlaylistInfo) {
        this.name = name;
        this.tracks = tracks;
        this.selectedTrack = selectedTrack;
        this.isSearchResult = isSearchResult;
        this.audioPlaylistInfo = audioPlaylistInfo;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<AudioTrack> getTracks() {
        return tracks;
    }

    @Override
    public AudioTrack getSelectedTrack() {
        return selectedTrack;
    }

    @Override
    public boolean isSearchResult() {
        return isSearchResult;
    }

    public AudioPlaylistInfo getInfo() {
        return audioPlaylistInfo;
    }

}
