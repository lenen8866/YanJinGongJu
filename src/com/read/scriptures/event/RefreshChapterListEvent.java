package com.read.scriptures.event;

public class RefreshChapterListEvent {

	private int volumeId;

	public RefreshChapterListEvent(int volumeId) {
		this.volumeId = volumeId;
	}

	public int getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(int volumeId) {
		this.volumeId = volumeId;
	}
}
