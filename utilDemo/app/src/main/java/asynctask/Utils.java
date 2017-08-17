package asynctask;

public class Utils
{
	//视频标题
	public static String[] title = {"","","","",""};
	//下载地址
	public static String[] url = {"","","","",""};
	//下载的字节位置
	public static int[] downloadPosition = {0,0,0,0,0};
	//缩略图地址
	public static String[] imgUrl = {"","","","",""};
	//vid
	public static String[] vid = {"","","","",""};
	//用户名
	public static String[] uname = {"","","","",""};
	//进度
	public static int[] progress = {0,0,0,0,0};
	//视频文件大小
	public static int[] fileLength = {0,0,0,0,0};
	//视频下载线程
	public static Async[] videoTask = {null,null,null,null,null};
	//预览图下载线程
	public static Async[] imgTask = {null,null,null,null,null};
}
