package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
//�������ڶ�ȡ�����ļ��������ļ������ڸ�Ŀ¼�£���Ҫ��ȡ�������ļ���Ϊ���ݿ����Ӳ����Լ����ݵ��뷽ʽ����
public class ConfigFile {
	
	public static Connection getConnection()
			throws SQLException, IOException ,ClassNotFoundException
	{
			//ͨ������conn.ini�ļ�����ȡ���ݿ����ӵ���ϸ��Ϣ
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("conn.ini");
			props.load(in);
			in.close();
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			//�������ݿ�����
			Class.forName("com.mysql.jdbc.Driver");
			//ȡ�����ݿ�����
			//WriteLog.WriteLogFile("�������ݿ����ӳɹ����û���"+username);
			return DriverManager.getConnection(url, username, password);
	}
}