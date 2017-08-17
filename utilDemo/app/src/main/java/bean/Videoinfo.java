package bean;

import android.graphics.Bitmap;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/9.
 */
public class Videoinfo implements Serializable {
    private String videoid;
    private String videoImgurl="";
    private String videoName;
    private String videotime;
    private String source;
    private Userinfo user;
    private String id;
    private Boolean issupport=false;
    private Bitmap bitmap;
    private Boolean isstore,digg,bury;//是否收 藏，顶，踩
    private String addresspath;//本地视频路径
    private String isok="no";
    private String upstate="";
    private String size;
    private String upurl;//上传服务器地址
    private String token;//上传该视频的token
    private String title;//标题
    private String miaoshu;//描述
    private String biaoqian;//视频标签
    private String clicknum;
    private String videotimelength;//时长
    private boolean isHot;//是否热门
    private int index;//序号
    private int levelImgR=-1;//等级资源图
    private String levelName="";
    private int replyTotal=0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int _index) {
        this.index = _index;
    }

    public boolean getHot() {
        return isHot;
    }

    public void setHot(boolean _isHot) {
        this.isHot = _isHot;
    }

    public String getVideotimelength() {
        return videotimelength;
    }

    public void setVideotimelength(String videotimelength) {
        this.videotimelength = videotimelength;
    }

    public String getClicknum() {
        return clicknum;
    }

    public void setClicknum(String clicknum) {
        this.clicknum = clicknum;
    }

    public String getUpstate() {
        return upstate;
    }

    public void setUpstate(String upstate) {
        this.upstate = upstate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMiaoshu() {
        return miaoshu;
    }

    public void setMiaoshu(String miaoshu) {
        this.miaoshu = miaoshu;
    }

    public String getBiaoqian() {
        return biaoqian;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    public String getUpurl() {
        return upurl;
    }

    public void setUpurl(String upurl) {
        this.upurl = upurl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIsok() {
        return isok;
    }

    public void setIsok(String isok) {
        this.isok = isok;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getAddresspath() {
        return addresspath;
    }

    public void setAddresspath(String addresspath) {
        this.addresspath = addresspath;
    }

    private String jindu="0";

    public String getJindu() {
        return jindu;
    }

    public void setJindu(String jindu) {
        this.jindu = jindu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsstore() {
        return isstore;
    }

    public void setIsstore(Boolean isstore) {
        this.isstore = isstore;
    }

    public Boolean getDigg() {
        return digg;
    }

    public void setDigg(Boolean digg) {
        this.digg = digg;
    }

    public Boolean getBury() {
        return bury;
    }

    public void setBury(Boolean bury) {
        this.bury = bury;
    }

    public Boolean getIssupport() {
        return issupport;
    }

    public void setIssupport(Boolean issupport) {
        this.issupport = issupport;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Userinfo getUser() {
        return user;
    }

    public void setUser(Userinfo user) {
        this.user = user;
    }

    private String support;//支持

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getVideotime() {
        return videotime;
    }

    public void setVideotime(String videotime) {
        this.videotime = videotime;
    }

    public String getVideoImgurl() {
        return videoImgurl;
    }

    public void setVideoImgurl(String videoImgurl) {
        this.videoImgurl = videoImgurl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setLevelImg(int _index){
        levelImgR=_index;
    }

    public int getLevelImg(){
        return levelImgR;
    }

    public void setLevelName(String _name){
        levelName=_name;
    }

    public String getLevelName(){
        return levelName;
    }

    public int getReplyTotal(){
        return replyTotal;
    }

    public void setReplyTotal(int _v){
        replyTotal=_v;
    }
}
