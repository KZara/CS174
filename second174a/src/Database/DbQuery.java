package Database;

import java.sql.ResultSet;
import java.sql.Statement;

public abstract class DbQuery {
	private String query;

	public DbQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public abstract void onComplete(ResultSet result);

	public abstract void onComplete(int numRowsAffected);

	public abstract void execute(Statement statement);


	public boolean onError(Exception e) {
		System.out.println("Failed to execute:  " + query);
		e.printStackTrace();
		return true;
	}
}