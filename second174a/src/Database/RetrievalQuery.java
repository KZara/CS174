package Database;

import java.sql.ResultSet;
import java.sql.Statement;

public abstract class RetrievalQuery extends DbQuery {

	protected String display_string;

	public RetrievalQuery(String query) {
		super(query);
	}

	@Override
	public abstract void onComplete(ResultSet result);

	@Override
	public void onComplete(int numRowsAffected) {
		throw new IllegalArgumentException();
	}

	@Override
	public void execute(Statement statement) {
		try {
			ResultSet result = statement.executeQuery(this.getQuery());
			this.onComplete(result);
		} catch (Exception e) {
			this.onError(e);
		}

	}
}
