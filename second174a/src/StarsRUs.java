import Database.DbClient;

public class StarsRUs {
	public static void main(String[] args) {
		WelcomePage.createFrame();
		DbClient.getInstance();
	}
}
