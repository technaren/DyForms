package ajira;

import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DyForms {

	public static Scanner scanner = new Scanner(System.in);

	public static void main(String args[]) {
		JSONObject inputJSON = getIputJSON();

		System.out.println("Select mode of input:\n1. Manual\n2. Automated");
		int choice = scanner.nextInt();
		InputMethod inputMethod = null;
		if (choice == 1) {
			inputMethod = new ManualInput();
		} else if (choice == 2) {
			System.out
					.println("Automated flow :\n1. NEW -> COMPLETED\n2. NEW -> CANCELLED -> ENDUSER\n3. NEW -> CANCELLED -> OTHERS");
			choice = scanner.nextInt();
			inputMethod = new AutomatedInput(choice);
		}

		if (inputMethod != null) {
			generateDyForm(inputJSON, inputMethod);
		}

	}

	private static void generateDyForm(JSONObject inputJSON, InputMethod inputMethod) {
		printActiveFields(inputJSON);
		getInput(inputJSON, inputMethod);
	}

	@SuppressWarnings({ "unchecked"})
	private static void getInput(JSONObject inputJSON, InputMethod inputMethod) {
		JSONObject fieldNameLocationJSON = getFieldNameLocationJSON(inputJSON);
		String activeField = null;
		String fieldType = null;
		Object input = null;
		
		for (int i = 1; i <= inputJSON.size(); i++) {
			String fieldLocation = Integer.toString(i);
			JSONObject jsonField = (JSONObject) inputJSON.get(fieldLocation);
			activeField = (String) jsonField.get(Constants.activeField);
			if (Boolean.valueOf(activeField) && (jsonField.get(Constants.fieldValue) == null || jsonField.get(Constants.fieldValue).toString().isEmpty())) {
				fieldType = (String) jsonField.get(Constants.fieldType);
				
				if (!Constants.enumField.equals(fieldType)) {
					System.out.println("Enter " + jsonField.get(Constants.fieldName) + ": ");
					input = (inputMethod instanceof ManualInput) ? inputMethod.getInput(fieldType) : inputMethod.getInput((String)jsonField.get(Constants.fieldName));
					jsonField.put(Constants.fieldValue, input);
					inputJSON.put(fieldLocation, jsonField);
				} else {
					Object enumValidation = true;
					while (enumValidation instanceof Boolean) {
						System.out.println("Select "+ jsonField.get(Constants.fieldName) + ": " + jsonField.get(Constants.fieldOptions));
						input = (inputMethod instanceof ManualInput) ? inputMethod.getInput(Constants.text) : inputMethod.getInput((String)jsonField.get(Constants.fieldName));
						enumValidation =  validateSelection(inputJSON, fieldLocation, input, fieldNameLocationJSON);
					}
						inputJSON = (JSONObject) enumValidation;
				}
				printActiveFields(inputJSON);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object validateSelection(JSONObject inputJSON, String fieldLocation, Object inputValue, JSONObject fieldNameLocationJSON) {
		JSONObject jsonField = (JSONObject) inputJSON.get(fieldLocation);
		List fieldOptions = (List) jsonField.get(Constants.fieldOptions);
		if (fieldOptions.contains(inputValue)) {
			jsonField.put(Constants.fieldValue, inputValue);
			inputJSON.put(fieldLocation, jsonField);
			if (jsonField.get(Constants.activateField) != null) {
				String fieldToBeActivated = (String) ((JSONObject) jsonField.get(Constants.activateField)).get(inputValue);
				if(fieldToBeActivated != null) {
					String fieldLocationToBeActivated = (String) fieldNameLocationJSON.get(fieldToBeActivated);
					JSONObject activateJSON = (JSONObject) inputJSON.get(fieldLocationToBeActivated);
					activateJSON.put(Constants.activeField, "true");
					inputJSON.put(fieldLocationToBeActivated, activateJSON);
				}
			}
			return inputJSON;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getFieldNameLocationJSON(JSONObject inputJSON) {
		JSONObject fieldNameLocationJSON = new JSONObject();
		String fieldLocation = null;
		for (int i = 1; i <= inputJSON.size(); i++) {
			fieldLocation = Integer.toString(i);
			fieldNameLocationJSON.put(((JSONObject) inputJSON.get(fieldLocation)).get(Constants.fieldName), fieldLocation);
		}
		return fieldNameLocationJSON;
	}

	@SuppressWarnings("unchecked")
	private static void printActiveFields(JSONObject inputJSON) {
		JSONObject activeFields = new JSONObject();
		for (int i = 1; i <= inputJSON.size(); i++) {
			JSONObject jsonField = (JSONObject) inputJSON.get(Integer.toString(i));
			String activeField = (String) jsonField.get(Constants.activeField);
			if (Boolean.valueOf(activeField)) {
				activeFields.put(jsonField.get(Constants.fieldName),
						(jsonField.get(Constants.fieldValue) != null) ? jsonField.get(Constants.fieldValue) : "");
			}
		}
		System.out.println("Active Fields - " + activeFields);
	}

	private static JSONObject getIputJSON() {
		String inputJSONString = scanner.nextLine();
		JSONParser parser = new JSONParser();
		
		JSONObject inputJSON = null;
		try {
			inputJSON = (JSONObject) parser.parse(inputJSONString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return inputJSON;
	}
}
