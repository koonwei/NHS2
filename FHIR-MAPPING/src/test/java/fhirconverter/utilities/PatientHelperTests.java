package fhirconverter.utilities;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import fhirconverter.converter.OpenEMPIbase;
import fhirconverter.converter.PatientFHIR;
import fhirconverter.exceptions.IdNotObtainedException;
import fhirconverter.exceptions.ResourceNotFoundException;

public class PatientHelperTests {

	/*NHS exist*/
	@Test
	public void testNHSexist() throws ResourceNotFoundException, Exception {
		PatientFHIR tester = new PatientFHIR();	
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourcePatientNHS.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());		
		String newRecordID = tester.create(patient);
		
		PatientHelper helper = new PatientHelper();
		String nhsIdentifier = helper.retrieveNHSbyId(newRecordID);		
		
		OpenEMPIbase remove = new OpenEMPIbase();
		remove.commonRemovePersonById(newRecordID);
		
		assertTrue("NHS is correct: ", (nhsIdentifier.equals("65498798126459873232897")));		
	}
	
	
	
	
	/*Patient exists in the correct format but without NHS - throw exception*/
	@Test(expected = IdNotObtainedException.class)
	public void testNHSnotExist() throws ResourceNotFoundException, Exception {
		PatientFHIR tester = new PatientFHIR();	
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());		
		String newRecordID = tester.create(patient);		
		PatientHelper helper = new PatientHelper();		
		try{
			String nhsIdentifier = helper.retrieveNHSbyId(newRecordID);		
		}finally{
			OpenEMPIbase remove = new OpenEMPIbase();
			remove.commonRemovePersonById(newRecordID);
		}		

	}	
	
	/*Patient doesn't exist - exception*/
	@Test(expected = ResourceNotFoundException.class)
	public void testPatientNotExist() throws Exception {
		PatientFHIR tester = new PatientFHIR();	
		PatientHelper helper = new PatientHelper();		
		String nhsIdentifier = helper.retrieveNHSbyId("1200");		
	}
	
	
	
	
}
