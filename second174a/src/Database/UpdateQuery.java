package Database;

import java.sql.ResultSet;
import java.sql.Statement;

public class UpdateQuery extends DbQuery {
	public UpdateQuery(String query) {
		super(query);
	}

	@Override
	public void onComplete(ResultSet result) {
		throw new IllegalArgumentException();
	}

	@Override
	public void onComplete(int numRowsAffected) {
		// optional: add logging info
	}

	@Override
	public void execute(Statement statement) {
		try {
			int result = statement.executeUpdate(this.getQuery());
			onComplete(result);
		} catch (Exception e) {
			this.onError(e);
		}

	}
}
