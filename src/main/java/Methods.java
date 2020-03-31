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
	
	public static String getString() {
		String temp = input.nextLine();
		char[] c = temp.toCharArray();
		temp = "";
		for(int i = 0; i < c.length; ++ i) {
			if(Character.isLetter(c[i]) || Character.isDigit(c[i])) {
				temp += c[i];
			}
		}
		return temp;
	}
}
