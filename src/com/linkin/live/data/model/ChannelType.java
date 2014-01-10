
package com.linkin.live.data.model;

import java.util.ArrayList;
import java.util.List;

public class ChannelType {
    private String id;
    private List<Channel> channelList;
    private String name;
    String pinyin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public ChannelType(String id, String name, String pinyin) {
        super();
        this.id = id;
        this.name = name;
        this.pinyin = pinyin;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(Channel channel) {
        if (this.channelList == null) {
            this.channelList = new ArrayList<Channel>();
        }
        this.channelList.add(channel);
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

}
