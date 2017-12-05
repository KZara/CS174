
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mysql.jdbc.ResultSetMetaData;

public class TraderDashboard {

	static JFrame frame;
	static JTextField actor_stock;
	static JTextField movie_info;

	static String user;
	static String currentTaxID = "";

	public static void createDashboard(String username) {

		// username is the distinct name of the user logged in at the moment.
		user = username;
		System.out.println(user);

		frame = new JFrame(username + "'s Dashboard");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension dimen = new Dimension(800, 800);

		frame.getContentPane().setPreferredSize(dimen);
		JPanel panel = new JPanel(new GridLayout(0, 2));

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
		JButton go_1 = new JButton("Find Stock");
		go_1.addActionListener(d.new Go1Listener());
		movie_info = new JTextField("Type name of movie to see information");
		JButton reviews = new JButton("Find Reviews");
		reviews.addActionListener(d.new ReviewsListener());
		panel.add(deposit);
		panel.add(buy);
		panel.add(market_balance);
		panel.add(transaction_history);
		panel.add(actor_stock);
		panel.add(go_1);
		panel.add(movie_info);
		panel.add(reviews);

		// 4. Size the frame.
		frame.pack();
		frame.setContentPane(panel);

		frame.setVisible(true);

		String taxID = "select taxID from Customer where username = '" + user + "'";
		try {
			StarsRUs.statement = StarsRUs.connection.createStatement();
			ResultSet resultSet = StarsRUs.statement.executeQuery(taxID);
			if (resultSet.next())
				currentTaxID = resultSet.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(currentTaxID);
	}

	class DepositListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JTextField textField = new JTextField();
			final JCheckBox checkBox = new JCheckBox();

			Object[] inputFields = { "Enter $ amount to deposit/withdraw", textField, "Withdraw? (Deposit is default)",
					checkBox };

			int option = JOptionPane.showConfirmDialog(null, inputFields, "Multiple Inputs",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (option == JOptionPane.OK_OPTION) {
				if (checkBox.isSelected()) {
					// withdraw
					String balanceQuery = "select balance - " + textField.getText()
							+ " from MarketAccount  where taxID = " + currentTaxID + ";";
					String withdrawStatement = "update MarketAccount  set balance = (balance - " + textField.getText()
							+ ") where taxID = " + currentTaxID + ";";
					String withdrawTransaction = "insert into Transactions (type, taxID, numShares, moneyAmount, date) values(\"withdraw\", " + currentTaxID + ", 0, " + textField.getText() +", (select date from MarketInfo));";

					try {
						StarsRUs.statement = StarsRUs.connection.createStatement();
						ResultSet resultSet = StarsRUs.statement.executeQuery(balanceQuery);
						if (resultSet.next())
							if (resultSet.getInt(1) >= 0) {
								boolean resultSet2 = StarsRUs.statement.execute(withdrawStatement);
								boolean resultSet3 = StarsRUs.statement.execute(withdrawTransaction);
							} else {
								JOptionPane.showMessageDialog(null, "Not enough funds available!");
							}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					// deposit
					String depositStatement = "update MarketAccount  set balance = (balance + " + textField.getText()
							+ ") where taxID = " + currentTaxID + ";";
					String depositTransaction = "insert into Transactions (type, taxID, numShares, moneyAmount, date) values(\"deposit\", " + currentTaxID + ", 0, " + textField.getText() +", (select date from MarketInfo));";
					try {
						StarsRUs.statement = StarsRUs.connection.createStatement();
						boolean resultSet = StarsRUs.statement.execute(depositStatement);
						boolean resultSet2 = StarsRUs.statement.execute(depositTransaction);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Deposit amount of $" + textField.getText());
				}
			}
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
			String balanceQuery = "select balance from MarketAccount where taxID = " + currentTaxID;
			String balance = "";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(balanceQuery);

				if (resultSet.next())
					balance = resultSet.getString(1);

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Balance = " + balance, balance, 1);

		}

	}

	class TransactionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String transactions = "select * from Transactions where taxID = '" + currentTaxID + "'";
			String results = "";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(transactions);
				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnsNumber = rsmd.getColumnCount();

				while (resultSet.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
						if (i > 1) {
							System.out.print(",  ");
							results += ", ";
						}
						String columnValue = resultSet.getString(i);
						results += columnValue + " ";
						System.out.print(columnValue);
					}
					results += "\n";
					System.out.println("");

				}
				if (!results.equals(""))
					JOptionPane.showMessageDialog(null, results);
				else
					JOptionPane.showMessageDialog(null, "No transactions recorded!");
			} catch (SQLException ev) {
				ev.printStackTrace();
			}
			

		}

	}

	class Go1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String input = actor_stock.getText();
			if (input.length() != 3)
				JOptionPane.showMessageDialog(null, "Not a valid Stock", input, 1);

			String stock = "select S.current_price, A.act_name, S.stock_symbol  "
					+ "from Stock S join ActorDirector A on A.stock_symbol = S.stock_symbol "
					+ "where S.stock_symbol = '" + input + "'";

			String results = "";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(stock);

				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnsNumber = rsmd.getColumnCount();

				while (resultSet.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
						if (i > 1) {
							System.out.print(",  ");
							results += ", ";
						}
						String columnValue = resultSet.getString(i);
						results += columnValue + " ";
						System.out.print(columnValue);
					}
					results += "\n";
					System.out.println("");

				}
				if (!results.equals(""))
					JOptionPane.showMessageDialog(null, results);
			} catch (SQLException ev) {
				ev.printStackTrace();
			}

		}
	}

	class ReviewsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String input = movie_info.getText();
			String reviews = "select R.author,R.review,M.title " + "from Reviews R,Movies M "
					+ "where R.movie_id = (select id from Movies m1  " + "where m1.title = '" + input
					+ "' and m1.id = M.id)";
			String results = "";
			try {

				StarsRUs.statement = StarsRUs.movieConnection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(reviews);

				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnsNumber = rsmd.getColumnCount();

				while (resultSet.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
						if (i > 1) {
							System.out.print(",  ");
							results += ", ";
						}
						String columnValue = resultSet.getString(i);
						results += columnValue + " ";
						System.out.print(columnValue);
					}
					results += "\n";
					System.out.println("");

				}
				if (!results.contentEquals(""))
					JOptionPane.showMessageDialog(null, results);
				else
					JOptionPane.showMessageDialog(null, "Not a valid movie!");

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

		}

	}
}
