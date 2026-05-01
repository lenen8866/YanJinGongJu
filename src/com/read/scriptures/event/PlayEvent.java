package com.read.scriptures.event;

public class PlayEvent {
	private int type;
	private boolean finish;


	public PlayEvent(int type, boolean finish) {
		this.type = type;
		this.finish = finish;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
