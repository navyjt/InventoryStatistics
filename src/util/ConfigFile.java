package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class ConfigFile {
	
	public static Connection getConnection()
			throws SQLException, IOException ,ClassNotFoundException
	{
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("conn.ini"); 
			props.load(in);
			in.close();
			String url = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(url, username, password);
	}
}