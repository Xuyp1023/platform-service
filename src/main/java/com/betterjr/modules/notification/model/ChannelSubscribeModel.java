package com.betterjr.modules.notification.model;

import java.io.Serializable;

public class ChannelSubscribeModel implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 374361275668747278L;
    
    private Long id;
    private Long profileId;
    private String channel;
    private Boolean subscribed;

    public void setId(Long anId) {
        this.id = anId;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setProfileId(Long anProfileId) {
        this.profileId = anProfileId;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
    
    public void setChannel(String anChannel) {
        this.channel = anChannel;
    }
    
    public String getChannel() {
        return this.channel;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean anSubscribed) {
        subscribed = anSubscribed;
    }
}
