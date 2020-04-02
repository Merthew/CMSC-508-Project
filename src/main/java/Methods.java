import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Methods {

	private static Scanner input = new Scanner(System.in);

	public static int getInt() {
		while (true) {
			if (input.hasNextInt()) {
				return input.nextInt();
			} else {
				System.out.print(">> ");
				input.next();
			}
		}
	}

	public static String getSterilizedString() {

		String temp = input.nextLine();
		if (temp.equals("")) {
			temp = input.nextLine();
		}
		char[] c = temp.toCharArray();
		temp = "";
		for (int i = 0; i < c.length; ++i) {
			if (Character.isLetter(c[i]) || Character.isDigit(c[i])) {
				temp += c[i];
			}
		}
		return temp;

	}

	public static String getPassword() {
		ArrayList<Character> allowedPasswordCharacters = new ArrayList<Character>();
		allowedPasswordCharacters.add('-');
		allowedPasswordCharacters.add('_');
		allowedPasswordCharacters.add('\\');
		allowedPasswordCharacters.add('/');
		allowedPasswordCharacters.add('|');
		String temp = input.nextLine();
		char[] c = temp.toCharArray();
		temp = "";
		for (int i = 0; i < c.length; ++i) {
			if (Character.isLetter(c[i]) || Character.isDigit(c[i]) || allowedPasswordCharacters.contains(c[i])) {
				temp += c[i];
			}
		}
		return temp;
	}

	// Stole this from geeksforgeeks
	public static String getMd5(String input) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
