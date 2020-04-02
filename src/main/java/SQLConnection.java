import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLConnection {

	private static Connection connection;

	public static void main(String[] args) {

		openConnection();

		selectEmployees();
		
		selectEmployeeByID("100");

		selectEmployeeByID("1 OR 1");

		selectEmployeeByIDPreparedStatement("100");

		selectEmployeeByIDPreparedStatementString("1 OR 1");

		addJob();
		
		addEmployeeReturnAutoID();
		
		deleteEmployeeByID(207);
 
		closeConnection();
	}

	private static void openConnection() {
		String user = "project_3";
		String password = "V00851455";
		String database = "pattersontj2";

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

	private static void selectEmployees() {
		try {
			String query = "SELECT employee_id, first_name, last_name, salary FROM employees";

			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);

			System.out.println("QUERY: " + query);
			System.out.println("=== RESULTS === ");

			while(rs.next()) {
				System.out.println("Employee ID: " + rs.getInt(1) + ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + ", Salary: " + rs.getDouble("salary"));
			}

			System.out.println("=== /RESULTS === ");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void selectEmployeeByID(String employee_id) {
		try {
			String query = "SELECT employee_id, first_name, last_name, salary FROM employees WHERE employee_id = " + employee_id + "";

			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(query);

			System.out.println("QUERY: " + query);
			System.out.println("=== RESULTS === ");

			while(rs.next()) {
				System.out.println("Employee ID: " + rs.getInt(1) + ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + ", Salary: " + rs.getDouble("salary"));
			}

			System.out.println("=== /RESULTS === ");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void selectEmployeeByIDPreparedStatement(String employee_id) {
		try {
			String query = "SELECT employee_id, first_name, last_name, salary FROM employees WHERE employee_id = ?";

			PreparedStatement pst = connection.prepareStatement(query);

			pst.setInt(1, Integer.valueOf(employee_id));

			ResultSet rs = pst.executeQuery(); // NO parameter, it's a prepared statement

			System.out.println("QUERY: " + query);
			System.out.println("=== RESULTS === ");

			while(rs.next()) {
				System.out.println("Employee ID: " + rs.getInt(1) + ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + ", Salary: " + rs.getDouble("salary"));
			}

			System.out.println("=== /RESULTS === ");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void selectEmployeeByIDPreparedStatementString(String employee_id) {
		try {
			String query = "SELECT employee_id, first_name, last_name, salary FROM employees WHERE employee_id = ?";

			PreparedStatement pst = connection.prepareStatement(query);

			pst.setString(1, employee_id);

			ResultSet rs = pst.executeQuery(); // NO parameter, it's a prepared statement

			System.out.println("QUERY: " + query);
			System.out.println("=== RESULTS === ");

			while(rs.next()) {
				System.out.println("Employee ID: " + rs.getInt(1) + ", Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + ", Salary: " + rs.getDouble("salary"));
			}

			System.out.println("=== /RESULTS === ");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void addJob() {

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the job ID: ");
		String job_id = scanner.next();

		System.out.print("Enter the job title: ");
		String job_title = scanner.next();

		System.out.print("Enter the minimum salary: ");
		float min_salary = scanner.nextFloat();

		System.out.print("Enter the maximum salary: ");
		float max_salary = scanner.nextFloat();
		
		scanner.close();

		try {
			String query = "INSERT INTO jobs (job_id, job_title, min_salary, max_salary) VALUES (?, ?, ?, ?)";

			PreparedStatement pst = connection.prepareStatement(query);

			pst.setString(1, job_id);
			pst.setString(2, job_title);
			pst.setFloat(3, min_salary);
			pst.setFloat(4, max_salary);

			pst.executeUpdate(); // executeUpdate when the query modifies the DB
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static int addEmployeeReturnAutoID() {

		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the last name: ");
		String last_name = scanner.next();

		System.out.print("Enter the email: ");
		String email = scanner.next();

		System.out.print("Enter the job ID: ");
		String job_id = scanner.next();
		// IDs shouldn't be a black-box input by the end user. The system should display a list of IDs easily enumerated so that the user can select one of the existing IDs.

		scanner.close();

		try {
			String query = "INSERT INTO employees (last_name, email, job_ID, hire_date) VALUES (?, ?, ?, CURDATE())";

			PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS); // RETURN ID AUTO_INCREMENT

			pst.setString(1, last_name);
			pst.setString(2, email);
			pst.setString(3, job_id);

			pst.executeUpdate(); // executeUpdate when the query modifies the DB
			
			ResultSet generatedKeys = pst.getGeneratedKeys();
			
			if (generatedKeys.next()) {
				int employee_id = generatedKeys.getInt(1);
				System.out.println("New employee ID: " + employee_id);
				return employee_id;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private static void deleteEmployeeByID(int employee_id) {

		try {
			String query = "DELETE FROM employees WHERE employee_id = ?";

			PreparedStatement pst = connection.prepareStatement(query);

			pst.setInt(1, employee_id);

			pst.executeUpdate(); // executeUpdate when the query modifies the DB
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}