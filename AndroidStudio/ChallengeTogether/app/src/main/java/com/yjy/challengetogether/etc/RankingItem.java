package com.yjy.challengetogether.etc;

public class RankingItem {

    private String nickname, recentstarttime;
    private int currenttime, ranking;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRecentstarttime() {
        return recentstarttime;
    }

    public void setRecentstarttime(String recentstarttime) {
        this.recentstarttime = recentstarttime;
    }

    public int getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(int currenttime) {
        this.currenttime = currenttime;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
