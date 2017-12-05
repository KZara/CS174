
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
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
		JButton go_1 = new JButton("Customer Report");
		go_1.addActionListener(d.new Go1Listener());

		JButton delete_trans = new JButton("Delete Transactions");
		delete_trans.addActionListener(d.new DeleteListener());

		panel.add(add_interest);
		panel.add(monthly_statement);
		panel.add(actives);
		panel.add(dter);
		panel.add(customer_report);
		panel.add(go_1);
		panel.add(delete_trans);

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
			String interest = "UPDATE MarketAccount " + "set balance = balance + avgBalance*.03" + "where taxID = 5555";
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

	class Go1Listener implements ActionListener {

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

}