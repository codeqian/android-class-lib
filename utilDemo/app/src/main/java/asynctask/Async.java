package com.qzd.asynctask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

import com.qzd.net.streamDownLoadManager;
import com.qzd.values.messageCode;

/**
 * 子线程下载器。
 * Created by QZD on 2015/5/8.
 */
public class Async extends AsyncTask<String, Integer, String>
{
	private boolean finished = true;
	private boolean paused = false;
	private Boolean _isVideo=false;
	//文件大小
	private int fileSize=0;

	public boolean isPaused()
	{
		return paused;
	}
	@Override
	protected String doInBackground(String... Params)
	{
		int position = Integer.parseInt(Params[0]);//列表中的序号
		if(Params[1].equals("mov")){
			_isVideo=true;
		}else{
			_isVideo=false;
		}
		Log.d("LOGCAT","position:"+position+"-isVideo:"+_isVideo);
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		RandomAccessFile outputStream = null;
		String path = streamDownLoadManager.getDownloadPath();
		String fileName="";
		int length = 0;
		try
		{
			//获取跳转地址的版本*************************
			if(_isVideo){
				url = new URL(Utils.url[position]);
				fileName=Utils.vid[position]+ messageCode.VIDEOFILETYPE;

				int code=-1;
				int tryCount=0;
				String videoUrl=Utils.url[position];
				while (tryCount<30){
					tryCount++;
					HttpURLConnection infoCon=(HttpURLConnection) url.openConnection();
					infoCon.setInstanceFollowRedirects(false);//禁止跳转(让他自己跳就获取不了最终的地址，不知道为什么)
					code=infoCon.getResponseCode();
//					Log.d("LOGCAT", "transUrl:" + tryCount + "-" + code + "-"+videoUrl);
					if(code==200){
//						Log.d("LOGCAT", "url:" + url);
						break;
					}else {
						videoUrl=infoCon.getHeaderField("Location");
						url=new URL(videoUrl);
					}
					infoCon.disconnect();
				}
				URL downUrl=new URL(videoUrl);
//				Log.d("LOGCAT", "downUrl:" + downUrl);
				httpURLConnection = (HttpURLConnection)downUrl.openConnection();
			}else{
				url = new URL(Utils.imgUrl[position]);
//				Log.d("LOGCAT", "imgUrl:" + url);
				fileName=Utils.vid[position]+ messageCode.IMAGEFILETYPE;
				httpURLConnection = (HttpURLConnection)url.openConnection();
			}
			//***********************************************

			//不管跳转地址的版本****************************
//			if(_isVideo){
//				url = new URL(Utils.url[position]);
//				fileName=Utils.vid[position]+ messageCode.VIDEOFILETYPE;
//			}else{
//				url = new URL(Utils.imgUrl[position]);
//				fileName=Utils.vid[position]+ messageCode.IMAGEFILETYPE;
//			}
//			httpURLConnection = (HttpURLConnection)url.openConnection();
			//***********************************************

//			httpURLConnection.setRequestMethod("POST");

			//设置当前线程下载的起点，终点
			int startPosition=0;
			File outFile = new File(path+fileName);
//			Log.d("LOGCAT","outFile:"+path+fileName);
			if(_isVideo) {
				//先检测文件是否存在，如果不存在（意外删除）则断点归0
				if (!outFile.exists()){
					Utils.downloadPosition[position]=0;
				}
				startPosition = Utils.downloadPosition[position];
			}
			httpURLConnection.setRequestMethod("GET");
			//设置User-Agent
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("referer","http://app.qzd.com");
			httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0/1.1");
			httpURLConnection.setRequestProperty("connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
			httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
			if(_isVideo) {
				// 设置断点续传的开始位置
				httpURLConnection.setRequestProperty("Range", "bytes=" + startPosition+"-");//Accept-Ranges
				Log.d("LOGCAT", "startPosition:" + startPosition);
			}

			httpURLConnection.setAllowUserInteraction(true);
			length = httpURLConnection.getContentLength()+startPosition;
			Log.d("LOGCAT", "getContentLength:" + length);
			if(fileSize<length){
				fileSize=length;
			}

			//显示头信息
//			try {
//				Map headers = httpURLConnection.getHeaderFields();
//				Set<String> keys = headers.keySet();
//				for( String key : keys ){
//					String val = httpURLConnection.getHeaderField(key);
//					Log.d("LOGCAT", "getHeaderFields:" + key+"    "+val);
//					System.out.println(key+"    "+val);
//				}
//			}catch (Exception e){
//			}

			inputStream = httpURLConnection.getInputStream();
			//使用java中的RandomAccessFile 对文件进行随机读写操作
			outputStream = new RandomAccessFile(outFile,"rw");
			//设置开始写文件的位置
			outputStream.seek(startPosition);

			byte[] buf = new byte[1024*4];
			int read = 0;
			int curSize = startPosition;
			while(finished)
			{
				while(paused)
				{
					Thread.sleep(500);
				}
				read = inputStream.read(buf);
//				Log.d("LOGCAT","read:"+read);
				if(read==-1)
				{
					break;
				}
				outputStream.write(buf,0,read);
				curSize = curSize+read;
				if(_isVideo) {
					//记录进度。停止时的位置就是下载的断点
					Utils.downloadPosition[position] = curSize;
					streamDownLoadManager.updataPosition(Utils.vid[position],curSize);
				}
				// 当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度
				if(_isVideo) {
					publishProgress((int) (curSize * 100.0f / length), position);
				}
				Log.d("LOGVAR", "loading:"+curSize +"-"+ length);
				if(curSize >= length)
				{
					if(_isVideo) {
						publishProgress(100, position);
					}
					break;
				}
				Thread.sleep(10);
			}
			inputStream.close();
			outputStream.close();
			httpURLConnection.disconnect();
			//完成
			if(_isVideo) {
				Log.d("LOGCAT", "downLoadComplete:"+Utils.vid[position]);
				streamDownLoadManager.downLoadDone(Utils.vid[position]);
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			finished = false;
			if(inputStream!=null)
			{
				try
				{
					inputStream.close();
					if(outputStream!=null)
					{
						outputStream.close();
					}
					if(httpURLConnection!=null)
					{
						httpURLConnection.disconnect();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		// 这里的返回值将会被作为onPostExecute方法的传入参数
		return String.valueOf(position);
	}
	/**
	 * 暂停下载
	 */
	public void pause()
	{
		paused = true;
		Log.d("LOGCAT","pause paused------------"+paused);
	}
	/**
	 * 继续下载
	 */
	public void continued()
	{
		paused = false;
		Log.d("LOGCAT","continued paused------------"+paused);
	}
	/**
	 * 停止下载
	 */
	@Override
	protected void onCancelled()
	{
		Log.d("LOGCAT","onCancelled");
		finished = false;
		super.onCancelled();
	}
	/**
	 * 当一个下载任务成功下载完成的时候调用这个方法，这里的result参数就是doInBackground方法的返回值
	 */
	@Override
	protected void onPostExecute(String result)
	{
		int pos = -1;
		try
		{
			pos = Integer.parseInt(result);
			// 判断当前结束的这个任务在任务列表中是否还存在，如果存在就移除
//			for(int i=0;i<AsyncTaskActivity.listTask.size();i++)
//			{
//				if(AsyncTaskActivity.listTask.get(i).get(String.valueOf(pos))!=null)
//				{
//					finished = false;
//					Log.d("LOGCAT","remove sucess?:"+AsyncTaskActivity.listTask.remove(i));
//					break;
//				}
//			}

//			Log.d("LOGCAT","onPostExecute:"+AsyncTaskActivity.listTask.size());
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}
	/**
	 * 更新下载进度，当publishProgress方法被调用的时候就会自动来调用这个方法
	 */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
		//修改进度
		Utils.progress[values[1]]=values[0];
		Utils.fileLength[values[1]]=fileSize;
		super.onProgressUpdate(values);
	}
}
