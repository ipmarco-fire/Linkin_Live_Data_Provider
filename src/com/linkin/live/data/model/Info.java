package com.linkin.live.data.model;

public class Info {
    
    String id; // 源
    String operator; // 运营商（客户）
    int sn; // SN
    long buffer; // 缓冲时间
    int play; // 是否播放
    int checked; // 是否选中
    long time; // 播放时间
    long testTime; // 测试时间
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public int getSn() {
        return sn;
    }
    public void setSn(int sn) {
        this.sn = sn;
    }
    public long getBuffer() {
        return buffer;
    }
    public void setBuffer(long buffer) {
        this.buffer = buffer;
    }
    public int getPlay() {
        return play;
    }
    public void setPlay(int play) {
        this.play = play;
    }
    public int getChecked() {
        return checked;
    }
    public void setChecked(int checked) {
        this.checked = checked;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getTestTime() {
        return testTime;
    }
    public void setTestTime(long testTime) {
        this.testTime = testTime;
    }
    
    
    
}
