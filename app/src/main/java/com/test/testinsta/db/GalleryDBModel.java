package com.test.testinsta.db;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Nikhil-PC on 20/12/17.
 */

public class GalleryDBModel extends SugarRecord {

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    String image_id;
    String imgThumb;

    public String getImgOri() {
        return imgOri;
    }

    public void setImgOri(String imgOri) {
        this.imgOri = imgOri;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    String imgOri;
    String comment_count;
    String caption;
    String likes;
    String tags;

    public String getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public String getLocation_long() {
        return location_long;
    }

    public void setLocation_long(String location_long) {
        this.location_long = location_long;
    }

    String location_lat;
    String location_long;

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

}
