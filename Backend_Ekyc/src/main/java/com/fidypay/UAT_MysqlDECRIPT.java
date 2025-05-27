package com.fidypay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.fidypay.encryption.Encryption;

public class UAT_MysqlDECRIPT {
	private static final String JDBC_URL = "jdbc:mysql://fidypay-uat-db.cljgt4wfgxae.ap-south-1.rds.amazonaws.com:3306";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "VZc3O1SADgsjEr1Ry9bY";
	private static final String sqlQuery = "SELECT CLIENT_ID, CLIENT_SECRET, IS_MERCHANT_ACTIVE FROM  FP_NEW.MERCHANT_INFO";

	public static void main(String[] args) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			System.out.println(Encryption.decString("yvmWkC6M0UZAJlLGpHf9TKhTaOuwtqggX6JHczuZOpE8Z0E07f8ADd4+kGPKKswm"));
			System.out.println(Encryption.decString("FP20250522025323445ZUsYFJkVWPcPNAJR"));
			
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
			System.out.println("Successfully connected to MySQL server!");
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlQuery);
			while (resultSet.next()) {
				String CLIENT_ID = Encryption.decString(Encryption.decString(resultSet.getString(1)));
//				System.out.println(Encryption.decString(CLIENT_ID));
				
				String CLIENT_SECRET = Encryption.decString(Encryption.decString(resultSet.getString(2))); 
				System.out.println("CLIENT_ID: " + CLIENT_ID + ", CLIENT_SECRET: " + CLIENT_SECRET );
			}

		} catch (ClassNotFoundException e) {
			System.err.println("MySQL JDBC driver not found.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Error connecting to MySQL database:");
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					System.out.println("Connection closed.");
				} catch (SQLException e) {
					System.err.println("Error closing the connection:");
					e.printStackTrace();
				}
			}
		}
	}
}