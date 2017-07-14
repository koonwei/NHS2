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


import fhirconverter.exceptions.*;

public class PatientFHIR extends OpenEMPIbase {
	Logger LOGGER = LogManager.getLogger(PatientFHIR.class);
	
	protected JSONObject read(String id) throws Exception {
		String result = this.commonReadPerson(id);	
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		ConversionFHIR_to_OpenEMPI converterFHIR = new ConversionFHIR_to_OpenEMPI();
		JSONObject convertedResults = converterFHIR.conversionToOpenEMPI(parameters);
		String result = this.commonSearchPersonByAttributes(convertedResults);
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected String update(String id) throws Exception {	
		return "";
	}
	
	protected String patch(String id, JsonPatch patient) throws Exception { //more testing needed! only gender done. by koon
		String result = this.commonReadPerson(id);
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		JSONObject xmlResults = converter.conversion(result);
		String jsonResults = xmlResults.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNodeResults = mapper.readTree(jsonResults);
		System.out.println(jsonNodeResults.toString());
		final JsonNode patched = patient.apply(jsonNodeResults);
		JSONObject patchedResults = new JSONObject(patched.toString());
		ConversionFHIR_to_OpenEMPI converter2 = new ConversionFHIR_to_OpenEMPI();
		JSONObject convertedXML = converter2.conversionToOpenEMPI(patchedResults);
		final String xmlPatch = XML.toString(convertedXML);

		return "";
	}
			
	protected String create(JSONObject patient) throws ResourceNotFoundException, Exception{
		ConversionFHIR_to_OpenEMPI converter = new ConversionFHIR_to_OpenEMPI();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		JSONObject records = new JSONObject();
		records.put("person", newRecordOpenEMPI);
		String xmlNewRecord = XML.toString(records);
		String result = this.commonAddPerson(xmlNewRecord);
		ConversionOpenEMPI_to_FHIR converterOpenEmpi = new ConversionOpenEMPI_to_FHIR();
		JSONObject createdObject = converterOpenEmpi.conversion(result);
		String replyCreatedNewRecord = "";
		if(createdObject.has("id")){
			replyCreatedNewRecord = createdObject.getString("id");	
		}
		return replyCreatedNewRecord; // Ask yuan if he wants all the fields or just certain. 
	}
	
	protected String delete(String id) throws Exception {
		String result = this.commonDeletePersonById(id);
		return result;
		//return "";
	}
	
}
