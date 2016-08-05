package util;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * 
 * @author laomaizi
 * 写入日志文件

 */

public class WriteLog {
	
	public WriteLog(String string) {
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


