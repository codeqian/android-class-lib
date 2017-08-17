package bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * 用户信息
 * Created by Administrator on 2015/4/9.
 */
public class Userinfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fansCount;
    private String id;
    private String imageUrl;
    private String integral;
    private String link;
    private String name;
    private String subjectCount;
    private String support;
    private String videoCount;
    private String videoPlayTotal;
    private String visited;
    private String head;
    private String sex;
    private Bitmap bitmapImage;//图片
    private Bitmap coverImage ;//个人封面
    private String coverpath;
    private String birday="";//出生日
    private String qian;
    private String qq="";
    private String tephone="";
    private String address="";
    private String realname="";

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getTephone() {
        return tephone;
    }

    public void setTephone(String tephone) {
        this.tephone = tephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirday() {
        return birday;
    }
    public void setBirday(String birday) {
        this.birday = birday;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getQian() {
        return qian;
    }

    public void setQian(String qian) {
        this.qian = qian;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getFansCount() {
        return fansCount;
    }

    public void setFansCount(String fansCount) {
        this.fansCount = fansCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(String subjectCount) {
        this.subjectCount = subjectCount;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(String videoCount) {
        this.videoCount = videoCount;
    }

    public String getVideoPlayTotal() {
        return videoPlayTotal;
    }

    public void setVideoPlayTotal(String videoPlayTotal) {
        this.videoPlayTotal = videoPlayTotal;
    }

    public String getVisited() {
        return visited;
    }

    public void setVisited(String visited) {
        this.visited = visited;
    }



}
