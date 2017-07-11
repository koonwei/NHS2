package fhirconverter;
import static org.junit.Assert.*;
import org.junit.*; 
import org.json.JSONException; 
import org.json.JSONObject;

public class JavaPatientTestCases{
	@Test
	public void testPatientWorkFlow(){
		PatientFHIR tester = new PatientFHIR();
		String data = tester.convertFHIR();
		JSONObject jsonObj = new JSONObject(data);
		String expected = "{resourceType:\"Patient\",identifier:[{system:\"http://ns.electronichealth.net.au/id/hi/ihi/1.0\",value: 8003608166690503}], name:[{use: \"official\", given:[\"Sam\"], prefix:[ \"Mr\"]}]}";
		Assert.assertTrue(jsonObj.has("resourceType"));		
	}
}
