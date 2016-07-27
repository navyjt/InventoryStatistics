package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
//本类用于读取配置文件，配置文件保存在根目录下，需要读取的配置文件分为数据库连接参数以及数据导入方式参数
public class ConfigFile {
	
	public static Connection getConnection()
			throws SQLException, IOException ,ClassNotFoundException
	{
			//通过加载conn.ini文件来获取数据库连接的详细信息
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("conn.ini");
			props.load(in);
			in.close();
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			//加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			//取得数据库连接
			//WriteLog.WriteLogFile("建立数据库连接成功，用户名"+username);
			return DriverManager.getConnection(url, username, password);
	}
}