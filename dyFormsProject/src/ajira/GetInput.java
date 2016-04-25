package ajira;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONObject;

public class GetInput {
	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		String input = "";
		Scanner scanner = new Scanner(System.in);
		List<JSONObject> inputJSONList = new ArrayList<JSONObject>();
		JSONObject inputJSON = null;
		while (!input.equalsIgnoreCase("exit")) {
			inputJSON = new JSONObject();
			System.out.println("Enter fieldName: ");
			inputJSON.put("fieldName", scanner.nextLine());
			System.out.println("Is activeField?: ");
			inputJSON.put("activeField", scanner.nextLine());
			System.out.println("Enter fieldType: ");
			input = scanner.nextLine();
			inputJSON.put("fieldType", input);
			if (input.equalsIgnoreCase("enum")) {
				List<String> list = new ArrayList<String>();
				System.out.println("Enter fieldValues(comma separated!): ");
				input = scanner.nextLine();
				list.add(input);
				list.add("test");
				inputJSON.put("options", list);
			}
			inputJSONList.add(inputJSON);
			System.out.println("Proceed? 1.exit 2.no");
			input = scanner.nextLine();
		}
		System.out.println(inputJSONList);
	}
}
