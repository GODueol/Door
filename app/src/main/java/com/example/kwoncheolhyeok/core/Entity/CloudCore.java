package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by kimbyeongin on 2018-03-17.
 */
public class CloudCore {
    String cUuid;
    long createDate;

    public String getcUuid() {
        return cUuid;
    }

    public void setcUuid(String cUuid) {
        this.cUuid = cUuid;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public CloudCore(String cUuid, long createDate) {
        this.cUuid = cUuid;
        this.createDate = createDate;
    }

    public CloudCore() {
    }
}
