package Database;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class DbClient {

	Connection connection;
	boolean connected = false;
	private boolean isRunning = false;
	private Queue<DbQuery> queryQueue = new LinkedList<>();

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/saagarDB";
	static final String USER = "saagar";
	static final String PASS = "291";

	private static DbClient instance = new DbClient();

	public static DbClient getInstance() {
		return instance;
	}

	public void run() {
		if (isRunning || !connected)
			return;
		isRunning = true;
		new Thread(new QueryRunner()).start();
	}

	private DbClient() {
		this.connect();
		this.onConnected();
	}

	private void runQueryQueue() {
		DbQuery currentQuery;
		Statement statement;
		try {
			statement = connection.createStatement();
		} catch (Exception e) {
			System.out.println("Failed to create Statement");
			e.printStackTrace();
			return;
		}
		while (!queryQueue.isEmpty()) {
			currentQuery = queryQueue.poll();
			currentQuery.execute(statement);
		}
	}

	private void onConnected() {
		run();
	}

	public void runQuery(DbQuery query) {
		queryQueue.add(query);
		run();
	}


	/**
	 * Connect to the database.
	 * 
	 * @return Whether or not the connection was successful
	 */
	private boolean connect() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Created JDBC driver class");
		try {
			connection = DriverManager.getConnection(HOST, USER, PASS);
			System.out.println("Connected to database");
			connected = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private class QueryRunner implements Runnable {
		@Override
		public void run() {
			isRunning = true;
			try {
				runQueryQueue();
			} catch (Exception e) {
				System.out.println("Error occurred, retrying...");
			}
			isRunning = false;
		}
	}
}