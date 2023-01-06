package io.github.theblacksquidward.squidwardbot.audio.track;

/**
 * Metadata information for an audio playlist.
 */
public class AudioPlaylistInfo {

    private final String artworkUrl;
    private final String artist;
    private final String artistArtworkUrl;
    private final String uri;

    public AudioPlaylistInfo(String artworkUrl, String artist, String artistArtworkUrl, String uri) {
        this.artworkUrl = artworkUrl;
        this.artist = artist;
        this.artistArtworkUrl = artistArtworkUrl;
        this.uri = uri;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getArtist() {
        return artist;
    }

    public String getArtistArtworkUrl() {
        return artistArtworkUrl;
    }

    public String getUri() {
        return uri;
    }

}
