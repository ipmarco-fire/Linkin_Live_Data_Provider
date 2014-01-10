package com.linkin.live.data.model;

public class PlayUrl {
	private String id;
	private int type;
	private String url;
	private long buffer;
	public long getBuffer() {
        return buffer;
    }
    public void setBuffer(long buffer) {
        this.buffer = buffer;
    }
    public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public PlayUrl(){
		
	}
	public PlayUrl(String id ,int type, String url) {
		super();
		this.id = id;
		this.type = type;
		this.url = url;
	}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
	
}
