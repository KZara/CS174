
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
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

import com.mysql.jdbc.ResultSetMetaData;

public class ManagerDashboard {

	static JFrame frame;
	static JTextField customer_report;
	static JTextField set_date;
	static String manager;
	static String print_string;

	public static JFrame createDashboard(String username) {
		// username is the distinct name of the user logged in at the moment.
		manager = username;

		frame = new JFrame(username + "'s Dashboard");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension dim = new Dimension(1000, 1000);
		frame.getContentPane().setPreferredSize(dim);
		JPanel panel = new JPanel(new GridLayout(0, 3));

		ManagerDashboard d = new ManagerDashboard();

		JButton add_interest = new JButton("Add Interest");
		add_interest.addActionListener(d.new InterestListener());

		JButton monthly_statement = new JButton("Generate Monthly Statement");
		monthly_statement.addActionListener(d.new StatementListener());

		JButton actives = new JButton("List Active Customers");
		actives.addActionListener(d.new ActivesListener());

		JButton dter = new JButton("Generate DTER");
		dter.addActionListener(d.new DTERListener());

		customer_report = new JTextField("Type username of customer here");
		JButton customerReport = new JButton("Customer Report");
		customerReport.addActionListener(d.new ReportListener());

		JButton delete_trans = new JButton("Delete Transactions");
		delete_trans.addActionListener(d.new DeleteListener());

		set_date = new JTextField("Enter date in format YYYY-MM-DD");
		JButton setDate = new JButton("Set Date");
		setDate.addActionListener(d.new DateListener());

		JButton open = new JButton("Open Market");
		open.addActionListener(d.new OpenListener());

		JButton close = new JButton("Close Market");
		close.addActionListener(d.new CloseListener());

		JButton setPrice = new JButton("Set Stock Price");
		close.addActionListener(d.new PriceListener());

		panel.add(add_interest);
		panel.add(actives);
		panel.add(close);
		panel.add(dter);
		panel.add(delete_trans);
		panel.add(open);
		panel.add(set_date);
		panel.add(setDate);
		panel.add(setPrice);
		panel.add(customer_report);
		panel.add(customerReport);
		panel.add(monthly_statement);

		frame.pack();
		frame.setContentPane(panel);

		frame.setVisible(true);

		return frame;
	}

	class InterestListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// add appropriate amount of interest to all accounts
			// average daily balance
			String interest = "UPDATE MarketAccount " + "set balance = balance + avgBalance*.03,"
					+ "profits = profits + avgBalance*.03 " + "where taxID = 5555";
			String interestResults = "select * from MarketAccount where taxID = 5555";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				boolean result = StarsRUs.statement.execute(interest);
				ResultSet resultSet = StarsRUs.statement.executeQuery(interestResults);
				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnsNumber = rsmd.getColumnCount();

				while (resultSet.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
						if (i > 1)
							System.out.print(",  ");
						String columnValue = resultSet.getString(i);
						System.out.print(columnValue);
					}
					System.out.println("");
				}

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Interest added.", interest, 1);
		}

	}

	class StatementListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ActivesListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class DTERListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String dter = "select C.name, C.state, M.profits from MarketAccount M join Customer C on C.taxID = M.taxID  where M.profits >= 10000";
			String results = "";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(dter);

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

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			JTextArea msg = new JTextArea(results);
			msg.setWrapStyleWord(true);

			JScrollPane scrollPane = new JScrollPane(msg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			JOptionPane.showMessageDialog(null, scrollPane);

		}

	}

	class ReportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String user = customer_report.getText();
			String report = "select C.name, A. stock_symbol, A.quantity, M.balance from Customer C "
					+ "join StockAccount A on C.taxID = A.taxID " + "join MarketAccount M on C.taxID = M.taxID "
					+ "where C.name = '" + user + "'";
			String results = "";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(report);

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

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

			JTextArea msg = new JTextArea(results);
			msg.setWrapStyleWord(true);

			JScrollPane scrollPane = new JScrollPane(msg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			JOptionPane.showMessageDialog(null, scrollPane);
		}

	}

	class DeleteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String delete = "delete from Transactions";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				boolean resultSet = StarsRUs.statement.execute(delete);
				System.out.println("Transactions Deleted");
				JOptionPane.showMessageDialog(null, "Transactions deleted for this month.", delete, 1);

			} catch (SQLException ev) {
				ev.printStackTrace();
			}
		}

	}

	class DateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String userInput = set_date.getText();
			String setDate = "update MarketInfo  set date = ' " + userInput + "'";
			String date = "SELECT DATEDIFF('" + userInput + "', (select date from MarketInfo))";
			int dateDiff = 0;
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				ResultSet resultSet = StarsRUs.statement.executeQuery(date);
				if (!resultSet.equals("")) {
					resultSet.next();
					dateDiff = Integer.parseInt(resultSet.getString(1));
					StarsRUs.statement.execute(setDate);
					for (int i = 0; i < dateDiff; i++) {
						close();
					}
					JOptionPane.showMessageDialog(null, "Date Changed");
				} else {
					JOptionPane.showMessageDialog(null, "Not a valid date");

				}

			} catch (SQLException ev) {
				ev.printStackTrace();
			}
		}
	}

	class OpenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String open = "update MarketInfo  set MarketOpen = 1";
			try {

				StarsRUs.statement = StarsRUs.connection.createStatement();
				boolean resultSet = StarsRUs.statement.execute(open);
				JOptionPane.showMessageDialog(null, "Market Opened.", "Open Market", 1);

			} catch (SQLException ev) {
				ev.printStackTrace();
			}

		}
	}

	class CloseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			close();
			JOptionPane.showMessageDialog(null, "Market Closed.", "Close Market", 1);

		}
	}

	class PriceListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public void close() {
		String close = "update MarketInfo  set MarketOpen = 0";
		String avgBalance = "update MarketAccount set avgBalance = (avgBalance + balance)/2 " + "where taxID = 5555";
		String close_price = "update Stock set daily_close_price = current_price";

		try {

			StarsRUs.statement = StarsRUs.connection.createStatement();
			StarsRUs.statement.addBatch(close);
			StarsRUs.statement.addBatch(avgBalance);
			StarsRUs.statement.addBatch(close_price);
			StarsRUs.statement.executeBatch();
			// System.out.println("Transactions Deleted");

		} catch (SQLException ev) {
			ev.printStackTrace();
		}
	}

}