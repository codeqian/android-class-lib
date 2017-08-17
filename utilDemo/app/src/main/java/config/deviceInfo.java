package config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.qzd.utildemo.MainActivity;

import org.json.JSONObject;

import math.mathFactory;
import values.messageCode;

/**
 * 设备信息
 * Created by QZD on 2015/1/14.
 */
public class deviceInfo {
    //设备标识
    public static String myToken="";
    //是否开启消息推送
    public static final boolean pushEnable=true;
    public static String device_token="";
    //是否测试模式
    public static final boolean istest=false;
    //是否开启断点续传
    public static final boolean rangeDownload_able=true;
    //网络状态
    public static boolean netReady=false;
    //视频流模式
    public static final String streamType="hls";
    //sdk版本号
    private static int sdkVersion;
    //系统版本号
    private static String osVersion="";
    //手机型号
    private static String phoneModel="normal";
    private static JSONObject ipInfoObj;
    //app版本号
    public static String appVersion="";
    //已添加过视频到舞队
    public static boolean addedVideo2Team=false;
    //是否存在sd卡
    private static boolean hadSdcard=false;
    //缓存地址
    public static String cacheRoot="";
    //数据地址
    public static String dataRoot="";
    //网络类型
    private static int netType=0;
    //ip地址
    private static String ipAddress="";
    //mac地址
    private static String macAddress="";
    //品牌
    private static String brand="";
    //主activity
    public static MainActivity mainAct;
    //地理位置
    private static double Longitude=0;//经度
    private static double Latitude=0;//维度
    public static void setLongitude(double _n){
        Longitude=_n;
    }
    public static double getLongitude(){
        return Longitude;
    }
    public static void setLatitude(double _n){
        Latitude=_n;
    }
    public static double getLatitude(){
        return Latitude;
    }
    public static void setSdcard(boolean _is){
        hadSdcard=_is;
    }
    public static Boolean getSdcard(){
        return hadSdcard;
    }
    public static void setPhoneModel(String _m){
        phoneModel= mathFactory.replaceBlank(_m);
    }
    public static String getPhoneModel(){
        return phoneModel;
    }
    public static void setSdkVer(int _v){
        sdkVersion=_v;
    }
    public static int getSdkVer(){
        return sdkVersion;
    }
    public static void setOsVer(String _v){
        osVersion=mathFactory.replaceBlank(_v);
    }
    public static String getOsVer(){
        return osVersion;
    }
    public static void setIpInfo(JSONObject _info){
        ipInfoObj=_info;
    }
    public static JSONObject getIpInfo(){
        return ipInfoObj;
    }
    public static void setNetType(int _netType){
        Log.d("LOGCAT","netType:"+_netType);
        netType=_netType;
    }
    public static int getNetType(){
        return netType;
    }
    public static String getNetTypeString(){
        String _type="nonet";
        switch (netType){
            case messageCode.WIFINET:
                _type="wifi";
                break;
            case messageCode.NET2G:
                _type="2G";
                break;
            case messageCode.NET3G:
                _type="3G";
                break;
            case messageCode.NET4g:
                _type="4G";
                break;
        }
        return _type;
    }
    public static void setIpAddress(String _ipAddress){
        ipAddress=_ipAddress;
    }
    public static String getIpAddress(){
        return ipAddress;
    }
    public static void setMacAdress(String _macAddress){
        macAddress=_macAddress;
    }
    public static String getMacAddress(){
        return macAddress;
    }
    public static void setBrand(String _brand){
        brand=mathFactory.replaceBlank(_brand);
    }
    public static String getBrand(){
        return brand;
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static void getVersion(Context context)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion= mathFactory.replaceBlank(pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号(内部识别号)
     * @param context
     * @return
     */
    public static int getVersionCode(Context context)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
}
