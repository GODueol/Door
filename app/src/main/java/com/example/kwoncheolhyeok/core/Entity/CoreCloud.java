package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by kimbyeongin on 2018-03-17.
 */
public class CoreCloud {
    String cUuid;
    long attachDate;
    long modifyDate;

    public long getAttachDate() {
        return attachDate;
    }

    public void setAttachDate(long attachDate) {
        this.attachDate = attachDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getcUuid() {
        return cUuid;
    }

    public void setcUuid(String cUuid) {
        this.cUuid = cUuid;
    }

    public CoreCloud(String cUuid, long attachDate, long modifyDate) {
        this.cUuid = cUuid;
        this.attachDate = attachDate;
        this.modifyDate = modifyDate;
    }

    public CoreCloud() {
    }
}
