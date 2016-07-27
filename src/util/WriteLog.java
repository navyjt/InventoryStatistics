package util;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * 
 * @author laomaizi
 *该段代码主要实现了对日志文件的写入功能，在书写日志文件时，加入当前时间
 */

public class WriteLog {
	
	public WriteLog(String string) {
		// TODO 自动生成的构造函数存根
	}

	public static void WriteLogFile(String str)
	{
		FileWriter fw = null;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
		String sTime = sdf.format(c.getTime());
		
		try
		{
			fw = new FileWriter("traffic.log",true);
			fw.write(sTime+"  "+str+"\r\n");
		
		}

		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			if (fw != null)
			{
				try {
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}


