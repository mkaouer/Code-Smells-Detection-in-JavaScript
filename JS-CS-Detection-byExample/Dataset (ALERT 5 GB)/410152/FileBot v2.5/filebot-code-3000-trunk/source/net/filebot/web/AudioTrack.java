package net.filebot.web;

import java.io.Serializable;

public class AudioTrack implements Serializable {

	protected String artist;
	protected String title;
	protected String album;

	protected String albumArtist;
	protected String trackTitle;
	protected SimpleDate albumReleaseDate;
	protected Integer mediumIndex;
	protected Integer mediumCount;
	protected Integer trackIndex;
	protected Integer trackCount;

	protected String mbid; // MusicBrainz Identifier

	protected AudioTrack() {
	}

	public AudioTrack(AudioTrack other) {
		this.artist = other.artist;
		this.title = other.title;
		this.album = other.album;
		this.albumArtist = other.albumArtist;
		this.trackTitle = other.trackTitle;
		this.albumReleaseDate = other.albumReleaseDate;
		this.mediumIndex = other.mediumIndex;
		this.mediumCount = other.mediumCount;
		this.trackIndex = other.trackIndex;
		this.trackCount = other.trackCount;
		this.mbid = other.mbid;
	}

	public AudioTrack(String artist, String title, String album) {
		this.artist = artist;
		this.title = title;
		this.album = album;
	}

	public AudioTrack(String artist, String title, String album, String albumArtist, String trackTitle, SimpleDate albumReleaseDate, Integer mediumIndex, Integer mediumCount, Integer trackIndex, Integer trackCount, String mbid) {
		this.artist = artist;
		this.title = title;
		this.album = album;
		this.albumArtist = albumArtist;
		this.trackTitle = trackTitle;
		this.albumReleaseDate = albumReleaseDate;
		this.mediumIndex = mediumIndex;
		this.mediumCount = mediumCount;
		this.trackIndex = trackIndex;
		this.trackCount = trackCount;
		this.mbid = mbid;
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getAlbum() {
		return album;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public SimpleDate getAlbumReleaseDate() {
		return albumReleaseDate;
	}

	public Integer getMedium() {
		return mediumIndex;
	}

	public Integer getMediumCount() {
		return mediumCount;
	}

	public Integer getTrack() {
		return trackIndex;
	}

	public Integer getTrackCount() {
		return trackCount;
	}

	public String getMBID() {
		return mbid;
	}

	@Override
	public AudioTrack clone() {
		return new AudioTrack(this);
	}

	@Override
	public String toString() {
		return String.format("%s - %s", getArtist(), getTitle());
	}

}
