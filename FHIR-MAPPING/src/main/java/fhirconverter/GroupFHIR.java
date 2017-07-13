package fhirconverter;
import org.json.JSONObject;
import com.github.fge.jsonpatch.JsonPatch;


public class GroupFHIR extends OpenEMPIbase {
	protected JSONObject read(String id) {
		JSONObject a = new JSONObject();
		return a;
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		//this.commonSearchPersonByAttributes(parameters);
		
		JSONObject a = new JSONObject();
		
		return a;
	}
	
	protected String update(String id) {
		
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

	public String convertFHIR(){ // call for converting, change this to group	
		return "Hello";
	}



/* @see fhirconverter.OpenEMPIbase#create(org.json.JSONObject)
	@Override
	protected String create(JSONObject patient) {
       		* @see fhirconverter.OpenEMPIbase#search(java.util.HashMap)

	@Override
	protected JSONObject search(JSONObject parameters) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}	
	 */
}




