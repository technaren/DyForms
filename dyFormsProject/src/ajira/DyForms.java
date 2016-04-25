package ajira;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DyForms {

	public static Scanner scanner = new Scanner(System.in);

	public static void main(String args[]) {
		System.out.println("Enter input JSON: ");
		JSONObject inputJSON = getIputJSON();

		System.out.println("Select mode of input:\n1. Manual\n2. Automated");
		int choice = scanner.nextInt();
		InputType inputType = null;
		if (choice == 1) {
			inputType = new ManualInput();
		} else if (choice == 2) {
			System.out
					.println("Automated flow :\n1. NEW -> COMPLETED\n2. NEW -> CANCELLED -> ENDUSER\n3. NEW -> CANCELLED -> OTHERS");
			choice = scanner.nextInt();
			inputType = new AutomatedInput(choice);
		} else {
			System.out.println("Rerun the program & provide valid input!");
		}

		if (inputType != null) {
			generateDyForm(inputJSON, inputType);
		}

	}

	private static void generateDyForm(JSONObject inputJSON, InputType inputType) {
		printActiveFields(inputJSON);
		getInput(inputJSON, inputType);
	}

	@SuppressWarnings({ "unchecked" })
	private static void getInput(JSONObject inputJSON, InputType inputType) {
		JSONObject fieldNameLocationJSON = getFieldNameLocationJSON(inputJSON);
		String activeField = null;
		String fieldType = null;
		Object input = null;

		for (int i = 1; i <= inputJSON.size(); i++) {
			String fieldLocation = Integer.toString(i);
			JSONObject jsonField = (JSONObject) inputJSON.get(fieldLocation);
			activeField = (String) jsonField.get(Constants.activeField);
			if (Boolean.valueOf(activeField)
					&& (jsonField.get(Constants.fieldValue) == null || jsonField.get(Constants.fieldValue).toString()
							.isEmpty())) {
				fieldType = (String) jsonField.get(Constants.fieldType);

				if (!Constants.enumField.equals(fieldType)) {
					System.out.println("Enter " + jsonField.get(Constants.fieldName) + ": ");
					input = (inputType instanceof ManualInput) ? inputType.getInput(fieldType) : inputType
							.getInput((String) jsonField.get(Constants.fieldName));
					jsonField.put(Constants.fieldValue, input);
					inputJSON.put(fieldLocation, jsonField);
				} else {
					Object enumValidation = true;
					while (enumValidation instanceof Boolean) {
						System.out.println("Select " + jsonField.get(Constants.fieldName) + ": "
								+ jsonField.get(Constants.fieldOptions));
						input = (inputType instanceof ManualInput) ? inputType.getInput(Constants.text) : inputType
								.getInput((String) jsonField.get(Constants.fieldName));
						enumValidation = validateSelection(inputJSON, fieldLocation, input, fieldNameLocationJSON);
					}
					inputJSON = (JSONObject) enumValidation;
				}
				printActiveFields(inputJSON);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object validateSelection(JSONObject inputJSON, String fieldLocation, Object inputValue,
			JSONObject fieldNameLocationJSON) {
		JSONObject jsonField = (JSONObject) inputJSON.get(fieldLocation);
		List fieldOptions = (List) jsonField.get(Constants.fieldOptions);
		if (fieldOptions.contains(inputValue)) {
			jsonField.put(Constants.fieldValue, inputValue);
			inputJSON.put(fieldLocation, jsonField);
			if (jsonField.get(Constants.activateField) != null) {
				String fieldToBeActivated = (String) ((JSONObject) jsonField.get(Constants.activateField))
						.get(inputValue);
				if (fieldToBeActivated != null) {
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
			fieldNameLocationJSON.put(((JSONObject) inputJSON.get(fieldLocation)).get(Constants.fieldName),
					fieldLocation);
		}
		return fieldNameLocationJSON;
	}

	@SuppressWarnings("unchecked")
	private static void printActiveFieldsInJSONFormat(JSONObject inputJSON) {
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

	@SuppressWarnings("unchecked")
	private static void printActiveFields(JSONObject inputJSON) {
		JSONObject activeFields = new JSONObject();
		List<String> activeFieldsList = new ArrayList<String>();
		for (int i = 1; i <= inputJSON.size(); i++) {
			JSONObject jsonField = (JSONObject) inputJSON.get(Integer.toString(i));
			StringBuilder activeField = new StringBuilder();
			;
			if (Boolean.valueOf((String) jsonField.get(Constants.activeField))) {
				activeFields.put(jsonField.get(Constants.fieldName),
						(jsonField.get(Constants.fieldValue) != null) ? jsonField.get(Constants.fieldValue) : "");
				activeField.append(jsonField.get(Constants.fieldName));
				activeField = (jsonField.get(Constants.fieldValue) != null) ? activeField.append("['")
						.append(jsonField.get(Constants.fieldValue)).append("']") : activeField;
				activeFieldsList.add(activeField.toString());
			}
		}
		System.out.println("Active Fields -" + activeFieldsList);
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
