package asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import bean.Videoinfo;
import config.deviceInfo;
import util.DBOService;
import util.Stringcommon;

/**
 * 上传用子线程。
 * Created by QZD on 2015/5/8.
 */

public class MyTask extends AsyncTask<String, Integer, String> {
    private static final int TIME_OUT = 20* 1000; // 超时时间
    @Override
    protected void onPostExecute(String result) {
        Log.d("res","result1=="+result);
        // mTvProgress.setText("上传完成");
        if(Stringcommon.isNotblank(result)){
            try {
                final JSONObject json=new JSONObject(result);
                final String name= json.getString("name");
                final String filename= json.getString("filename");
                // 取出一个图片名
                final String imagename=json.getJSONArray("thumbs").getString(0)+"";
                DBOService.updatestate(name,"1");//改变状态完成
                for(int i=0;i< Stringcommon.upvideos.size();i++){
                    if( Stringcommon.upvideos.get(i).getVideoName().equals(name)){
                        final Videoinfo v= Stringcommon.upvideos.get(i);
                        Stringcommon.ths.remove(name);
                        Stringcommon.upvideos.get(i).setJindu("100");
                        Stringcommon.upvideos.get(i).setIsok("ok");
                        //修改名字
                        Stringcommon.upvideos.get(i).setVideoName(name+"rnm");
                        try {
                            new Thread(){
                                @Override
                                public void run()
                                {
                                }
                            }.start();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }catch (Exception e){
            }
//            Log.d("res","result==完成");
            Toast.makeText(deviceInfo.mainAct, "上传完毕，请等待审核。可在视频管理内查看审核结果。", Toast.LENGTH_LONG).show();
        }else {
//            Log.d("res","result==超时");
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int jindu=values[0];
        Double uped=values[2]/1024.0/1024.0;
        Double total=values[3]/1024.0/1024.0;
        DecimalFormat df = new DecimalFormat("#.0");//保留两位小树
        String result=df.format(uped)+"M/"+df.format(total)+"M  ("+jindu+"%)";
        Stringcommon.upvideos.get(values[1]).setJindu(result+"a"+jindu);
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String end = "\r\n";
            String name = params[0].trim();
            String size = params[1];
            String headstr = params[2];
            String srcPath = params[3];
            RandomAccessFile randomAccessFile = null;
            final int po= Integer.parseInt(params[4]);//当前位置
            String uploadurl = params[5];
            String token = params[6];
            String userid = params[7];
            String username = params[8];
            JSONObject jsonget=null;
//            JSONObject jsonget= UserService.upvideoget(name,size,headstr,uploadurl,token);//获取连接和进度
            int count = 0;
            Log.d("json", "jsonget==" + uploadurl+token);
            String urlString=uploadurl+"?token="+token+"&size="+size+"&name="+Stringcommon.toURLEncoded(name);
            try {
                // 读取文件 已传大小
                int length = Integer.parseInt(jsonget.getString("start"));
                URL url = new URL(urlString);
                // 上传文件内容
                FileInputStream fis = new FileInputStream(srcPath);
                long total = Long.parseLong(size);
                String totalstr = String.valueOf(total);
                Log.d("文件大小", totalstr);

                int onesize=2*1024*1024;
                int totalchuan=0;
                int clength=0;
                if(total%onesize>0){
                    totalchuan=((int)total- Integer.parseInt(jsonget.getString("start")))/onesize+1;
                }else{
                    totalchuan=((int)total- Integer.parseInt(jsonget.getString("start")))/onesize;
                }
                //每次上传大小
                for(int i=1;i<=totalchuan;i++){
                    int startsize= Integer.parseInt(jsonget.getString("start"))+(i-1)*onesize;
                    int endsize= Integer.parseInt(jsonget.getString("start"))+i*onesize;
                    clength=onesize;
                    if(i==totalchuan){
                        endsize=(int)total;
                        clength=(int)total-(i-1)*onesize- Integer.parseInt(jsonget.getString("start"));
                    }
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    // 使用POST方法
                    httpURLConnection.setRequestMethod("POST");
                    // httpURLConnection.connect();
                    // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
                    // 此方法用于在预先不知道内容长度时启用,没有进行内部缓冲的 HTTP 请求正文的流。
                    httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
                    httpURLConnection.setUseCaches(false);
                    // 允许输入输出流
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("Content-Range", "bytes " +startsize + "-" + endsize + "/" + size);
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
                    httpURLConnection.setRequestProperty("Content-Length", clength+"");
                    //  httpURLConnection.setRequestProperty("Transfer-Encoding","chunked");
                    //   httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.2.1) Chrome/18.0.1025.166 phone/Build(" + deviceInfo.getPhoneModel() + ")");
                    httpURLConnection.setRequestProperty("http-passport", headstr);
                    httpURLConnection.setConnectTimeout(5*1000*60*2);
                    httpURLConnection.setReadTimeout(5*1000*60*5);

                    DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
                    byte[] buffer = new byte[1024]; // 1k
                    randomAccessFile = new RandomAccessFile(srcPath, "rwd");
                    randomAccessFile.seek(startsize);
                    if(isCancelled()){
                        return null;
                    }
                    while (length<endsize) {
                        count = randomAccessFile.read(buffer);
                        dos.write(buffer, 0, count);
                        length += count;
                        int jindu=(int) ((length / (float) total) * 100);
                        publishProgress(jindu,po,length,(int)total);
                    }
                    dos.flush();
                    if(httpURLConnection.getResponseCode()!=200){
                        Stringcommon.upvideos.get(po).setIsok("ok");
                        return null;
                    }
                    DataInputStream inStream = new DataInputStream( httpURLConnection.getInputStream() );
                    if(i==totalchuan){
                        StringBuffer strBuffer = new StringBuffer("");
                        String str;
                        while (( str = inStream.readLine()) != null)
                        {
                            strBuffer.append(str);
                        }
                        JSONObject json=new JSONObject(strBuffer+"");
                        json.put("name",name);
                        json.put("userid",userid);
                        json.put("username",username);
                        json.put("head",headstr);//头
                        fis.close();
                        dos.close();
                        dos.close();
                        inStream.close();
                        return json+"";
                    }
                }
                randomAccessFile.close();
            } catch (Exception e) {
                Stringcommon.upvideos.get(po).setIsok("ok");
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}