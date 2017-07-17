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

public class PatientFHIR {
	Logger LOGGER = LogManager.getLogger(PatientFHIR.class);
	OpenEMPIbase caller = new OpenEMPIbase();

	protected JSONObject read(String id) throws Exception {
		String result = caller.commonReadPerson(id);	
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		ConversionFHIR_to_OpenEMPI converterFHIR = new ConversionFHIR_to_OpenEMPI();
		JSONObject convertedResults = converterFHIR.conversionToOpenEMPI(parameters);
		String result = caller.commonSearchPersonByAttributes(convertedResults);
		ConversionOpenEMPI_to_FHIR converter = new ConversionOpenEMPI_to_FHIR();
		return converter.conversion(result);
	}
	
	protected String update(String id, JSONObject patient) throws Exception {	
		ConversionFHIR_to_OpenEMPI converter = new ConversionFHIR_to_OpenEMPI();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		newRecordOpenEMPI.put("personId", id);
		if(newRecordOpenEMPI.has("personIdentifiers")) {
			newRecordOpenEMPI.remove("personIdentifiers");
		}
		
		JSONObject records = new JSONObject();
		
		
		String readResult = caller.commonReadPerson(id);
		JSONObject xmlRead = XML.toJSONObject(readResult);
		JSONArray personIdentifiers = new JSONArray();
		if(xmlRead.has("person")){
			JSONObject person = xmlRead.getJSONObject("person");
			if(person.has("personIdentifiers")) {
				personIdentifiers = person.getJSONArray("personIdentifiers");
				for(int j=0; j<personIdentifiers.length(); j++) {
					JSONObject identifierRecord = personIdentifiers.getJSONObject(j);
					if((identifierRecord.has("identifierDomain"))&&(identifierRecord.optString("identifierDomainName").equals("OpenEMPI"))) {
						
						JSONObject identifier = new JSONObject();
						identifier.put("identifier", identifierRecord.optString("identifier"));
						JSONObject identifierDomain = new JSONObject();
						identifierDomain.put("identifierDomainName", identifierRecord.getJSONObject("identifierDomain").optString("identifierDomainName"));	
						identifier.put("identifierDomain", identifierDomain);				
						personIdentifiers.put(identifier);
						
					}
				}
						
			}
		
		}
		newRecordOpenEMPI.put("personIdentifiers", personIdentifiers);
		records.put("person", newRecordOpenEMPI);

		/**
		 * For now we don't handle the update of personIdentifiers, so
		 * if they are included in the JSONObject we will ignore it
		 */
		
		
		
		
		
		String xmlNewRecord = XML.toString(records);
		System.out.println("FOCUS - THIS IS WHAT WE SENT TO OPENEMPIBASE: \n" + xmlNewRecord);
		String result = caller.commonUpdatePerson(xmlNewRecord);
		/*ConversionOpenEMPI_to_FHIR converterOpenEmpi = new ConversionOpenEMPI_to_FHIR();
		JSONObject createdObject = converterOpenEmpi.conversion(result);
		String replyCreatedNewRecord = "";
		if(createdObject.has("id")){
			replyCreatedNewRecord = createdObject.getString("id");	
		}*/
		return result; // Ask yuan if he wants all the fields or just certain. 

	}
	
	protected String patch(String id, JsonPatch patient) throws Exception { //more testing needed! only gender done. by koon
		String result = caller.commonReadPerson(id);
		ConversionOpenEMPI_to_FHIR converterOpenEmpi = new ConversionOpenEMPI_to_FHIR();
		JSONObject xmlResults = converterOpenEmpi.conversion(result);
		xmlResults.remove("identifier");
		String jsonResults = xmlResults.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNodeResults = mapper.readTree(jsonResults);
		JsonNode patched = null;
		try{
			patched = patient.apply(jsonNodeResults);
			System.out.println(patched.toString());
		}catch(Exception e){
			e.printStackTrace();
			throw new JsonPatchException("Resource does not contain the paths for remove or replace");
		}
		if(patched != null)
		{
			boolean fhirSchemeRequirements = Utils.validateScheme(patched, "resource/Patient.schema.json");
			if(fhirSchemeRequirements){
				JSONObject patchedResults = new JSONObject(patched.toString());
				ConversionFHIR_to_OpenEMPI converterFHIR = new ConversionFHIR_to_OpenEMPI();
		 		JSONObject convertedXML = converterFHIR.conversionToOpenEMPI(patchedResults);
				final JsonNode jsonNodePatched = mapper.readTree(convertedXML.toString());	
				if(Utils.validateScheme(jsonNodePatched, "resource/openempiSchema.json")){
					JSONObject convertedXMLvalidated = new JSONObject();
					JSONObject xmlJsonObj = XML.toJSONObject(result);
					JSONObject xmlPeopleObj = xmlJsonObj.getJSONObject("person");
					System.out.println(xmlPeopleObj.toString());
					JSONArray personIdentifiers = xmlPeopleObj.optJSONArray("personIdentifiers"); 
					if(personIdentifiers == null){
						JSONObject multiplePersons = xmlPeopleObj.getJSONObject("personIdentifiers");
						convertedXML.put("personIdentifiers", multiplePersons);
				       
					}else{
						JSONArray multiplePersons = xmlPeopleObj.getJSONArray("personIdentifiers");
						convertedXML.put("personIdentifiers", multiplePersons);
					}
									
					convertedXML.put("personId", id);
					convertedXMLvalidated.put("person", convertedXML);
					final String xmlPatch = XML.toString(convertedXMLvalidated);			
					return caller.commonUpdatePerson(xmlPatch);
				}else{
					throw new OpenEMPISchemeNotMetException("The Parameters does not confine to OpenEMPIScheme");
				}
			}else{
				throw new FhirSchemeNotMetException("The parameters does not confine to FHIR Standards for OpenEMPIScheme");
			}

		}
		return "";
	}
			
	protected String create(JSONObject patient) throws ResourceNotFoundException, Exception{
		ConversionFHIR_to_OpenEMPI converter = new ConversionFHIR_to_OpenEMPI();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		JSONObject records = new JSONObject();
		records.put("person", newRecordOpenEMPI);
		String xmlNewRecord = XML.toString(records);
		String result = caller.commonAddPerson(xmlNewRecord);
		ConversionOpenEMPI_to_FHIR converterOpenEmpi = new ConversionOpenEMPI_to_FHIR();
		JSONObject createdObject = converterOpenEmpi.conversion(result);
		String replyCreatedNewRecord = "";
		if(createdObject.has("id")){
			replyCreatedNewRecord = createdObject.getString("id");	
		}
		return replyCreatedNewRecord; // Ask yuan if he wants all the fields or just certain. 
	}
	
	protected String delete(String id) throws Exception {
		String result = caller.commonDeletePersonById(id);
		return result;
		//return "";
	}
	
}
