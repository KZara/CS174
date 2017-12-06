
import java.sql.*;

public class StarsRUs {
	static final String JDBC_DRIVER2 = "com.mysql.jdbc.Driver";
	static final String HOSTS = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/saagarDB";
	static final String USERS = "saagar";
	static final String PASSS = "291";
	static final String MOVIE_HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB";
	static Connection movieConnection = null;
	static Connection connection = null;
	static Statement statement = null;

	public static void main(String[] args) {
		WelcomePage.createFrame();
		// DbClient.getInstance();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {

			connection = DriverManager.getConnection(HOSTS, USERS, PASSS);
			movieConnection = DriverManager.getConnection(MOVIE_HOST, USERS, PASSS);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
