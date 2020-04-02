import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLClient {

	private static Connection connection;

	public static void main(String[] args) {

		openConnection();

		printWelcomeMenu();
		inputHandler(1);

		closeConnection();
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
		
	}

	private static void login() {
		System.out.print("\n/=======================================\\\n" + "Username: ");
		Methods.getSterilizedString();
		String userName = Methods.getSterilizedString();

		System.out.print("Password: ");
		String password = Methods.getPassword();
		password = Methods.getMd5(password);
		System.out.println("\nLogging in: " + userName + "\n");
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