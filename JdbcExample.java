import java.sql.*;

public class JdbcExample {

    public static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu/saagarDB";
    public static final String USER = "saagar";
    public static final String PWD = "291";
    public static final String QUERY = "SELECT * from  Stock";

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            connection = DriverManager.getConnection(HOST, USER, PWD);

            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(QUERY);


            System.out.println("stock_symbol\tdaily_close_price\tcurrent_price");

            while (resultSet.next()) {
                String A = resultSet.getString("stock_symbol");
                int B = resultSet.getInt("daily_close_price");
                int C = resultSet.getInt("current_price");

                System.out.println(A + "\t" + B + "\t" + C);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }

    }
}
