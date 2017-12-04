
import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class TraderDashboard {

	static JFrame frame;
	static JTextField actor_stock;
	static JTextField movie_info;

	static String user;

	// used for deposit/withdraw listener
	static String accountID;

	public static void createDashboard(String username) {

		// username is the distinct name of the user logged in at the moment.
		user = username;
		System.out.println(user);

		frame = new JFrame(username + "'s Dashboard");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension dimen = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(dimen);
		JPanel panel = new JPanel(new GridLayout(0,2));

		TraderDashboard d = new TraderDashboard();

		JButton deposit = new JButton("Deposit/Withdraw");
		deposit.addActionListener(d.new DepositListener());
		JButton buy = new JButton("Buy/Sell");
		buy.addActionListener(d.new BuyListener());
		JButton market_balance = new JButton("See Balance");
		market_balance.addActionListener(d.new BalanceListener());
		JButton transaction_history = new JButton("Transaction History");
		transaction_history.addActionListener(d.new TransactionListener());
		actor_stock = new JTextField("Type stock symbol Here to see current price");
		JButton go_1 = new JButton("Go");
		go_1.addActionListener(d.new Go1Listener());
		movie_info = new JTextField("Type name of movie to see information");
		JButton go_2 = new JButton("Go");
		go_2.addActionListener(d.new Go2Listener());

		panel.add(deposit);
		panel.add(buy);
		panel.add(market_balance);
		panel.add(transaction_history);
		panel.add(actor_stock);
		panel.add(go_1);
		panel.add(movie_info);
		panel.add(go_2);

		// 4. Size the frame.
		frame.pack();
		frame.setContentPane(panel);

		frame.setVisible(true);

		//return frame;
	}

	class DepositListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// go to deposit/withdraw page

			StringBuilder findAccountID = new StringBuilder("SELECT A.ID ").append("FROM Account A ")
					.append("WHERE ").append("A.username = ").append("'").append(user).append("'");

			DbClient.getInstance().runQuery(new RetrievalQuery(findAccountID.toString()) {
				@Override
				public void onComplete(ResultSet result) {

					try {
						if (!result.next()) {

							accountID = "";

							return;
						}
					} catch (HeadlessException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						accountID = result.getString(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("account id IS " + accountID);

			if (accountID == "") {
				JOptionPane.showMessageDialog(null, "no accounts shown for given username",
						"Error with Deposit/Withdraw", 0);
				return;
			}

			frame.setVisible(false);
			frame.dispose();
			accountID = "12356";

			// DepositPage.createDepositPage(user, accountID);
		}

	}

	class BuyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// go to buy/sell page

			frame.setVisible(false);
			frame.dispose();

			BuyStocksPage.createStocksPage(user);

		}

	}

	class BalanceListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// show balance
			// get balance using username
			StringBuilder addEntry = new StringBuilder("SELECT M.balance ").append("FROM MarketAccount M ")
					.append("WHERE M.username = ").append("'").append(user).append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String balance = "";

					try {
						if (!result.next()) {
							JOptionPane.showMessageDialog(null, "no accounts shown for given username", "Show Balance",
									1);
							return;
						}
					} catch (HeadlessException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					try {
						balance = result.getString(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String display_string = "Balance is " + balance + " dollars";
					JOptionPane.showMessageDialog(null, display_string, "Show Balance", 1);
				}
			});

			return;
		}

	}

	class TransactionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// deposit page

		}

	}

	class Go1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// gets information about the entered stock

			// get stock
			String stock = actor_stock.getText();

			if (stock.length() != 3 || !stock.matches("[a-zA-Z]+")) {
				JOptionPane.showMessageDialog(null, "Stock Symbol must be of size 3 and can only contain letters",
						"Stock Error", 0);
				return;
			}

			// stock is a 3 letter string
			// query to find stock

			StringBuilder addEntry = new StringBuilder("SELECT S.current_price ").append("FROM Stock S ")
					.append("WHERE S.stock_symbol = ").append("'").append(TraderDashboard.actor_stock.getText())
					.append("'");
			DbClient.getInstance().runQuery(new RetrievalQuery(addEntry.toString()) {
				@Override
				public void onComplete(ResultSet result) {
					String price = "";

					try {
						if (!result.next()) {
							JOptionPane.showMessageDialog(null, "no stocks under this symbol", "Error in Stocks", 0);
							return;
						}
					} catch (HeadlessException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					try {
						price = result.getString(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String display_string = "Current Stock Price is " + price + " dollars";
					JOptionPane.showMessageDialog(null, display_string, "Show Current Price", 1);
					System.out.println("lol");
				}
			});
		}

	}

	class Go2Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// deposit page

		}

	}

}