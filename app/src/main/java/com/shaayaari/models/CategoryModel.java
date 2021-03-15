package com.shaayaari.models;

import java.util.List;

public class CategoryModel {
    String msg;
    Integer likes;
    String id;
    String msgId;
    List<String> likeIds;
    List<String> favouriteIds;
    Boolean Like;
    Boolean Favourite;

    public String getMsgId() {
        return msgId;
    }

    public void setLike(Boolean like) {
        Like = like;
    }

    public void setFavourite(Boolean favourite) {
        Favourite = favourite;
    }

    public Boolean getLike() {
        return Like;
    }

    public Boolean getFavourite() {
        return Favourite;
    }

    public List<String> getFavouriteIds() {
        return favouriteIds;
    }

    public List<String> getLikeIds() {
        return likeIds;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getLikes() {
        return likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
