import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLClient {

	private static Connection connection;
	private static String sessionID;

	public static void main(String[] args) {

		openConnection();

		printWelcomeMenu();
		inputHandler(1);

		printFeed();
		inputHandler(2);

		closeConnection();
	}

	private static void printFeed() {
		System.out.println("Welcome " + sessionID);

		try {
			String query = "SELECT Message, TimeS, Posted_By FROM( SELECT * FROM Post ORDER BY TimeS DESC LIMIT 10 ) Post ORDER BY TimeS DESC";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				System.out.println("\n\n" + rs.getString("Posted_By") + "\t\t\t" +rs.getString("TimeS") +"\n" + rs.getString("Message"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void printWelcomeMenu() {
		System.out.print("/=======================================\\\n" + "|===============  FUISM  ===============|\n"
				+ "\\=======================================/\n" + "\n" + "1.  Login\n" + "2.  Register\n"
				+ "3.  Quit\n" + ">> ");
	}

	private static void inputHandler(int i) {
		switch (i) {
		case 1:
			// Welcome Menu Handler
			int temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					login();
					temp = 0;
					break;
				case 2:
					register();
					temp = 0;
					break;
				case 3:
					quit();
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		case 2:
			// something else
			break;
		}
	}

	private static void quit() {
		System.out.println("Goodbye");
	}

	private static void register() {
		boolean flag = true;
		System.out.print("\n/=======================================\\\n" + "Username: ");

		String userName = Methods.getSterilizedString();

		try {
			String query = "SELECT UserName FROM Users WHERE UserName = \"" + userName + "\"";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				System.out.println("The username: " + userName + " is taken, please try again.");

				flag = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (flag) {

			System.out.print("Password: ");
			String password = Methods.getPassword();
			System.out.println("Your Password: " + password);
			password = Methods.getMd5(password);

			System.out.print("Screen Name: ");
			String screenName = Methods.getSterilizedString();

			try {
				String query = "Insert into Users ( UserName, ScreenName, Password ) Values ( ?, ?, ? )";

				PreparedStatement pst = connection.prepareStatement(query);

				pst.setString(1, userName);
				pst.setString(2, screenName);
				pst.setString(3, password);

				pst.executeUpdate(); // executeUpdate when the query modifies the DB
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			register();
		}

	}

	private static void login() {
		boolean flag = false;
		System.out.print("\n/=======================================\\\n" + "Username: ");

		String userName = Methods.getSterilizedString();

		System.out.print("Password: ");
		String password = Methods.getPassword();
		password = Methods.getMd5(password);
		System.out.println("\nLogging in: " + userName + "\n");

		try {
			String query = "SELECT UserName FROM Users WHERE UserName = \"" + userName + "\" AND Password = \""
					+ password + "\"";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				flag = true;
				sessionID = userName;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!flag) {
			System.out.println("Login Failed.");
			login();
		}
	}

	private static void openConnection() {
		String user = "project_3";
		String password = "V00851455";
		String database = "project_3";

		String url = "jdbc:mysql://3.234.246.29:3306/" + database;

		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}