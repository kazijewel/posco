package com.common.share;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseBackup {
//"BACKUP DATABASE transport TO DISK='"+savePath+
 //   "' WITH FORMAT,COMPRESSION"
	public DatabaseBackup(String path) throws Exception{
		Configuration cfg = new Configuration().configure();
		Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
		String user = cfg.getProperty("hibernate.connection.username");
		String pass = cfg.getProperty("hibernate.connection.password");
		String db = cfg.getProperty("hibernate.connection.databaseName");
		
		System.out.println("M");
		Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName="+db+";username="+user+";password="+pass);
		conn.createStatement().execute("BACKUP DATABASE "+db+" TO DISK = '"+path+"' WITH FORMAT,COMPRESSION");
		conn.close();
	}
	public DatabaseBackup(String path,int i) throws Exception{
		/*Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Connection conn = session.connection();
		Statement stmt = conn.createStatement();
		//stmt.execute("exec dbbackup '"+path+"'");
		stmt.execute("BACKUP DATABASE stockbroker TO DISK = '"+path+"' WITH FORMAT,COMPRESSION");
		*/
		new DatabaseBackup(path);
	}
}
