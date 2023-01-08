package io.github.theblacksquidward.squidwardbot.audio.track;

/**
 * Metadata information for an audio playlist.
 */
public class AudioPlaylistInfo {

    private final String artworkUrl;
    private final String owner;
    private final String ownerThumbnailUrl;
    private final String uri;

    public AudioPlaylistInfo(String artworkUrl, String owner, String ownerThumbnailUrl, String uri) {
        this.artworkUrl = artworkUrl;
        this.owner = owner;
        this.ownerThumbnailUrl = ownerThumbnailUrl;
        this.uri = uri;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerThumbnailUrl() {
        return ownerThumbnailUrl;
    }

    public String getUri() {
        return uri;
    }

}
