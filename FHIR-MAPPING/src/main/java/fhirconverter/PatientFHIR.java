package fhirconverter;
import ca.uhn.fhir.context.FhirContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.PositiveIntType;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.DateTimeType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.github.fge.jackson.JacksonUtils; 
import com.github.fge.jsonpatch.JsonPatch;  
import com.github.fge.jsonpatch.JsonPatchException; 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientFHIR extends OpenEMPIbase {
	Logger LOGGER = LogManager.getLogger(PatientFHIR.class);
	
	protected JSONObject read(String id) throws Exception {
		String result = this.commonReadPerson(id);	
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		String result = this.commonSearchPersonByAttributes(parameters);
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected String update(String id) {	
		return "";
	}
	
	protected String patch(String id, JsonPatch patient) throws Exception {
		String result = this.commonReadPerson(id);
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		JSONObject xmlResults = converter.conversion(result);
		String jsonResults = xmlResults.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNodeResults = mapper.readTree(jsonResults);
		JsonNode patched = patient.apply(jsonNodeResults);
		JSONObject patchedResults = new JSONObject(patched.toString());

		ConversionFHIR_to_OpenEMPI converter2 = new ConversionFHIR_to_OpenEMPI();
		JSONObject convertedXML = converter2.conversionToOpenEMPI(patchedResults);
		final String xmlPatch = XML.toString(convertedXML);		
		return "";
	}
			
	protected String create(JSONObject patient) throws Exception {
		ConversionFHIR_to_OpenEMPI converter = new ConversionFHIR_to_OpenEMPI();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		String xmlNewRecord = XML.toString(newRecordOpenEMPI);
		System.out.println(newRecordOpenEMPI.toString());
		System.out.println(xmlNewRecord);
		String result = this.commonAddPerson(xmlNewRecord);
		return "";
	}
	
	protected String delete(String id) throws Exception {
		String result = this.commonDeletePersonById(id);
		return result;
		//return "";
	}
	
	

		public String convertFHIR(){ // call for converting	
		FhirContext ctx = FhirContext.forDstu3();
        	Patient patient = new Patient();
        // you can use the Fluent API to chain calls
        // see http://hapifhir.io/doc_fhirobjects.html
        	patient.addName().setUse(HumanName.NameUse.OFFICIAL).addPrefix("Mr").addGiven("Sam"); //Removed addFamily. Causes error.
        	patient.addIdentifier().setSystem("http://ns.electronichealth.net.au/id/hi/ihi/1.0").setValue("8003608166690503");
        	System.out.println("Serialise Resource to the console to JSON.");
        // create a new XML parser and serialize our Patient object with it
        	String encoded = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient); 
		return encoded;
	}	

}
