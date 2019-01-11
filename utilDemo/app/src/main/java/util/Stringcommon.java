package util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import asynctask.MyTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.Videoinfo;

/**
 * 字符串处理工具类
 *
 * @author zd
 * @Date 2014-12-2
 */
public class StringCommon {
    public static Boolean isgreegps=false;//记录用户是否同意打开定位权限
    public static List<Videoinfo> upvideos=new ArrayList<Videoinfo>();
    public static Map<String,MyTask> ths=new HashMap<String,MyTask>();//正在上传的线程
    /**
     * 格式转换为Utf-8
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static String toUtf8(String value) {
        try {
            return java.net.URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }
    //时间戳变为时间
    public static String cuototime(String cuo){
        try{
            Long date = Long.parseLong(cuo);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
        }catch(Exception e){
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

    }
    //时间戳变为时间
    public static String cuototimebyday(String cuo){
        try{
            Long date = Long.parseLong(cuo);
            return new SimpleDateFormat("yyyy.MM.dd").format(new Date(date));
        }catch(Exception e){
            return new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        }

    }

    /**
     * 转换服务器数据格式（中文）
     *
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String toServerUtf8(String value) {
        try {
            return java.net.URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException){
        }
        return "";
    }

    /**
     * 根据需要转编码两次
     *
     * @param value
     * @return
     */
    public static String toDecode(String value) {
        return toUtf8(toUtf8(value));
    }

    /**
     * 非空判断
     *
     * @param value
     * @return
     */
    public static boolean isNotblank(String value) {
        return value != null && !value.equals("") && !value.equals("null");
    }

    /**
     * 为空判断
     *
     * @param value
     * @return
     */
    public static boolean isblank(String value) {
        return value == null || value.equals("") || value.equals("null");
    }

    /**
     * 判断给定字符串是否空白串。
     * 空白串是指由空格、制表符、回车符、换行符组成的字符串
     * 若输入字符串为null或空字符串，返回true
     * @param input
     * @return boolean
     */
    public static boolean isEmpty( String input )
    {
        if ( input == null || "".equals( input ) )
            return true;
        for ( int i = 0; i < input.length(); i++ )
        {
            char c = input.charAt( i );
            if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @param
     * @return 整形字符串
     */
    public static String toIntString(String value) {
        return value.substring(0, value.indexOf("."));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, boolean _extr) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount()+1));

        ((ViewGroup.MarginLayoutParams) params).setMargins(40, 40, 40, 40); // 可删除

        listView.setLayoutParams(params);
    }


}
