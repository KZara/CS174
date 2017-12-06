
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

		JButton buy = new JButton("Buy");
		buy.addActionListener(d.new BuyListener());

		JButton sell = new JButton("Sell");
		sell.addActionListener(d.new SellListener());

		JButton market_balance = new JButton("See Balance");
		market_balance.addActionListener(d.new BalanceListener());

		JButton transaction_history = new JButton("Transaction History");
		transaction_history.addActionListener(d.new TransactionListener());

		actor_stock = new JTextField("Type stock symbol Here to see current price");
		JButton find_stock = new JButton("Find Stock");
		find_stock.addActionListener(d.new FindStockListener());

		movie_info = new JTextField("Type name of movie to see information");
		JButton reviews = new JButton("Find Reviews");
		reviews.addActionListener(d.new ReviewsListener());

		JButton top_reviews = new JButton("List Top Reviews");
		top_reviews.addActionListener(d.new TopReviewsListener());

		panel.add(deposit);
		panel.add(buy);
		panel.add(sell);
		panel.add(market_balance);
		panel.add(actor_stock);
		panel.add(find_stock);
		panel.add(movie_info);
		panel.add(reviews);
		panel.add(top_reviews);
		panel.add(transaction_history);

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

	private String validate_stock(String input) {
		String stock = "select S.current_price, A.act_name, S.stock_symbol  "
				+ "from Stock S join ActorDirector A on A.stock_symbol = S.stock_symbol " + "where S.stock_symbol = '"
				+ input + "'";

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
			return results;
		} catch (SQLException ev) {
			ev.printStackTrace();
		}
		return null;
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
					String withdrawTransaction = "insert into Transactions (type, taxID, numShares, moneyAmount, date) values(\"withdraw\", "
							+ currentTaxID + ", 0, " + textField.getText() + ", (select date from MarketInfo));";

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
					String depositTransaction = "insert into Transactions (type, taxID, numShares, moneyAmount, date) values(\"deposit\", "
							+ currentTaxID + ", 0, " + textField.getText() + ", (select date from MarketInfo));";
					try {
						StarsRUs.statement = StarsRUs.connection.createStatement();
						boolean resultSet = StarsRUs.statement.execute(depositStatement);
						boolean resultSet2 = StarsRUs.statement.execute(depositTransaction);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	class BuyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean marketOpen = false;

			// check if market is open
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery("select marketOpen from MarketInfo;");

				if (resultSet.next())
					marketOpen = resultSet.getInt(1) == 1;

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			if (marketOpen) {

				JTextField stockSymbol = new JTextField();
				JTextField numberOfStock = new JTextField();

				Object[] inputFields = { "Enter stock symbol", stockSymbol, "Enter number of stock", numberOfStock };

				int option = JOptionPane.showConfirmDialog(null, inputFields, "Multiple Inputs",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				if (option == JOptionPane.OK_OPTION) {

					// check if stock is valid

					if (stockSymbol.getText().length() != 3)
						JOptionPane.showMessageDialog(null, "Not a valid Stock");

					String results = validate_stock(stockSymbol.getText());
					if (results == null || results.equals(""))
						JOptionPane.showMessageDialog(null, "Not a valid Stock!");

					// check for enough funds

					String enoughFundsQuery = "select M.balance - (" + numberOfStock.getText()
							+ "*S.current_price)-20  from MarketAccount M, Stock S where M.taxID = " + currentTaxID
							+ " and S.stock_symbol = \'" + stockSymbol.getText() + "\';";
					boolean hasEnoughFunds = false;
					try {
						ResultSet resultSet = StarsRUs.statement.executeQuery(enoughFundsQuery);

						if (resultSet.next()) {
							hasEnoughFunds = resultSet.getInt(1) >= 0;
							System.out.println("balance - price = " + resultSet.getInt(1));
						}
						if (hasEnoughFunds) {
							// get amount of money involved in transaction
							String amountInvolvedQuery = "select " + numberOfStock.getText()
									+ "*(select current_price from Stock where stock_symbol = \'"
									+ stockSymbol.getText() + "\') + 20;";
							ResultSet resultSet2 = StarsRUs.statement.executeQuery(amountInvolvedQuery);
							resultSet2.next();
							double amountInvolved = resultSet2.getDouble(1);
							System.out.println("amt involved = " + amountInvolved);

							// check if user already has stockAccount for this
							// stock
							String checkHasStockQuery = "select taxID,stock_symbol, buy_price from StockAccount  where taxID = "
									+ currentTaxID + " and stock_symbol = \'" + stockSymbol.getText()
									+ "\' and buy_price = (select current_price from Stock where stock_symbol = \'"
									+ stockSymbol.getText() + "\'); ";
							ResultSet resultSet3 = StarsRUs.statement.executeQuery(checkHasStockQuery);
							boolean hasStock = resultSet3.next();

							// add to stockAccount or create new stockAccount if
							// no account
							if (hasStock) {
								System.out.println("here");
								String buyHasAccount = "update StockAccount set quantity = quantity + "
										+ numberOfStock.getText() + " where taxID = " + currentTaxID
										+ " and stock_symbol = \'" + stockSymbol.getText()
										+ "\' and buy_price = (select current_price from Stock where stock_symbol = \'"
										+ stockSymbol.getText() + "\'); ";
								StarsRUs.statement.execute(buyHasAccount);
							} else {
								System.out.println("else");
								String buyNoAccount = "insert into StockAccount (buy_price, quantity, taxID, stock_symbol) values((select current_price from Stock where stock_symbol = \'"
										+ stockSymbol.getText() + "\')," + numberOfStock.getText() + "," + currentTaxID
										+ ", \'" + stockSymbol.getText().toUpperCase() + "\');";
								System.out.println(buyNoAccount);
								StarsRUs.statement.execute(buyNoAccount);
							}

							// update balance and profits
							String updateMarketAccount = "update MarketAccount set balance = balance - "
									+ amountInvolved + ", profits = profits - " + amountInvolved + " where taxID = " + currentTaxID;
							StarsRUs.statement.execute(updateMarketAccount);

							// Create transaction
							String insertTransaction = "insert into Transactions (type, taxID,numShares,moneyAmount, date, stock_symbol) "
									+ "values('buy'," + currentTaxID + "," + numberOfStock.getText() + ","
									+ amountInvolved + ",(select date from MarketInfo),+\'"
									+ stockSymbol.getText().toUpperCase() + "\');";
							StarsRUs.statement.execute(insertTransaction);
						}

					} catch (SQLException ev) {
						ev.printStackTrace();
					}

				}

			}

		}

	}

	class SellListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean marketOpen = false;

			// check if market is open
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery("select marketOpen from MarketInfo;");

				if (resultSet.next())
					marketOpen = resultSet.getInt(1) == 1;

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			if (marketOpen) {

				JTextField stockSymbol = new JTextField();
				JTextField numberOfStock = new JTextField();
				JTextField buyPrice = new JTextField();

				String stockInfo = "";
				try {

					StarsRUs.statement = StarsRUs.connection.createStatement();
					ResultSet resultSet = StarsRUs.statement
							.executeQuery("select * from StockAccount where taxID = " + currentTaxID + ";");

					ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
					int columnsNumber = rsmd.getColumnCount();

					while (resultSet.next()) {
						for (int i = 1; i <= columnsNumber; i++) {
							if (i > 1) {
								stockInfo += ", ";
							}
							String columnValue = resultSet.getString(i);
							stockInfo += columnValue + " ";
						}
						stockInfo += "\n";

					}

					System.out.println(stockInfo);

				} catch (SQLException ev) {
					ev.printStackTrace();
				}

				Object[] inputFields = { stockInfo, "Enter stock symbol", stockSymbol, "Enter number of stock",
						numberOfStock, "Enter buy price of stock to sell", buyPrice };

				int option = JOptionPane.showConfirmDialog(null, inputFields, "Multiple Inputs",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				if (option == JOptionPane.OK_OPTION) {
					String stock_symbol = stockSymbol.getText();
					String num_stock = numberOfStock.getText();
					String buy_price = buyPrice.getText();

					String enoughStockQuery = "select quantity - " + num_stock + " from StockAccount where taxID = "
							+ currentTaxID + " and stock_symbol = \'" + stock_symbol + "\' ;";

					try {
						// has enough stock to sell?
						ResultSet resultSet = StarsRUs.statement.executeQuery(enoughStockQuery);
						if (resultSet.next()) {
							if (resultSet.getInt(1) >= 0) {
								// money gained
								String moneyGainedQuery = "select (" + num_stock + "*(select current_price from Stock where stock_symbol = \'"
										+ stock_symbol + "\')) - 20";
								ResultSet resultSet2 = StarsRUs.statement.executeQuery(moneyGainedQuery);
								if (resultSet2.next()) {
									
									double moneyGained = resultSet2.getDouble(1);
									System.out.print(moneyGained);
									//update marketAccount
									String updateMarketAccountQuery = "update MarketAccount set balance = balance + "
											+ moneyGained + ", profits = profits + " + moneyGained + "where taxID = " + currentTaxID + ";";
									StarsRUs.statement.execute(updateMarketAccountQuery);

									String soldAllQuery = "select quantity from StockAccount where stock_symbol = \'"
											+ stock_symbol + "\' and taxID = " + currentTaxID + " and buy_price = " + buy_price +";";
									System.out.println(soldAllQuery);
									ResultSet resultSet3 = StarsRUs.statement.executeQuery(soldAllQuery);
									resultSet3.next();
									System.out.println(resultSet3.getInt(1));
									boolean soldAll = (resultSet3.getInt(1) - Integer.parseInt(num_stock)) == 0;
									// update stockAccount
									if (soldAll) {
										System.out.println("here");
										String soldAllUpdate = "delete from StockAccount where taxID = " + currentTaxID
												+ " and stock_symbol = \'" + stock_symbol + "\' and buy_price = "
												+ buy_price + ";";
										StarsRUs.statement.execute(soldAllUpdate);

									} else {
										System.out.println("else");
										String notSoldAllUpdate = "update StockAccount set quantity = quantity - "
												+ num_stock + " where taxID = " + currentTaxID
												+ " and stock_symbol = \'" + stock_symbol + "\' and buy_price = "
												+ buy_price + ";";
										StarsRUs.statement.execute(notSoldAllUpdate);

									}
									
									// insert transaction
									
									String insertTransaction = "insert into Transactions (type, taxID,numShares,moneyAmount, date, stock_symbol) "
											+ "values('sell'," + currentTaxID + "," + numberOfStock.getText() + ","
											+ (moneyGained+20) + ",(select date from MarketInfo),+\'"
											+ stockSymbol.getText().toUpperCase() + "\');";
									StarsRUs.statement.execute(insertTransaction);
									
									
									

								}
							}
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}

				}

			}
		}

	}

	class BalanceListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// show balancereviews
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

	class FindStockListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String input = actor_stock.getText();
			if (input.length() != 3)
				JOptionPane.showMessageDialog(null, "Not a valid Stock");

			String results = validate_stock(input);
			if (results != null && !results.equals(""))
				JOptionPane.showMessageDialog(null, results);

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

	class TopReviewsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JTextField startYear = new JTextField();
			JTextField endYear = new JTextField();

			Object[] inputFields = { "Enter start year", startYear, "Enter end year", endYear };

			int option = JOptionPane.showConfirmDialog(null, inputFields, "Multiple Inputs",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			String results = "";
			if (option == JOptionPane.OK_OPTION) {
				String reviews = "select * from Movies where rating >= 5 and production_year >= \'"
						+ startYear.getText() + "\' and production_year <= \'" + endYear.getText() + "\';";
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
						JOptionPane.showMessageDialog(null, "No top movies found!");

				} catch (SQLException ev) {
					ev.printStackTrace();
				}
			}

		}

	}

}
