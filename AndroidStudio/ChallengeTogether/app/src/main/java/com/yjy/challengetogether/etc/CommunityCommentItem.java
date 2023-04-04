package com.yjy.challengetogether.etc;

public class CommunityCommentItem {

    private String useridx, content, createdate, nickname;
    private int commentidx, parentidx;

    public String getUseridx() {
        return useridx;
    }

    public void setUseridx(String useridx) {
        this.useridx = useridx;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParentidx() {
        return parentidx;
    }

    public void setParentidx(int parentidx) {
        this.parentidx = parentidx;
    }

    public int getCommentidx() {
        return commentidx;
    }

    public void setCommentidx(int commentidx) {
        this.commentidx = commentidx;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
