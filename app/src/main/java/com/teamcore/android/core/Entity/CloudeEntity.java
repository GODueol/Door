package com.teamcore.android.core.Entity;

public class CloudeEntity {

    private String cUuid;
    private CoreListItem coreListItem;
    private String deletePostKey;
    private String deleteCUuid;

    public CloudeEntity(String cUuid, CoreListItem coreListItem, String deletePostKey, String deleteCUuid) {
        this.cUuid = cUuid;
        this.coreListItem = coreListItem;
        this.deletePostKey = deletePostKey;
        this.deleteCUuid = deleteCUuid;
    }

    public String getCUuid() {
        return cUuid;
    }

    public CoreListItem getCoreListItem() {
        return coreListItem;
    }


    public String getDeletePostKey() {
        return deletePostKey;
    }


    public String getDeleteCUuid() {
        return deleteCUuid;
    }

}
