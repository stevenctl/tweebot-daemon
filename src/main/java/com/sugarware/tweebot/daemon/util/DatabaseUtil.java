package com.sugarware.tweebot.daemon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {

	public static Connection getConnection() throws SQLException {
		Properties props = PropertiesUtil.getInstance();
		return DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"),
				props.getProperty("db.password"));
	}

}
