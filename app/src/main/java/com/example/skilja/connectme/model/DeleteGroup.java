package com.example.skilja.connectme.model;

public class DeleteGroup {

    String deletedGroupId;
    String groupId;
    String emailUser;
    Boolean deleted;
    String title;
    int color;

    public DeleteGroup() {
        super();
    }

    public DeleteGroup(String deletedGroupId, String groupId, String emailUser, Boolean deleted, String title, int color) {
        this.deletedGroupId = deletedGroupId;
        this.groupId = groupId;
        this.emailUser = emailUser;
        this.deleted = deleted;
        this.title = title;
        this.color = color;
    }

    public String getDeletedGroupId() {
        return deletedGroupId;
    }

    public void setDeletedGroupId(String deletedGroupId) {
        this.deletedGroupId = deletedGroupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
