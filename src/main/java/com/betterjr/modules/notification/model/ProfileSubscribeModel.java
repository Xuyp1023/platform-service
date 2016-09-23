package com.betterjr.modules.notification.model;

import java.io.Serializable;
import java.util.List;

public class ProfileSubscribeModel implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 4406806777854305420L;

    private String profileRule;
    private String profileName;
    private Long custNo;
    private String custName;

    private List<ChannelSubscribeModel> channels;

    public String getProfileRule() {
        return profileRule;
    }

    public void setProfileRule(final String anProfileRule) {
        profileRule = anProfileRule;
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

    public String getCustName() {
        return custName;
    }

    public void setCustName(final String anCustName) {
        custName = anCustName;
    }

    public List<ChannelSubscribeModel> getChannels() {
        return channels;
    }

    public void setChannels(final List<ChannelSubscribeModel> anChannels) {
        channels = anChannels;
    }
}
