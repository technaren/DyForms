package ajira;

import java.util.Scanner;

public class ManualInput implements InputType {

	public static Scanner scanner;
	
	String input = null;
	
	int numericInput = 1;
	
	public ManualInput() {
		scanner = new Scanner(System.in);
	}
	
	@Override
	public Object getInput(String fieldType) {
		switch (fieldType) {
		case Constants.text :
			return getStringInput();
		case Constants.number :
			return getNumericInput();
		}
		return input;
	}
	
	private String getStringInput() {
		return scanner.nextLine();
	}
	
	private int getNumericInput() {
		while (!scanner.hasNextInt()) {
			scanner.next();
			System.out.println("Enter a numeric value: ");
		}
		numericInput = scanner.nextInt();
		return numericInput;
	}

}
