import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLClient {

	// TODO: Error Handling

	private static Connection connection;
	private static String sessionID, currIDView;
	private static List<Integer> localResPostId;
	private static List<Integer> localResCommId;
	private static List<String> localResFriendId;
	private static int currPost, currComm;

	public static void main(String[] args) {

		openConnection();
		printWelcomeMenu();
		printFeed();
		closeConnection();
	}

	private static void printFeed() {
		currPost = -1;
		if (localResPostId == null) {
			localResPostId = new ArrayList<Integer>();
		} else {
			localResPostId.clear();
		}

		System.out.println("\n=========================================\nWelcome " + sessionID);

		try {
			String query = "SELECT Message, TimeS, Posted_By, Post_ID FROM( SELECT * FROM Post ORDER BY TimeS DESC LIMIT 10 ) Post ORDER BY TimeS DESC";

			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {

				try {
					String query1 = "SELECT ScreenName FROM Users WHERE UserName = \"" + rs.getString("Posted_By")
							+ "\"";
					PreparedStatement pst1 = connection.prepareStatement(query1);
					ResultSet rs1 = pst1.executeQuery();
					rs1.next();

					System.out.printf("\n\n%-20s %s\n%s", rs1.getString("ScreenName"), rs.getString("TimeS"),
							rs.getString("Message"));

					localResPostId.add(rs.getInt("Post_ID"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// FEED MENU
		System.out.print("\n\n1.  Select Post\n2.  Make Post\n3.  Profile\n>> ");
		inputHandler(2);
	}

	// WELCOME MENU
	private static void printWelcomeMenu() {
		System.out.print("/=======================================\\\n" + "|===============  FUISM  ===============|\n"
				+ "\\=======================================/\n" + "\n" + "1.  Login\n" + "2.  Register\n"
				+ "3.  Quit\n" + ">> ");
		inputHandler(1);
	}

	private static void inputHandler(int i) {
		switch (i) {
		// WELCOME MENU
		case 1:
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
		// FEED MENU
		case 2:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// View Post
					if (localResPostId.isEmpty()) {
						System.out.print("No posts.\n>> ");
						inputHandler(2);
					} else {
						System.out.print("Input post number: ");
						int index = localResPostId.get(Methods.getInt() - 1);
						displayPost(index);
					}
					temp = 0;
					break;
				case 2:
					// make post
					makePost();
					temp = 0;
					break;
				case 3:
					// view profile
					displayProfile(sessionID);
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		// POST MENU
		case 3:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					printFeed();
					temp = 0;
					break;
				case 2:
					// View comment
					if (localResCommId.isEmpty()) {
						System.out.print("No comments.\n>> ");
						inputHandler(3);
					} else {
						System.out.print("Input comment number: ");
						int index = localResCommId.get(Methods.getInt() - 1);
						displayComment(index);
					}
					temp = 0;
					break;
				case 3:
					// make comment
					makeComment();
					temp = 0;
					break;
				case 4:
					// Like post
					likePost();
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		// PROFILE MENU
		case 4:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					printFeed();
					temp = 0;
					break;
				case 2:
					// BF profile
					displayBFProfile(sessionID);
					temp = 0;
					break;
				case 3:
					viewPosts(currIDView);
					temp = 0;
					break;
				case 4:
					// View Friends
					viewFriends();
					temp = 0;
					break;
				case 5:
					// Update Screen Name
					System.out.print("Input new screen name: ");
					String name = Methods.getSterilizedString();
					updateScreenName(name);
					temp = 0;
					break;
				case 6:
					// Search User
					System.out.print("Input username: ");
					String username = Methods.getSterilizedString();
					displayProfile(username);
					temp = 0;
					break;
				case 7:
					// Logout
					printWelcomeMenu();
					printFeed();
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		case 5:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					displayProfile(sessionID);
					temp = 0;
					break;
				case 2:
					// BF profile
					displayBFProfile(currIDView);
					temp = 0;
					break;
				case 3:
					// View Posts by user
					viewPosts(currIDView);
					temp = 0;
					break;
				case 4:
					// Toggle Friend
					toggleFriend();
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		case 6:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					displayProfile(sessionID);
					temp = 0;
					break;
				case 2:
					// Select Friend
					if (localResFriendId.isEmpty()) {
						System.out.print("No friends.\n>> ");
						inputHandler(6);
					} else {
						System.out.print("Input friend number: ");
						int index = Methods.getInt() - 1;
						displayProfile(localResFriendId.get(index));
					}
					temp = 0;
					break;
				case 3:
					// Make Best Friend
					if (localResFriendId.isEmpty()) {
						System.out.print("No friends.\n>> ");
						inputHandler(6);
					} else {
						System.out.print("Input friend number: ");
						int index1 = Methods.getInt() - 1;
						makeBestFriend(localResFriendId.get(index1));
					}
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		case 7:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					displayPost(currPost);
					temp = 0;
					break;
				case 2:
					// Like Comment
					likeComment();
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;
		case 8:
			temp = 1;
			while (temp != 0) {
				switch (Methods.getInt()) {
				case 1:
					// Back
					displayProfile(currIDView);
					temp = 0;
					break;
				case 2:
					// Display Post
					if(localResPostId.isEmpty()) {
						System.out.print("No posts.\n>> ");
						inputHandler(8);
					}
					System.out.print("Input post number: ");
					int index = localResPostId.get(Methods.getInt() - 1);
					displayPost(index);
					temp = 0;
					break;
				default:
					System.out.print(">> ");
					break;
				}
			}
			break;

		default:
			break;
		}

	}

	private static void likeComment() {
		boolean liked = false;
		String query = "SELECT CL_ID FROM Comment_Like WHERE Liked_By = '" + sessionID + "' AND Comment_ID = '"
				+ currComm + "'";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				liked = true;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (liked) {
			query = "DELETE FROM Comment_Like WHERE Liked_By = '" + sessionID + "' and Comment_ID = " + currComm + "";
			try {
				PreparedStatement pst = connection.prepareStatement(query);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			query = "INSERT INTO Comment_Like(Comment_Like.Liked_By, Comment_Like.Comment_ID, Comment_Like.TimeS) VALUES ('"
					+ sessionID + "', " + currComm + ", CURRENT_TIMESTAMP)";
			try {
				PreparedStatement pst = connection.prepareStatement(query);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		printFeed();
	}

	private static void makeComment() {
		System.out.print("Enter your comment message: ");

		String message = Methods.getSterilizedMessage();
		String query = "INSERT INTO Comments(Comments.Commented_By, Comments.Post_ID, Comments.Content, Comments.TimeS) VALUES ('"
				+ sessionID + "', " + currPost + ", '" + message + "', CURRENT_TIMESTAMP)";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		displayPost(currPost);
	}

	private static void makePost() {
		System.out.print("Enter your post message: ");

		String message = Methods.getSterilizedMessage();
		String query = "INSERT INTO Post(Post.Message, Post.Posted_By, Post.TimeS) VALUES('" + message + "', '"
				+ sessionID + "' ,CURRENT_TIMESTAMP)";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		printFeed();
	}

	private static void displayBFProfile(String Username) {
		String query = "SELECT Best_Friends.Friend_UserName FROM Best_Friends WHERE Best_Friends.User_UserName = \'"
				+ Username + "\'";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			rs.next();
			String user = rs.getString("Friend_UserName");
			displayProfile(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void makeBestFriend(String username) {
		String delete = "DELETE FROM `Best_Friends` WHERE `User_UserName` = \'" + sessionID + "\'";
		try {
			PreparedStatement pst = connection.prepareStatement(delete);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String query = "INSERT INTO `Best_Friends`(`User_UserName`, `Friend_UserName`, `TimeS`) VALUES (\'" + sessionID
				+ "\', \'" + username + "\',CURRENT_TIMESTAMP)";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		displayProfile(sessionID);
	}

	private static void likePost() {
		boolean liked = false;
		String q = "SELECT PL_ID FROM Post_Like WHERE Post_ID = \'" + currPost + "\' AND Liked_By = \'" + sessionID
				+ "\'";
		try {
			PreparedStatement pst = connection.prepareStatement(q);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				liked = true;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (liked) {
			String query = "DELETE FROM `Post_Like` WHERE Post_ID = \'" + currPost + "\' AND Liked_By = \'" + sessionID
					+ "\'";
			try {
				PreparedStatement pst = connection.prepareStatement(query);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String query = "INSERT INTO `Post_Like`(`Liked_By`, `Post_ID`, `TimeS`) VALUES (\'" + sessionID + "\', \'"
					+ currPost + "\' , CURRENT_TIMESTAMP)";
			try {
				PreparedStatement pst = connection.prepareStatement(query);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		printPost(currPost);
	}

	private static void updateScreenName(String name) {
		String query = "update Users set ScreenName = \"" + name + "\" where UserName = \"" + sessionID + "\"";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		displayProfile(sessionID);
	}

	private static void toggleFriend() {
		String query = "select User_2 from Friends where User_1= \"" + sessionID + "\"";
		boolean isFriend = false;
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String temp1 = rs.getString("User_2");
				String temp2 = currIDView;
				if (temp2.equals(temp1)) {
					isFriend = true;
					break;
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		if (isFriend) {
			String query1 = "DELETE FROM Friends WHERE User_1 = \"" + sessionID + "\"and User_2 =\"" + currIDView
					+ "\"";
			try {
				PreparedStatement pst = connection.prepareStatement(query1);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			String query1 = "INSERT INTO `Friends`(`User_1`, `User_2`, `TimeS`) VALUES (\'" + sessionID + "\',\'"
					+ currIDView + "\',CURRENT_TIMESTAMP)";
			try {
				PreparedStatement pst = connection.prepareStatement(query1);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		displayProfile(currIDView);
	}

	private static void viewPosts(String user) {
		if (localResPostId == null) {
			localResPostId = new ArrayList<Integer>();
		} else {
			localResPostId.clear();
		}

		String query = "select Post_ID, Message,TimeS from Post where Posted_By = \"" + user + "\"";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				printPost(rs.getInt("Post_ID"));
				localResPostId.add(rs.getInt("Post_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// View User's posts menu
		System.out.print("\n1.  Back\n2.  Select Post\n>> ");
		inputHandler(8);
	}

	private static void printPost(int index) {
		String query = "SELECT * FROM Post WHERE Post_ID = \"" + index + "\"";

		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			rs.next();
			try {
				String query1 = "SELECT ScreenName FROM Users WHERE UserName = \"" + rs.getString("Posted_By") + "\"";
				PreparedStatement pst1 = connection.prepareStatement(query1);
				ResultSet rs1 = pst1.executeQuery();
				rs1.next();

				System.out.printf("\n\n%-20s %s\n%s", rs1.getString("ScreenName"), rs.getString("TimeS"),
						rs.getString("Message"));

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void viewFriends() {
		if (localResFriendId == null) {
			localResFriendId = new ArrayList<String>();
		} else {
			localResFriendId.clear();
		}
		String query = "select User_2 from Friends where User_1= \"" + currIDView + "\"";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			int i = 1;
			System.out.println("\n---------------------------------------");
			while (rs.next()) {
				System.out.printf("\n%d: %-20s\n", i, rs.getString("User_2"));
				localResFriendId.add(rs.getString("User_2"));
				++i;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		System.out.print("\n1.  Back\n2.  Select Friend\n3.  Make Best Friend\n>> ");
		inputHandler(6);
	}

	private static void displayProfile(String user) {
		currIDView = user;
		System.out.println("\n---------------------------------------");

		String query = "SELECT Users.ScreenName, COUNT(Post.Post_ID) AS No_Of_Posts_Created, Best_Friends.User_UserName, Best_Friends.Friend_UserName FROM Users "
				+ "LEFT JOIN Best_Friends ON Users.UserName = Best_Friends.User_UserName OR Users.UserName = Best_Friends.Friend_UserName LEFT JOIN Post ON Users.UserName = Post.Posted_By WHERE Users.UserName = \""
				+ user + "\"GROUP BY Users.UserName, Best_Friends.User_UserName, Best_Friends.Friend_UserName";

		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				String friend = rs.getString("Friend_UserName");
				if (friend == user) {
					friend = rs.getString("User_UserName");
				}
				if (friend == null) {
					friend = "None";
				}

				System.out.printf("Username: %-20s Screen Name: %-20s\nPosts: %d\n\nBest Friend: %-20s\n\n", user,
						rs.getString("ScreenName"), rs.getInt("No_Of_Posts_Created"), friend);
			} else {
				System.out.println("User does not exist.");
				displayProfile(sessionID);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (user == sessionID) {
			// PROFILE MENU
			System.out.print(
					"1.  Back\n2.  Go to BF profile\n3.  View posts by user\n4.  View Friends\n5.  Change Screen Name\n6.  Search User\n7.  Logout\n>> ");
			inputHandler(4);
		} else {
			// PROFILE NOT YOU MENU

			String query1 = "select User_2 from Friends where User_1= \"" + sessionID + "\"";
			boolean isFriend = false;
			try {
				PreparedStatement pst = connection.prepareStatement(query1);
				ResultSet rs = pst.executeQuery();
				while (rs.next()) {

					if (rs.getString("User_2").equals(user)) {

						isFriend = true;
					}
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}

			if (isFriend) {
				System.out.print("1.  Back\n2.  Go to BF profile\n3.  View posts by user\n4.  Remove Friend\n>> ");
				inputHandler(5);
			} else {
				System.out.print("1.  Back\n2.  Go to BF profile\n3.  View posts by user\n4.  Add Friend\n>> ");
				inputHandler(5);
			}
		}
	}

	private static void displayComment(int index) {
		currComm = index;
		String query = "SELECT * FROM Comments WHERE Comment_ID = \"" + index + "\"";
		try {
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			rs.next();
			try {
				String query1 = "SELECT ScreenName FROM Users WHERE UserName = \"" + rs.getString("Commented_By")
						+ "\"";
				PreparedStatement pst1 = connection.prepareStatement(query1);
				ResultSet rs1 = pst1.executeQuery();
				rs1.next();

				System.out.printf("\n\n%-20s %s\n%s", rs1.getString("ScreenName"), rs.getString("Comments.TimeS"),
						rs.getString("Content"));

				localResCommId.add(rs.getInt("Comment_ID"));

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Comment menu
		System.out.print("\n\n1.  Back\n2.  Like Comment\n>> ");
		inputHandler(7);
	}

	private static void displayPost(int index) {
		currPost = index;
		if (localResCommId == null) {
			localResCommId = new ArrayList<Integer>();
		} else {
			localResCommId.clear();
		}
		printPost(index);
		System.out.println("\n---------------------------------------");
		String commQuery = "SELECT Commented_By, content, Comments.TimeS, Comment_ID FROM Comments WHERE Comments.Post_ID = \""
				+ index + "\" ORDER BY Comments.TimeS DESC LIMIT 10 ";
		try {
			PreparedStatement pst = connection.prepareStatement(commQuery);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				try {
					String query1 = "SELECT ScreenName FROM Users WHERE UserName = \"" + rs.getString("Commented_By")
							+ "\"";
					PreparedStatement pst1 = connection.prepareStatement(query1);
					ResultSet rs1 = pst1.executeQuery();
					rs1.next();

					System.out.printf("\n\n%-20s %s\n%s", rs1.getString("ScreenName"), rs.getString("Comments.TimeS"),
							rs.getString("Content"));

					localResCommId.add(rs.getInt("Comment_ID"));

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// POST MENU
		System.out.print("\n\n1.  Back\n2.  Select Comment\n3.  Make Comment\n4.  Like Post\n>> ");
		inputHandler(3);
	}

	private static void quit() {
		System.out.println("Goodbye");
		System.exit(0);
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