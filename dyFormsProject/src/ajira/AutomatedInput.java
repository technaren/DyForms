package ajira;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class AutomatedInput implements InputType {
	
	int automatedInputChoice = 1;
	
	static JSONObject automatedInputs = new JSONObject();
	
	JSONObject selectedChoice = null;
	
	static {
		JSONObject tempJSONObject = new JSONObject();
		tempJSONObject.put(Constants.CreatedBy, "NEW -> COMPLETED");
		tempJSONObject.put(Constants.Description, "Internet is not working");
		tempJSONObject.put(Constants.Severity, 1);
		tempJSONObject.put(Constants.Status, "COMPLETED");
		tempJSONObject.put(Constants.Comments, "COMPLETED");
		automatedInputs.put(1, tempJSONObject);
		
		tempJSONObject = new JSONObject();
		tempJSONObject.put(Constants.CreatedBy, "NEW -> CANCELLED -> ENDUSER");
		tempJSONObject.put(Constants.Description, "Internet is not working");
		tempJSONObject.put(Constants.Severity, 1);
		tempJSONObject.put(Constants.Status, "CANCELLED");
		tempJSONObject.put(Constants.CancelledReason, "ENDUSER");
		tempJSONObject.put(Constants.Comments, "ENDUSER CANCELLED");
		automatedInputs.put(2, tempJSONObject);
		
		tempJSONObject = new JSONObject();
		tempJSONObject.put(Constants.CreatedBy, "NEW -> CANCELLED -> OTHERS");
		tempJSONObject.put(Constants.Description, "Internet is not working");
		tempJSONObject.put(Constants.Severity, 1);
		tempJSONObject.put(Constants.Status, "CANCELLED");
		tempJSONObject.put(Constants.CancelledReason, "OTHERS");
		tempJSONObject.put(Constants.CancelledOtherDescription, "CancelledOtherDescription");
		automatedInputs.put(3, tempJSONObject);
		
	}
	
	AutomatedInput(int choice) {
		automatedInputChoice = choice;
		if (choice > 0 && choice < 5) {
			selectedChoice = (JSONObject) automatedInputs.get(choice);
		}
	}
	
	@Override
	public Object getInput(String field) {
		switch (field) {
		case Constants.CreatedBy :
			return selectedChoice.get(Constants.CreatedBy);
		case Constants.Description :
			return selectedChoice.get(Constants.Description);
		case Constants.Status :
			return selectedChoice.get(Constants.Status);
		case Constants.CancelledReason :
			return selectedChoice.get(Constants.CancelledReason);
		case Constants.CancelledOtherDescription :
			return selectedChoice.get(Constants.CancelledOtherDescription);
		case Constants.Severity :
			return selectedChoice.get(Constants.Severity);
		case Constants.Comments :
			return selectedChoice.get(Constants.Comments);
		}
		return null;
	}

}
