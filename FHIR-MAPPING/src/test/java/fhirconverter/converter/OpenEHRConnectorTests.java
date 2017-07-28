package fhirconverter.converter;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import fhirconverter.exceptions.IdNotObtainedException;
import org.json.JSONObject;
import org.json.JSONArray;

public class OpenEHRConnectorTests{
	@Test
	public void getEHRidNumberTest() throws Exception{
		OpenEHRConnector tester = new OpenEHRConnector("openEhrApi");		
		assertEquals("Get EHRid operation","c831fe4d-0ce9-4a63-8bfa-2c51007f97e5",tester.getEHRIdByNhsNumber("9999999332"));
	}
	@Test(expected = IdNotObtainedException.class)
	public void getEHRidNumberNotExistTest() throws Exception{	
		OpenEHRConnector tester = new OpenEHRConnector("openEhrApi");	
		tester.getEHRIdByNhsNumber("999999933");
	}
	@Test
	public void getObservationsTests() throws Exception{
		OpenEHRConnector tester = new OpenEHRConnector("openEhrApi");	
		JSONObject responseObj = tester.getGrowthChartObservations("9999999332","1");
		if(responseObj.has("resultSet"))
		{
			assertNotNull(responseObj.getJSONArray("resultSet"));
			assertEquals(responseObj.getString("patientId"),"1");
		}else{
			assertEquals("Test Case Failed! No Response","1","2");
		}
		
	}
}


