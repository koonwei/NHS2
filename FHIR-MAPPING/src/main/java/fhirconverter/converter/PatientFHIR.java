package fhirconverter.converter;
import ca.uhn.fhir.context.FhirContext;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	protected Patient read(String id) throws Exception {
		String result = caller.commonReadPerson(id);	
		ConversionOpenEmpiToFHIR converter = new ConversionOpenEmpiToFHIR();
		return converter.conversion(result).get(0);
	}
	
	protected List<Patient> search(JSONObject parameters) throws Exception {
		String result = "";
		if((parameters.has("identifier_value"))&&(parameters.has("identifier_domain"))) {
			result = caller.commonSearchPersonById(parameters);
		}else
			result = caller.commonSearchPersonByAttributes(parameters);

		LOGGER.info("Search Results: " + result);
		
		ConversionOpenEmpiToFHIR converter = new ConversionOpenEmpiToFHIR();
		List<Patient> patients = converter.conversion(result);
		LOGGER.info("Patients Converted: " + patients);
		return patients;
	}
	
	protected String update(String id, JSONObject patient) throws Exception {	
		ConversionFHIRToOpenEmpi converter = new ConversionFHIRToOpenEmpi();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		newRecordOpenEMPI.put("personId", id);
		
		JSONObject records = new JSONObject();

		String readResult = caller.commonReadPerson(id);
		if(!readResult.equals("")) {
			if(newRecordOpenEMPI.has("personIdentifiers")) {
				newRecordOpenEMPI.remove("personIdentifiers");
			}
			
			JSONObject xmlRead = XML.toJSONObject(readResult);
			JSONArray personIdentifiers = new JSONArray();
			if(xmlRead.has("person")){
				JSONObject person = xmlRead.getJSONObject("person");
                JSONObject personIdentifierObj = person.optJSONObject("personIdentifiers");
                JSONArray personIdentifierArray = person.optJSONArray("personIdentifiers");
				if(personIdentifierObj!=null)
                {
                    JSONObject identifier = createIdentifierOpenEMPI(person.optJSONObject("personIdentifiers"));
                    personIdentifiers.put(identifier);
                }
				else if(personIdentifierArray!=null) {
                    JSONObject identifier = null;
					for(Object identifierObj : personIdentifierArray) {
					    if ( identifierObj instanceof JSONObject) {
                            JSONObject identifierRecord = (JSONObject) identifierObj;
                            if ((identifierRecord.has("identifierDomain")) && (identifierRecord.getJSONObject("identifierDomain").optString("identifierDomainName").equals("OpenEMPI"))) {
                                identifier = createIdentifierOpenEMPI(identifierRecord);
                                personIdentifiers.put(identifier);
                            }
                        }
                    }
				}
				else
                {
                    LOGGER.error("Patient has no identifier: " + person);
                    throw new InternalError("Patient has no identifier");
                }
                newRecordOpenEMPI.put("personIdentifiers", personIdentifiers);
			}

		}
		records.put("person", newRecordOpenEMPI);

		LOGGER.info(records);

		/**
		 * For now we don't handle the update of personIdentifiers, so
		 * if they are included in the JSONObject we will ignore it
		 */
		
		
		
		
		String xmlNewRecord = XML.toString(records);
		System.out.println("***sos** SEND TO OPENEMPIBASE: \n" + xmlNewRecord );
		String result = caller.commonUpdatePerson(xmlNewRecord);
		/*ConversionOpenEMPI_to_FHIR converterOpenEmpi = new ConversionOpenEMPI_to_FHIR();
		JSONObject createdObject = converterOpenEmpi.conversion(result);
		String replyCreatedNewRecord = "";
		if(createdObject.has("id")){
			replyCreatedNewRecord = createdObject.getString("id");	
		}*/
		return result; // Ask yuan if he wants all the fields or just certain. 

	}

    private JSONObject createIdentifierOpenEMPI(JSONObject identifierRecord) {
        JSONObject identifier = new JSONObject();
        identifier.put("identifier", identifierRecord.optString("identifier"));
        JSONObject identifierDomain = new JSONObject();
        identifierDomain.put("identifierDomainName", identifierRecord.getJSONObject("identifierDomain").optString("identifierDomainName"));
        identifierDomain.put("identifierDomainId", identifierRecord.getJSONObject("identifierDomain").optString("identifierDomainId"));
        identifier.put("identifierDomain", identifierDomain);
        return identifier;
    }

    protected String patch(String id, JsonPatch patient) throws Exception { //more testing needed! only gender done. by koon
		String result = caller.commonReadPerson(id);
		ConversionOpenEmpiToFHIR converterOpenEmpi = new ConversionOpenEmpiToFHIR();
//		JSONObject xmlResults = converterOpenEmpi.conversion(result);
		Patient patientObj = converterOpenEmpi.conversion(result).get(0);
		JSONObject xmlResults = new JSONObject(FhirContext.forDstu2().newJsonParser().encodeResourceToString(patientObj));

		xmlResults.remove("identifier");
		String jsonResults = xmlResults.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNodeResults = mapper.readTree(jsonResults);
		JsonNode patched = null;
		try{
			patched = patient.apply(jsonNodeResults);
		}catch(Exception e){
			e.printStackTrace();
			throw new JsonPatchException("Resource does not contain the paths for remove or replace");
		}
		if(patched != null)
		{
			System.out.println(patched.toString());
			boolean fhirSchemeRequirements = Utils.validateScheme(patched, "resource/Patient.schema.json");
			if(fhirSchemeRequirements){
				JSONObject patchedResults = new JSONObject(patched.toString());
				ConversionFHIRToOpenEmpi converterFHIR = new ConversionFHIRToOpenEmpi();
		 		JSONObject convertedXML = converterFHIR.conversionToOpenEMPI(patchedResults);
				final JsonNode jsonNodePatched = mapper.readTree(convertedXML.toString());	
				if(Utils.validateScheme(jsonNodePatched, "resource/openempiSchema.json")){
					JSONObject convertedXMLvalidated = new JSONObject();
					JSONObject xmlJsonObj = XML.toJSONObject(result);
					JSONObject xmlPeopleObj = xmlJsonObj.getJSONObject("person");
					System.out.println(xmlPeopleObj.toString());
					JSONArray personIdentifiers = xmlPeopleObj.optJSONArray("personIdentifiers"); 
					if(personIdentifiers == null){
						JSONObject personIdentifier = xmlPeopleObj.getJSONObject("personIdentifiers");
						convertedXML.put("personIdentifiers", personIdentifier);
				       
					}else{
						JSONArray multipleIdentifiers = xmlPeopleObj.getJSONArray("personIdentifiers");
						convertedXML.put("personIdentifiers", multipleIdentifiers);
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
		else 
		{
			throw new Exception("Patch operators are empty!");
		}
    }
			
	public String create(JSONObject patient) throws ResourceNotFoundException, Exception{
		ConversionFHIRToOpenEmpi converter = new ConversionFHIRToOpenEmpi();
		JSONObject newRecordOpenEMPI = converter.conversionToOpenEMPI(patient);
		JSONObject records = new JSONObject();
		records.put("person", newRecordOpenEMPI);
		String xmlNewRecord = XML.toString(records);
		String result = caller.commonAddPerson(xmlNewRecord);
		ConversionOpenEmpiToFHIR converterOpenEmpi = new ConversionOpenEmpiToFHIR();
//		JSONObject createdObject = converterOpenEmpi.conversion(result);
		Patient createdPatient = converterOpenEmpi.conversion(result).get(0);
		String replyCreatedNewRecord = "";
//		if(createdObject.has("id")){
//			replyCreatedNewRecord = createdObject.getString("id");
//		}
		if(createdPatient.getId() != null)
		{
			replyCreatedNewRecord = createdPatient.getId().getIdPart();
		}
		return replyCreatedNewRecord; // Ask yuan if he wants all the fields or just certain. 
	}
	
	protected String delete(String id) throws Exception {
		String result = caller.commonDeletePersonById(id);
		return result;
		//return "";
	}
	
}
