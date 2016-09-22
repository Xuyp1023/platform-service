package com.betterjr.modules.notification.model;

import java.io.Serializable;

public class ChannelSubscribeModel implements Serializable {
    private static final long serialVersionUID = 374361275668747278L;

    private Long id;
    private Long sourceCustNo;
    private String profileName;
    private Long custNo;
    private String channel;
    private Boolean subscribed;

    public Long getId() {
        return id;
    }

    public void setId(final Long anId) {
        id = anId;
    }

    public Long getSourceCustNo() {
        return sourceCustNo;
    }

    public void setSourceCustNo(final Long anSourceCustNo) {
        sourceCustNo = anSourceCustNo;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(final String anProfileName) {
        profileName = anProfileName;
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(final Long anCustNo) {
        custNo = anCustNo;
    }

    public void setChannel(final String anChannel) {
        this.channel = anChannel;
    }

    public String getChannel() {
        return this.channel;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(final Boolean anSubscribed) {
        subscribed = anSubscribed;
    }
}
