package com.betterjr.modules.notification.model;

import java.io.Serializable;
import java.util.List;

public class ProfileSubscribeModel implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4406806777854305420L;
    
    private Long id;
    private String profileName;
    private Long custNo;
    private String custName;
    
    private List<ChannelSubscribeModel> channels;

    public Long getId() {
        return id;
    }

    public void setId(Long anId) {
        id = anId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String anProfileName) {
        profileName = anProfileName;
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(Long anCustNo) {
        custNo = anCustNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String anCustName) {
        custName = anCustName;
    }

    public List<ChannelSubscribeModel> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelSubscribeModel> anChannels) {
        channels = anChannels;
    }
}
