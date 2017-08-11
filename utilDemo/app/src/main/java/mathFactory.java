package com.boosj.math;

import android.util.Log;

import com.boosj.Common.Stringcommon;
import com.boosj.bean.Userinfo;
import com.boosj.config.deviceInfo;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 转换数字格式
 * Created by QZD on 2015/2/26.
 */
public class mathFactory {
    private static String[] tKey={"01136c5948d353b1bg2","01136c5948d353b1bg2rfj"};//获取hls时用的校验key.获取下载时用的校验key
    //将毫秒转换为小时：分钟：秒格式
    public static String ms2HMS(int _ms){
        String HMStime;
        _ms/=1000;
        int hour=_ms/3600;
        int mint=(_ms%3600)/60;
        int sed=_ms%60;
        String hourStr=String.valueOf(hour);
        if(hour<10){
            hourStr="0"+hourStr;
        }
        String mintStr=String.valueOf(mint);
        if(mint<10){
            mintStr="0"+mintStr;
        }
        String sedStr=String.valueOf(sed);
        if(sed<10){
            sedStr="0"+sedStr;
        }
        HMStime=hourStr+":"+mintStr+":"+sedStr;
        return HMStime;
    }

    //将毫秒转换为标准日期格式
    public static String ms2Date(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static String ms2DateOnlyDay(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 标准时间转换为时间戳
     * @param _data
     * @return
     */
    public static long Date2ms(String _data){
        SimpleDateFormat format =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(_data);
            return date.getTime();
        }catch(Exception e){
            return 0;
        }
    }

    //将大于1万的数字转换为万为单位保留一位小数
    public static String changeCountFormat(String _count){
        int _ct=Integer.valueOf(_count).intValue();
        String countStr="";
        float _cf=0;
        if(_ct<10000){
            return _count;
        }else{
            _cf=((float) _ct)/ 10000;
            countStr=String.format("%.1f", _cf)+"万";
        }
        return countStr;
    }

    //格式化文件大小(参数的单位是kb)
    public static String changeSizeFormat(int _s){
        int _ct=Integer.valueOf(_s).intValue();
        String _Str="";
        float _cf=0;
        if(_ct<1024){
            return _s+"KB";
        }else{
            _cf=((float) _ct)/1024;
            _Str=String.format("%.1f", _cf)+"MB";
        }
        return _Str;
    }

    //计算时间差
    public static  String DateDistance(Date startDate,Date endDate){
        if(startDate == null ||endDate == null){
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if(timeLong<0){
            timeLong=0;
        }
        if (timeLong<60*1000)
            return timeLong/1000 + "秒前";
        else if (timeLong<60*60*1000){
            timeLong = timeLong/1000 /60;
            return timeLong + "分钟前";
        }
        else if (timeLong<60*60*24*1000){
            timeLong = timeLong/60/60/1000;
            return timeLong+"小时前";
        }
        else if ((timeLong/1000/60/60/24)<7){
            timeLong = timeLong/1000/ 60 / 60 / 24;
            return timeLong + "天前";
        }else{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(startDate);
        }
//        else if ((timeLong/1000/60/60/24)<30){
//            timeLong = timeLong/1000/ 60 / 60 / 24/7;
//            return timeLong + "周前";
//        }
//        else if ((timeLong/1000/60/60/24/30)<12){
//            timeLong = timeLong/1000/ 60 / 60 / 24/30;
//            return timeLong + "月前";
//        }
//        else {
//            return timeLong/1000/60/60/24/30/12+"年前";
//        }
    }

    //计算与当前的时间差
    public static  String DateDistance2now(long _ms){
        SimpleDateFormat DateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Long time=new Long(_ms);
            String d = DateF.format(time);
            Date startDate=DateF.parse(d);
            Date nowDate = Calendar.getInstance().getTime();
            return DateDistance(startDate, nowDate);
        }catch (Exception e){
        }
        return null;
    }

    /**
     * 清除html标签
     * @param _info
     * @return
     */
    public static String clearHtmlFormat(String _info){
        String htmlStr = _info; // 含html标签的字符串
        String textStr = _info;
        try {
            Pattern p_script;
            Matcher m_script;
            Pattern p_style;
            Matcher m_style;
            Pattern p_html;
            Matcher m_html;
            Pattern p_special;
            Matcher m_special;
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // 定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";
            // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            String regEx_special = "\\&[a-zA-Z]{1,10};";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
            m_special = p_special.matcher(htmlStr);
            htmlStr = m_special.replaceAll(""); // 过滤特殊标签
            textStr = htmlStr;
        }catch (Exception e){
        }
        return textStr;
    }

    /**
     * 重构html文本用以在webView展示
     * @param _info
     * @return
     */
    public static String reBuildHtml(String _info,String _title){
        String htmlStr="<html lang='zh-cn'><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>"+_title+"</title></head><body>"+_info+"</body></html>";
        return htmlStr;
    }

    public static String md5Encode(String _vid,int _index){
        String string4md5=_vid+tKey[_index];
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(string4md5.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 用户信息md5
     */
    public static String userMd5Encode(Userinfo _user){
        String _head="";
        String _uid="";
        if(_user!=null && !Stringcommon.isblank(_user.getName()) && Stringcommon.isNotblank(_user.getHead())) {
            _head = _user.getHead();
            _uid = _user.getId();
        }
        String string4md5="uid="+_uid+"&t="+_head + tKey;
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(string4md5.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return "uid="+_uid+"&t="+_head+"&hash="+buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设备标识（每个请求都发送，第一次请求http时获得并存入deviceConfig)
     */
    public static String getDeviceToken(){
        String _t="";
        String _t2hash="";
        String[] KEY_FOR_HASH={"an","bm","bn","cv","dt","lat","lon","nt","os","osv","t"};
        Date nowDate=new Date();
        String[] keys={
                "gcwapp",
                deviceInfo.getPhoneModel(),
                deviceInfo.getBrand(),
                deviceInfo.appVersion,
                deviceInfo.device_token,
                ""+deviceInfo.getLatitude(),
                ""+deviceInfo.getLongitude(),
                deviceInfo.getNetTypeString(),
                "android",
                deviceInfo.getOsVer(),
                ""+nowDate.getTime()};
        for(int i=0;i<KEY_FOR_HASH.length;i++){
            if(!keys[i].equals("")){
                _t+="&"+KEY_FOR_HASH[i]+"="+keys[i];
                _t2hash+=keys[i];
            }
        }
//        Arrays.sort(keys);
        _t+="&hash="+SHA(_t2hash, "SHA-256");
        deviceInfo.myToken=_t;
        return _t;
    }

    /**
     * hash
     */
    private static String SHA(final String strText, final String strType)
    {
        // 返回值
        String strResult = null;
        if (strText != null && strText.length() > 0)
        {
            try{
                // 创建加密对象 并传入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 类型结果
                byte byteBuffer[] = messageDigest.digest();
                // 將 byte 转换为 string
                StringBuffer strHexString = new StringBuffer();
                // 遍历 byte buffer
                for (int i = 0; i < byteBuffer.length; i++)
                {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1)
                    {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }
        return strResult;
    }

    /**
     * 移除不可见字符
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
