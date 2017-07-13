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
		return conversion(result);
	}
	
	protected JSONObject search(JSONObject parameters) throws Exception {
		String result = this.commonSearchPersonByAttributes(parameters);
		return conversion(result);
	}
	
	protected String update(String id) {	
		return "";
	}
	
	protected String patch(String id, JsonPatch patient) throws Exception {
		String result = this.commonReadPerson(id);
		JSONObject xmlResults = conversion(result);
		String jsonResults = xmlResults.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNodeResults = mapper.readTree(jsonResults);
		JsonNode patched = patient.apply(jsonNodeResults);
		JSONObject patchedResults = new JSONObject(patched.toString());
		JSONObject convertedXML = conversionToOpenEMPI(patchedResults);
		final String xmlPatch = XML.toString(convertedXML);		
		return "";
	}
			
	protected String create(JSONObject patient) {
		JSONObject newRecordOpenEMPI = conversionToOpenEMPI(patient);
		String xmlNewRecord = XML.toString(newRecordOpenEMPI);
		System.out.println(newRecordOpenEMPI.toString());
		System.out.println(xmlNewRecord);
		return "";
	}
	
	protected String delete(String id) throws Exception {
		String result = this.commonDeletePersonById(id);
		return result;
		//return "";
	}
	

		/* Conversion from FHIR to OpenEMPI */	
	protected JSONObject conversionToOpenEMPI(JSONObject patient) {
		JSONObject content = new JSONObject();
		
		/* SET THE FULL NAME FROM FHIR TO OPENEMPI */
		
		if(patient.has("name")) {
			
			/* Name field is an array in the receiving JSONObject by the frontEnd */
			JSONArray array = patient.optJSONArray("name");
			
			/* If array has content */
			if(array!=null) {
				
				/* OpenEMPI takes only one name
				 * We have to find the name in the array list whose "use" value 
				 * is "official". Otherwise, it will take the first one in the array.   
				 */
				boolean officialFound = false; // it found official name
			
				
				for(int i=0; i<array.length(); i++) {
					JSONObject details = array.getJSONObject(i);
					
					/* Define maiden name if it exists */
					if((details.has("use"))&&details.optString("use").equals("maiden")) {
						content.put("mothersMaidenName", details.getJSONObject("use").getString("family"));
					}
					
					
					//is it the official name?
					if((details.has("use"))&&(details.optString("use").equals("official"))) {
						officialFound = true;
						content = createName(content,details);
						break;
					}
					
				
				}
				
				//didn't find official - gets the first one
				if(!officialFound) {
					JSONObject details = array.getJSONObject(0);
					content = createName(content,details);
				}
			}			
		}
		
		/* SET THE ADDRESS FROM FHIR TO OPENEMPI */
		
		if(patient.has("address")) {
			
			/* There may be multiple addresses for
			 * each patient in FHIR, therefore we get an array.
			 * OpenEMPI takes only one address so we will feed to it only
			 * the first one.
			 */
			JSONArray addresses = patient.getJSONArray("address");
			if(addresses.length()>0) {
				JSONObject address = addresses.getJSONObject(0);
				content = createAddress(content, address);			
			}									
		}
		
		/* SET THE MARITAL STATUS FROM FHIR TO OPENEMPI */
		
		if(patient.has("maritalStatus")) {
			JSONObject status = patient.getJSONObject("maritalStatus");
			if(status.has("text")) {
				content.put("maritalStatusCode", status.getString("text"));
			}
		}

		/* SET **PHONE NUMBER** AND **EMAIL** FROM FHIR TO OPENEMPI */
		
		if(patient.has("telecom")) {
			
			/* Telecom is an array of all the contact details of the patient
			 * We need phone and email for openEMPI
			 *  */
			JSONArray telecom = patient.getJSONArray("telecom");
			for(int i=0; i<telecom.length(); i++) {
				JSONObject system = telecom.getJSONObject(i);
				
				/* If this element in the array represents phone*/
				if((system.has("system"))&&(system.getString("system").equals("phone"))) {
					content.put("phoneNumber", system.getString("value"));
				}
	
				/* If this element in the array represents email*/
				if((system.has("system"))&&(system.getString("system").equals("email"))) {
					content.put("email", system.getString("value"));
				}
			}
			
			
		}
		
		/* SET **GENDER** FROM FHIR TO OPENEMPI */
		
		JSONObject genderDetails = new JSONObject();
		if(patient.has("gender")){
			if(patient.getString("gender").equals("female")){
				genderDetails.put("genderCd","1");
				genderDetails.put("genderCode","F");
				genderDetails.put("genderDescription","Female");
				genderDetails.put("genderName","Female");
			}
			else if(patient.getString("gender").equals("male")){
				genderDetails.put("genderCd","2");
				genderDetails.put("genderCode","M");
				genderDetails.put("genderDescription","Male");
				genderDetails.put("genderName","Male");
			}
			else if(patient.getString("gender").equals("other")){
				genderDetails.put("genderCd","3");
				genderDetails.put("genderCode","O");
				genderDetails.put("genderDescription","Other");
				genderDetails.put("genderName","Other");
			}
			else if(patient.getString("gender").equals("unknown")){
				genderDetails.put("genderCd","4");
				genderDetails.put("genderCode","U");
				genderDetails.put("genderDescription","Unknown");
				genderDetails.put("genderName","Unknown");
			}

		}
		content.put("gender", genderDetails);	
		
		
		/* SET BIRTH ORDER FROM FHIR TO OPENEMPI */

		
		if(patient.has("multipleBirthInteger")) {
			content.put("birthOrder", patient.optString("multipleBirthInteger"));
		}
		
		/* SET DEATH TIME FROM FHIR TO OPENEMPI */
		
		if(patient.has("deceasedDateTime")) {
			content.put("deathTime", patient.optString("deceasedDateTime"));
		}
		
		/* SET DATE OF BIRTH FROM FHIR TO OPENEMPI */ 
		
		if(patient.has("birthDate")) {
			content.put("dateOfBirth", patient.optString("birthDate"));
		}
		
		/* SET DATE OF CHANGE FROM FHIR TO OPENEMPI */
		
		if(patient.has("meta")) {
			if(patient.getJSONObject("meta").has("lastUpdated"))
			content.put("dateChanged", patient.getJSONObject("meta").optString("lastUpdated"));
		}
		
		/* SET IDENTIFIER FROM FHIR TO OPENEMPI */ //need fixing
		
		if(patient.has("identifier")) {
			JSONArray identifierArray = new JSONArray();
			JSONObject personIdentifier = new JSONObject();

			
			JSONArray receivedIdentifiers = patient.getJSONArray("identifier");
			
			if(receivedIdentifiers.length()>0) {
				JSONObject systemID = receivedIdentifiers.getJSONObject(0);
				personIdentifier.put("identifier", systemID.optString("value"));
				JSONObject identifierDomain = new JSONObject();
				identifierDomain.put("identifierDomainName", systemID.optString("system"));	
				personIdentifier.put("identifierDomain", identifierDomain);				
				
			}	
			content.put("personIdentifiers", personIdentifier);
		}
		return content;
	}
	/* ***** FHIR TO OPEN-EMPI ***** */

	protected JSONObject createName(JSONObject content,JSONObject details) {		
		
		/* - Family Name - */
		if(details.has("family")) {
			content.put("familyName", new String(details.optString("family")));
		}
		
		/* Given Name  
		 * It is a JSONArray because it may contains First Name and
		 * Middle Names
		 * */
		if(details.has("given")) {
			JSONArray given = details.getJSONArray("given");
			
			/* Check that there is at least one given Name (first name) */
			if(given.length()>0)
				content.put("givenName", given.getString(0));
			
			/* If there are more than 1 given names, the next one is the middle name */
			if(given.length()>1) 
				content.put("middleName", given.getString(1));
		}
		
		/* Prefix
		 * It is a JSONArray - but OpenEMPI accepts only one
		 * We get the first one
		 */
		if(details.has("prefix")) {
			JSONArray prefix = details.getJSONArray("prefix");
			if(prefix.length()>1)
				content.put("prefix", prefix.getString(0));

		}
		
		/* Suffix
		 * It is a JSONArray - but OpenEMPI accepts only one
		 * We get the first one
		 */
		if(details.has("suffix")) {
			JSONArray suffix = details.getJSONArray("suffix");
			if(suffix.length()>1)
				content.put("suffix", suffix.getString(0));
		}
		
		return content;
	}
	
	protected JSONObject createAddress(JSONObject content,JSONObject address) {
		
		/* line[0]=Address 1 & Line[1] = Address 2 (if they exist) */
		if(address.has("line")) {
			JSONArray lines = address.getJSONArray("line");
			
			if(lines.length()>0)
				content.put("address1", lines.getString(0));
			if(lines.length()>1)
				content.put("address2", lines.getString(1));
					
		}
		
		/* City */
		if(address.has("city")) {
			content.put("city", address.getString("city"));
		}
		
		/* Country */ 
		if(address.has("country")) {
			content.put("country", address.getString("country"));
		}
		
		/* State */
		if(address.has("state")) {
			content.put("state", address.getString("state"));
		}
		
		if(address.has("postalCode")) {
			content.put("postalCode", address.get("postalCode"));
		}
		return content;
	}


			/* Convert OPENEMPI TO FHIR */
	protected JSONObject conversion(String result){
        	JSONObject xmlJSONObj = XML.toJSONObject(result); // converts to jsonobject hashmap
        	int PRETTY_PRINT_INDENT_FACTOR = 4;
        	String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);// converts to human readable, add this if needed to system print to test
        	System.out.println(jsonPrettyPrintString);
        	ArrayList<JSONObject> searchResults = new ArrayList<JSONObject>(); 
		FhirContext ctx = FhirContext.forDstu3();
		/*Automation doesn't work*/
		//patient = gson.fromJson(xmlJSONObj.getJSONObject("people").getJSONArray("person").getJSONObject(0).toString(), Patient.class); //does not work
		//System.out.println("patient name: " + patient.getName());
		if(xmlJSONObj.has("people")){
			if (xmlJSONObj.optJSONObject("people")!=null) {
				JSONArray persons = xmlJSONObj.optJSONObject("people").optJSONArray("person");
				if(persons==null) {
					JSONObject person = xmlJSONObj.getJSONObject("people").getJSONObject("person");
					JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
					searchResults.add(fhirPersonStructure);
				}
				else {
					for (int i=0; i<persons.length(); i++) {
						JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString((personMapping(persons.getJSONObject(i)))));
						searchResults.add(fhirPersonStructure);
					}
				}		
			}
		}
		if(xmlJSONObj.has("person")){
			JSONObject person = xmlJSONObj.getJSONObject("person");
			JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
			searchResults.add(fhirPersonStructure);	
		}	
		JSONObject people = new JSONObject();
		people.put("resource", searchResults); 
		for(int i = 0; i<searchResults.size(); i++)
		{
			System.out.println(searchResults.get(i));
		} 
		JSONObject replyJSON = new JSONObject();
		replyJSON.put("entry", people);
		return replyJSON;
 	}	
	
	private String checkText(Address t) {
		if(t.getText()==null) {
			return "";
		}
		else {
			return t.getText();
		}
	}
	
	private String checkPhone(ContactPoint p, boolean exists) {
		if (exists)
			return p.getValue();
		else
			return "";
	}
	protected HumanName setHumanName(JSONObject node){
	/* ----- SET THE FULL NAME OF THE PATIENT ----- */
		HumanName n = new HumanName();
		/*Given Name*/
		if(node.has("givenName")) {
			n.addGiven(node.optString("givenName"));
		}				
		
		/*Family Name*/
		if(node.has("familyName")) {
			n.setFamily(node.optString("familyName"));
			n.setUse(HumanName.NameUse.OFFICIAL);			
		}	
		
		/*Middle Name*/
		if(node.has("middleName")) {
			n.addGiven(node.optString("middleName"));
		}
		
		/*Prefix*/
		if(node.has("prefix")) {
			n.addPrefix(node.optString("prefix"));
		}
		
		/*Suffix*/
		if(node.has("suffix")) {
			n.addSuffix(node.optString("suffix"));
		}		
		return n;
	}
	protected Address setAddress(JSONObject node){
		Address t = new Address();
		
		/*Address1*/
		if(node.has("address1")) {
			t.addLine(node.optString("address1"));
			t.setText(node.optString("address1") + " ");
		}
		
		/*Address2*/
		if(node.has("address2")) {
			t.addLine(node.optString("address2"));
			t.setText(checkText(t) + node.optString("address2") + " ");
		}
		
		/*City*/
		if(node.has("city")) {
			t.setCity(node.optString("city"));			
			t.setText(checkText(t) + node.optString("city")+ " ");

		}
		
		/*State*/
		if(node.has("state")) {
			t.setState(node.optString("state"));
			t.setText(checkText(t) + node.optString("state") + " ");			
		}
		
		/*Postal Code*/
		if(node.has("postalCode")) {
			t.setPostalCode(node.optString("postalCode"));
			t.setText(checkText(t) + node.optString("postalCode")+ " ");			

		}
		
		/*Country*/
		if(node.has("country")) {
			t.setCountry(node.optString("country"));
			t.setText(checkText(t) + node.optString("country"));			

		}
		return t;

	}
	protected Patient personMapping(JSONObject node)  { //tried automated using generic objects methods, but inner classes methods differ too much. Unable to get it more automated. By koon.
		Patient p = new Patient();
		p.addName(setHumanName(node));
		/* Maiden Name */
		if(node.has("mothersMaidenName"))
		{
			HumanName maidenName = new HumanName();
			maidenName.setFamily(node.optString("mothersMaidenName"));
		        maidenName.setUse(HumanName.NameUse.MAIDEN);
			p.addName(maidenName);
		}
		System.out.println("Current patient's name: " + p.getName().get(0).getNameAsSingleString());
	        if(node.has("personId")){
			IdType id = new IdType(node.optString("personId"));
			p.setId(id);
		}	
		/* ----- SET DATE CHANGED ---- */
		if(node.has("dateChanged")) {
			Meta v = new Meta();
			v.setLastUpdated(convertStringtoDate(node.getString("dateChanged")));
			p.setMeta(v);
		}
		/* ----- SET MARITAL STATUS ----- */
		
		if(node.has("maritalStatusCode")) {
			CodeableConcept value = new CodeableConcept();
			value.setText(node.getString("maritalStatusCode"));			
			p.setMaritalStatus(value);
		}
		if(node.has("birthOrder")) {
			PositiveIntType birthOrder = new PositiveIntType(node.optInt("birthOrder"));			
			p.setMultipleBirth(birthOrder);
		}
			
		/* ----- SET THE ADDRESS OF THE PATIENT ----- */
		
		if(setAddress(node) !=null)
		p.addAddress(setAddress(node));
		System.out.println("Patient's address: " + p.getAddress().get(0).getText());
		/* ----- SET THE CONTACT DETAILS OF THE PATIENT ----- */
		
		ArrayList<ContactPoint> telecom = new ArrayList<ContactPoint>();

		/*email*/
		if(node.has("email")) {
			ContactPoint email = new ContactPoint();
			email.setSystem(ContactPointSystem.EMAIL).setValue(node.getString("email"));
			telecom.add(email);
		}
		
		/*Phone*/
		ContactPoint phone = new ContactPoint();
		phone.setSystem(ContactPointSystem.PHONE);
		boolean exists = false;
		
		//phone country code
		if(node.has("phoneCountryCode")) {
			exists = true;
			phone.setValue(node.optString("phoneCountryCode"));
		}
		
		//phone area code
		if(node.has("phoneAreaCode")) {
			phone.setValue(checkPhone(phone, exists) + node.optString("phoneAreaCode"));
			exists = true;
		}
		
		//phone number
		if(node.has("phoneNumber")) {
			phone.setValue(checkPhone(phone, exists) + node.optString("phoneNumber"));
			exists = true;
		}
		
		//phone extension
		if(node.has("phoneExt")) {
			phone.setValue(checkPhone(phone, exists) + node.optString("phoneExt"));
			exists = true;
		}
		
		if(exists)
			telecom.add(phone);
		
		if(telecom.size()>0) {
			p.setTelecom(telecom);
			for(int z=0; z<telecom.size(); z++)
				System.out.println("Contact: " + p.getTelecom().get(z).getValue());
		}
		
		
		
		/* ----- SET THE BIRTH DETAILS OF THE PATIENT ----- */
		
		
		if(node.has("dateOfBirth")){
			p.setBirthDate(this.convertStringtoDate(node.optString("dateOfBirth")));
		}
		
		
		if(node.has("gender")){
			JSONObject genders = node.getJSONObject("gender");
			if(genders.has("genderDescription")) {
				String gender = genders.optString("genderDescription");
				gender = gender.toUpperCase();
				p.setGender(AdministrativeGender.valueOf(gender));
			}
		}
		// SET PatientIdentifier 
		if(node.has("personIdentifiers")){
			
			JSONArray personIdentifiers = node.optJSONArray("personIdentifiers");
			
			if(personIdentifiers==null) {
				Identifier identifier = new Identifier();
				System.out.println("person identifiers not an array");
				JSONObject id = node.getJSONObject("personIdentifiers");
			    
			       if(id.has("identifier")) {
			       		identifier.setValue(id.optString("identifier"));	
			       }
			       if(id.has("identifierDomain")) {
						JSONObject domain = id.getJSONObject("identifierDomain");
						
						if(domain.has("namespaceIdentifier")) {
							identifier.setSystem(domain.optString("namespaceIdentifier"));
						}
						
			       }
			       p.addIdentifier(identifier);
			}
			else {
				JSONArray ids = node.getJSONArray("personIdentifiers");
				for(int i=0; i<ids.length(); i++) {
					Identifier identifier = new Identifier();					
					JSONObject id = ids.getJSONObject(i);
				    
				       if(id.has("identifier")) {
				       		identifier.setValue(id.optString( "identifier"));	
				       }
				       
				       if(id.has("identifierDomain")) {
							JSONObject domain = id.getJSONObject("identifierDomain");
							if((domain.has("identifierDomainName"))&&(!domain.optString("identifierDomainName").equals("OpenEMPI"))) {
								identifier.setSystem(domain.optString("identifierDomainName"));							
							}
							else
								continue;
				       }
				       p.addIdentifier(identifier);

				}	
			}
		}
		// SET PATIENT DECEASED 
		if(node.has("deathTime")){
			DateTimeType deceasedDate = new DateTimeType(node.optString("deathTime"));
			p.setDeceased(deceasedDate);		
		}	
		return p;
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
