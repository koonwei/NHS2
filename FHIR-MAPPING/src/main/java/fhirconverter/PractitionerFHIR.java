package fhirconverter;
import org.json.JSONObject;

import com.github.fge.jsonpatch.JsonPatch; 

public class PractitionerFHIR {
	protected JSONObject read(String id) throws Exception{
		JSONObject a = new JSONObject();
		return a;
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		JSONObject a = new JSONObject();
		
		return a;
	}
	
	protected String update(String id, JSONObject patient) {
		
		return "";
	}

	
	protected String patch(String id, JsonPatch parameters) {
		
		
		return "";
		
	}
	
	protected String create(JSONObject patient) {
		
		return "";
	}
	
	protected String delete(String id) {
		
		return "";
	}

	public String convertFHIR(){ // call for converting, change this to practitioner	
		return "Practitioner";
	}

	

	/* (non-Javadoc)
	 * @see fhirconverter.OpenEMPIbase#create(org.json.JSONObject)
	
	@Override
	protected String create(JSONObject patient) 
	 * @see fhirconverter.OpenEMPIbase#search(java.util.HashMap)
	
	@Override
 */
}

