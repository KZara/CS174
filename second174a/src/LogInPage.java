import Database.DbClient;
import Database.DbQuery;
import Database.RetrievalQuery;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class LogInPage {

	static JFrame frame;
	static JTextField username;
	static JPasswordField password;
	static boolean isManager;

	public static void createLogInPage() {

		frame = new JFrame("Log In");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(1000, 1000);
		frame.getContentPane().setPreferredSize(d);
		JPanel panel = new JPanel(new GridLayout(4, 4, 4, 4));

		JLabel username_label = new JLabel("Username:");
		JLabel password_label = new JLabel("Password:");

		username = new JTextField(20);
		password = new JPasswordField(20);

		panel.add(username_label);
		panel.add(username);
		panel.add(password_label);
		panel.add(password);
		JCheckBox isAdmin = new JCheckBox("Admin Login");
		panel.add(isAdmin);
		LogInPage logIn = new LogInPage();
		JButton enterButton = new JButton("Enter");
		
		
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isAdmin.isSelected()) {
					isManager = true;
				}
				// check if username and password are good
				StringBuilder checkList = new StringBuilder("SELECT USERS.username ");
				if (isManager) {
					checkList = checkList.append("FROM Manager USERS ");
				} else {
					checkList = checkList.append("FROM Customer USERS ");
				}

				checkList = checkList.append("WHERE USERS.username = \'").append(username.getText())
						.append("\' AND USERS.password = \'").append(password.getPassword()).append("\'");
				System.out.println(checkList.toString());
				DbClient.getInstance().runQuery(new RetrievalQuery(checkList.toString()) {
					@Override
					public void onComplete(ResultSet result) {
						try {
							if (!result.next()) {
								JOptionPane.showMessageDialog(null, "No match for username/password", "Error Message",
										0);
								java.lang.System.exit(0);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				// go to new page
				frame.setVisible(false);
				frame.dispose();

				if (isManager) {
					ManagerDashboard.createDashboard(username.getText());
				} else {
					TraderDashboard.createDashboard(username.getText());
				}

			}

		});
		panel.add(enterButton);

		frame.setContentPane(panel);
		frame.pack();

		frame.setVisible(true);

	}

}
