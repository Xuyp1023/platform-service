package com.betterjr.modules.customer.helper;

import java.io.Serializable;

import com.betterjr.modules.customer.entity.CustChangeApply;

public class ChangeDetailBean<T extends Serializable> implements Serializable {
    private CustChangeApply changeApply;

    private T befData;
    private T nowData;

    public CustChangeApply getChangeApply() {
        return changeApply;
    }

    public void setChangeApply(CustChangeApply anChangeApply) {
        changeApply = anChangeApply;
    }

    public T getBefData() {
        return befData;
    }

    public void setBefData(T anBefData) {
        befData = anBefData;
    }

    public T getNowData() {
        return nowData;
    }

    public void setNowData(T anNowData) {
        nowData = anNowData;
    }

}
