package fhirconverter;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.*; 
import org.json.JSONException; 
import org.json.JSONObject;
import org.json.JSONArray;
public class PatientTestCases{
	/*
	@Test
	public void testPatientWorkFlow(){
		PatientFHIR tester = new PatientFHIR();
		String data = tester.convertFHIR();
		JSONObject jsonObj = new JSONObject(data);
		String expected = "{resourceType:\"Patient\",identifier:[{system:\"http://ns.electronichealth.net.au/id/hi/ihi/1.0\",value: 8003608166690503}], name:[{use: \"official\", given:[\"Sam\"], prefix:[ \"Mr\"]}]}";
		Assert.assertTrue(jsonObj.has("resourceType"));		
	}
	*/
	@Test
	public void testPatientSearch() throws Exception {
		PatientFHIR tester = new PatientFHIR();
		JSONObject patientSearch = new JSONObject();
		patientSearch.put("familyName", new String("ppp"));
      		patientSearch.put("givenName", new String("eee"));
	        JSONObject reply = new JSONObject();
  	       	reply = tester.search(patientSearch);
		Assert.assertTrue(reply.has("entry"));
		JSONObject resource = reply.optJSONObject("entry");
		Assert.assertTrue(resource.has("resource"));
		JSONArray patient = resource.optJSONArray("resource");	
		Assert.assertFalse(patient.equals(null));
		JSONObject patientDetails = patient.getJSONObject(0);
		Assert.assertTrue(patientDetails.has("resourceType"));
		String resourceType = patientDetails.optString("resourceType");
		Assert.assertEquals(resourceType, "Patient");
	}
	@Test
	public void testPatientRead() {
		PatientFHIR tester = new PatientFHIR();
		JSONObject reply = new JSONObject();
		String testerString = "1";
  	        try {
 	       		reply = tester.read(testerString);
			Assert.assertTrue(reply.has("entry"));
			JSONObject resource = reply.optJSONObject("entry");
			Assert.assertTrue(resource.has("resource"));
			JSONArray patient = resource.optJSONArray("resource");	
			Assert.assertFalse(patient.equals(null));
			JSONObject patientDetails = patient.getJSONObject(1);
			Assert.assertTrue(patientDetails.has("resourceType"));
			String resourceType = patientDetails.optString("resourceType");
			Assert.assertEquals(resourceType, "Patient");	
		} catch (Exception e) {
    			e.printStackTrace();
		}
			
	}
	@Test
	public void testPatientUpdate() {
		
	}
	@Test
	public void testPatientPatch() {
		
	}
	@Test
	public void testPatientCreate() {
		
	}
	@Test
	public void testPatientDelete() {
	}
}
