
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
		//DbClient.getInstance();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            connection = DriverManager.getConnection(HOSTS, USERS, PASSS);
            movieConnection = DriverManager.getConnection(MOVIE_HOST,USERS,PASSS);
//            String movie = "SELECT * FROM Movies";
//            statement = movieConnection.createStatement();
//            ResultSet resultSet = statement.executeQuery(movie);
//
//            ResultSetMetaData rsmd = resultSet.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//            System.out.println("id\ttitle\trating\tproduction_year");
//
//            while (resultSet.next()) {
//            	  for (int i = 1; i <= columnsNumber; i++) {
//                      if (i > 1) System.out.print(",  ");
//                      String columnValue = resultSet.getString(i);
//                      System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                  }
//                  System.out.println("");
//            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

		
	}
}
