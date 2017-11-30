import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class WelcomePage {

	static JFrame frame;

	public static JFrame createFrame() {
		frame = new JFrame("Stars R Us Stock Exchange System");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension d = new Dimension(900, 900);
		frame.getContentPane().setPreferredSize(d);
		JPanel panel = new JPanel(new GridLayout(0,1));
		JLabel label1 = new JLabel("Welcome to Stars R Us Stock Exchange System");
		label1.setVerticalAlignment(JLabel.CENTER);
		label1.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label1);
		Dimension button_size = new Dimension(20, 20);
		
		WelcomePage welcome = new WelcomePage();
		JButton debugButton = new JButton("Debug");
		debugButton.addActionListener(welcome.new DebugListener());
		debugButton.setPreferredSize(button_size);
		
		
		JButton logInButton = new JButton("Log In");
		logInButton.addActionListener(welcome.new LogInListener());
		logInButton.setPreferredSize(button_size);
		panel.add(logInButton);
		panel.add(debugButton);
		
		frame.pack();
		frame.setContentPane(panel);
		frame.setVisible(true);

		return frame;
	}

	class DebugListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.setVisible(false);
			frame.dispose();
			// DebugPage.createSignUpPage();
		}

	}

	class LogInListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.setVisible(false);
			frame.dispose();
			LogInPage.createLogInPage();

		}

	}

}