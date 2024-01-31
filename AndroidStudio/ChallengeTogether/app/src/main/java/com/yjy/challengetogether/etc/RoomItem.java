package com.yjy.challengetogether.etc;

public class RoomItem {

    private String useridx, username, permission, recentresettime, bestrecord;
    private Long bestTime;

    public String getUseridx() {
        return useridx;
    }

    public void setUseridx(String useridx) {
        this.useridx = useridx;
    }
    public void setBestTime(Long bestTime) { this.bestTime = bestTime; }
    public Long getBestTime() { return bestTime; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRecentresettime() {
        return recentresettime;
    }

    public void setRecentresettime(String recentresettime) {
        this.recentresettime = recentresettime;
    }

    public String getBestrecord() {
        return bestrecord;
    }

    public void setBestrecord(String bestrecord) {
        this.bestrecord = bestrecord;
    }
}
