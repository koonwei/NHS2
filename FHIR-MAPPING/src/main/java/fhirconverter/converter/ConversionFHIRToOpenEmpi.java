/**
 * ConversionFHIR_to_OpenEMPI
 * 
 * v2.0
 * 
 * Date: 13-7-2017
 * 
 * Copyrights: Koon Wei Teo, Evanthia Tingiri, Shruti Shina
 * 
 * Description: This class contains the necessary functions to convert FHIR to OpenEMPI.
 * 				It is called by PatientFHIR in the case of the following requests:
 * 				Create, Update and Patch
 * 
 */

package fhirconverter.converter;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConversionFHIRToOpenEmpi {
	
	
	protected JSONObject conversionToOpenEMPI(JSONObject patient) {
		JSONObject content = new JSONObject();
		
		/* SET THE FULL NAME FROM FHIR TO OPENEMPI */	
		content = setFullNames(patient, content);
		/*if(patient.has("name")) {
			
			JSONArray Namesarray = patient.optJSONArray("name");
			
			if(Namesarray!=null) {
				
				boolean officialFound = false; // it found official name
			
				for(int i=0; i<Namesarray.length(); i++) {
					JSONObject details = Namesarray.getJSONObject(i);
					
					if((details.has("use"))&&details.optString("use").equals("maiden")) {
						if(details.getJSONArray("family").length() > 0){
							content.put("mothersMaidenName", details.getJSONArray("family").getString(0));
						}
					}
					
					if((details.has("use"))&&(details.optString("use").equals("official"))) {
						officialFound = true;
						content = createName(content,details);
						continue;
					}				
				}
				
				if(!officialFound) {
					JSONObject details = Namesarray.getJSONObject(0);
					content = createName(content,details);
				}
			}			
		}*/
		
		/* SET THE ADDRESS FROM FHIR TO OPENEMPI */	
		content = setAddresses(patient, content);
		
		
		/* SET THE MARITAL STATUS FROM FHIR TO OPENEMPI */
		content = setMaritalStatus(patient, content);
		/*if(patient.has("maritalStatus")) {
			JSONObject status = patient.getJSONObject("maritalStatus");
			if(status.has("coding")) {
				JSONObject code = status.getJSONArray("coding").getJSONObject(0);
				System.out.println(code.toString());
				if(code.has("code")){
					String codeName = code.optString("code");
					if(codeName.equals("M")){
						codeName = "MARRIED";
					}else if(codeName.equals("D")){
						codeName = "DIVORCED";
					}else if(codeName.equals("I")){
						codeName = "INTERLOCUTORY";
					}else if(codeName.equals("L")){
						codeName = "LEGALLY SEPARATED";
					}else if(codeName.equals("P")){
						codeName = "POLYGAMOUS";
					}else if(codeName.equals("S")){
						codeName = "NEVER MARRIED";
					}else if(codeName.equals("T")){
						codeName = "DOMESTIC PARTNER";
					}else if(codeName.equals("W")){
						codeName = "WIDOWED";
					}else if(codeName.equals("A")){
						codeName = "ANNULLED";
					}else{
						codeName = "UNKNOWN";
					}
					System.out.println(codeName + "HI THERE");
					content.put("maritalStatusCode", codeName);
				}
			}
		}*/

		/* SET PHONE NUMBER AND EMAIL FROM FHIR TO OPENEMPI */		
		content = setTelecom(patient, content);
		/*if(patient.has("telecom")) {
			
			JSONArray telecom = patient.getJSONArray("telecom");
			for(int i=0; i<telecom.length(); i++) {
				JSONObject system = telecom.getJSONObject(i);
				
				content = checkExistsAndPut(((system.has("system"))&&(system.getString("system").equals("phone"))),
						"system", system,content, "phoneNumber", system.optString("value"));

				
				content = checkExistsAndPut(((system.has("system"))&&(system.getString("system").equals("email"))),
						"system", system,content, "email", system.optString("value"));

			}			
		}*/
		
		/* SET **GENDER** FROM FHIR TO OPENEMPI */	
		JSONObject genderDetails = setGender(patient);		
		content.put("gender", genderDetails);	
		
		
		/* SET BIRTH ORDER FROM FHIR TO OPENEMPI */		
		content=checkExistsAndPut(patient.has("multipleBirthInteger"), "multipleBirthInteger", patient,content, "birthOrder", patient.optString("multipleBirthInteger"));

		/*if(patient.has("multipleBirthInteger")) {
			content.put("birthOrder", patient.optString("multipleBirthInteger"));
		}*/
		
		/* SET DEATH TIME FROM FHIR TO OPENEMPI */
		content=setDeathTime(patient, content);

		/*if(patient.has("deceasedDateTime")) {
			String date = patient.optString("deceasedDateTime");
			date = date.substring(0, 19);
			content.put("deathTime", date);
		}*/
		
		/* SET DATE OF BIRTH FROM FHIR TO OPENEMPI */ 
		content=checkExistsAndPut(patient.has("birthDate"), "birthDate", patient,content, "dateOfBirth", patient.optString("birthDate"));

		/*if(patient.has("birthDate")) {
			content.put("dateOfBirth", patient.optString("birthDate"));
		}*/
		
		/* SET DATE OF CHANGE FROM FHIR TO OPENEMPI */
		content = setMetaData(patient, content);
		/*if(patient.has("meta")) {
			if(patient.getJSONObject("meta").has("lastUpdated"))
			content.put("dateChanged", patient.getJSONObject("meta").optString("lastUpdated"));
		}*/
		
		/* SET IDENTIFIER FROM FHIR TO OPENEMPI */ //need fixing
		content = setPersonIdentifier( patient, content);
		/*if(patient.has("identifier")) {
			JSONArray identifierArray = new JSONArray();
			JSONArray personIdentifier = new JSONArray();

			
			JSONArray receivedIdentifiers = patient.getJSONArray("identifier");
			
			if(receivedIdentifiers.length()>0) {
				for(int j=0; j<receivedIdentifiers.length(); j++) {
					JSONObject identifier = new JSONObject();
					JSONObject systemID = receivedIdentifiers.getJSONObject(j);
					identifier.put("identifier", systemID.optString("value"));
					JSONObject identifierDomain = new JSONObject();
					identifierDomain.put("identifierDomainName", systemID.optString("system"));	
					identifier.put("identifierDomain", identifierDomain);				
					personIdentifier.put(identifier);
				}
				
			}	
			content.put("personIdentifiers", personIdentifier);
		}*/
		
		return content;
	}
	

	protected JSONObject setGender(JSONObject patient) {
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
		return genderDetails;
	}
	
	protected JSONObject createName(JSONObject content,JSONObject details) {		
			JSONObject temp = content;
			/* - Family Name - */
			temp = checkExistLengthAndPut("family", details, temp, "familyName", 0);

			/*if(details.has("family")) {
				if(details.getJSONArray("family").length() > 0){
					content.put("familyName", new String(details.getJSONArray("family").getString(0)));
				}
			}*/
			/* Given Name: JSONArray because it contails First & Middle Name */
			temp = checkExistLengthAndPut("given", details, temp, "givenName", 0);
			temp = checkExistLengthAndPut("given", details, temp, "middleName", 1);

			/*if(details.has("given")) {
				JSONArray given = details.getJSONArray("given");
				
				 Check that there is at least one given Name (first name) 
				if(given.length()>0)
					content.put("givenName", given.getString(0));
				
				 If there are more than 1 given names, the next one is the middle name 
				if(given.length()>1) 
					content.put("middleName", given.getString(1));
			}*/
			
			/* Prefix & Prefix
			 * It is a JSONArray - but OpenEMPI accepts only one
			 * We get the first one
			 */
			temp = checkExistLengthAndPut("prefix", details, temp, "prefix", 0);

			/*if(details.has("prefix")) {
				JSONArray prefix = details.getJSONArray("prefix");
				if(prefix.length()>0)
					content.put("prefix", prefix.getString(0));
	
			}*/
			temp = checkExistLengthAndPut("suffix", details, temp, "suffix", 0);

			/*if(details.has("suffix")) {
				JSONArray suffix = details.getJSONArray("suffix");
				if(suffix.length()>0)
					content.put("suffix", suffix.getString(0));
			}*/
			
			return temp;
		}
		
		protected JSONObject createAddress(JSONObject content,JSONObject address) {
			JSONObject temp = content;
			/* line[0]=Address 1 & Line[1] = Address 2 (if they exist) */
			temp = checkExistLengthAndPut("line", address, temp, "address1", 0);
			temp = checkExistLengthAndPut("line", address, temp, "address2", 1);

			/*if(address.has("line")) {
				JSONArray lines = address.getJSONArray("line");
				
				if(lines.length()>0)
					content.put("address1", lines.getString(0));
				if(lines.length()>1)
					content.put("address2", lines.getString(1));
						
			}*/
			
			/* City */
			temp = checkExistsAndPut(address.has("city"),"city", address, temp, "city", address.optString("city"));
			/*if(address.has("city")) {
				content.put("city", address.getString("city"));
			}*/
			
			/* Country */ 
			temp = checkExistsAndPut(address.has("country"),"country", address,temp, "country", address.optString("country"));
			/*if(address.has("country")) {
				content.put("country", address.getString("country"));
			}*/
			
			/* State */
			temp = checkExistsAndPut(address.has("state"),"state", address, temp,"state", address.optString("state"));
			/*if(address.has("state")) {
				content.put("state", address.getString("state"));
			}*/
			
			/* Postal Code */
			temp = checkExistsAndPut(address.has("postalCode"),"postalCode", address, temp,"postalCode", address.optString("postalCode"));
			/*if(address.has("postalCode")) {
				content.put("postalCode", address.get("postalCode"));
			}*/
			return content;
		}

	protected JSONObject setFullNames(JSONObject patient, JSONObject content) {
		JSONObject temp = content;

		if(patient.has("name")) {
			
			/* FHIR has multiple names */
			JSONArray Namesarray = patient.optJSONArray("name");
			
			if(Namesarray!=null) {
				
				/* OpenEMPI takes only official and maiden name */
				boolean officialFound = false; // it found official name
			
				for(int i=0; i<Namesarray.length(); i++) {
					JSONObject details = Namesarray.getJSONObject(i);
					
					/* Define maiden name if it exists */
					if((details.has("use"))&&details.optString("use").equals("maiden")) {
						if(details.getJSONArray("family").length() > 0){
							temp.put("mothersMaidenName", details.getJSONArray("family").getString(0));
						}
					}
					
					if((details.has("use"))&&(details.optString("use").equals("official"))) {
						officialFound = true;
						temp = createName(temp,details);
						continue;
					}				
				}
				
				//didn't find official - gets the first one
				if(!officialFound) {
					JSONObject details = Namesarray.getJSONObject(0);
					temp = createName(temp,details);
				}
			}			
		}
		return temp;
	}
	protected JSONObject checkExistsAndPut(boolean condition,  String searchField,JSONObject ConditionObject,JSONObject receiver, String key, Object value) {
		JSONObject temp = receiver;

		if(condition) {
			temp.put(key, value);
		}
		return temp;
	}
	
	protected JSONObject checkExistLengthAndPut(String searchField,JSONObject ConditionObject, JSONObject receiver, String key, int threshold) {
		JSONObject temp = receiver;

		if(ConditionObject.has(searchField)&&((ConditionObject.getJSONArray(searchField).length() > threshold))) {
			//if(ConditionObject.getJSONArray(searchField).length() > threshold){
				temp.put(key, new String(ConditionObject.getJSONArray(searchField).getString(threshold)));
			//}
		}				
		return temp;
	}
	protected JSONObject setMaritalStatus(JSONObject patient, JSONObject content) {
		JSONObject temp = content;

		if(patient.has("maritalStatus")) {
			JSONObject status = patient.getJSONObject("maritalStatus");
			if(status.has("coding")) {
				JSONObject code = status.getJSONArray("coding").getJSONObject(0);
				System.out.println(code.toString());
				if(code.has("code")){
					String codeName = code.optString("code");
					if(codeName.equals("M")){
						codeName = "MARRIED";
					}else if(codeName.equals("D")){
						codeName = "DIVORCED";
					}else if(codeName.equals("I")){
						codeName = "INTERLOCUTORY";
					}else if(codeName.equals("L")){
						codeName = "LEGALLY SEPARATED";
					}else if(codeName.equals("P")){
						codeName = "POLYGAMOUS";
					}else if(codeName.equals("S")){
						codeName = "NEVER MARRIED";
					}else if(codeName.equals("T")){
						codeName = "DOMESTIC PARTNER";
					}else if(codeName.equals("W")){
						codeName = "WIDOWED";
					}else if(codeName.equals("A")){
						codeName = "ANNULLED";
					}else{
						codeName = "UNKNOWN";
					}
					System.out.println(codeName + "HI THERE");
					temp.put("maritalStatusCode", codeName);
				}
			}
		}
		return temp;
	}
	
	protected JSONObject setDeathTime(JSONObject patient, JSONObject content) {
		JSONObject temp = content;
		if(patient.has("deceasedDateTime")) {
			String date = patient.optString("deceasedDateTime");
			date = date.substring(0, 19);
			temp.put("deathTime", date);
		}		
		return temp;
	}
	
	protected JSONObject setAddresses(JSONObject patient, JSONObject content) {
		JSONObject temp = content;

		if(patient.has("address")) {
			
			/* Multiple addresses in FHIR but OpenEMPI takes only one */
			JSONArray addresses = patient.getJSONArray("address");
			if(addresses.length()>0) {
				JSONObject address = addresses.getJSONObject(0);
				temp = createAddress(temp, address);			
			}									
		}
		return temp;
	}
	protected JSONObject setTelecom(JSONObject patient, JSONObject content) {
		JSONObject temp = content;

		if(patient.has("telecom")) {
			
			/* Telecom is an array of all the contact details of the patient*/
			JSONArray telecom = patient.getJSONArray("telecom");
			for(int i=0; i<telecom.length(); i++) {
				JSONObject system = telecom.getJSONObject(i);
				
				/* Phone */
				temp = checkExistsAndPut(((system.has("system"))&&(system.getString("system").equals("phone"))),
						"system", system,temp, "phoneNumber", system.optString("value"));

				/*if((system.has("system"))&&(system.getString("system").equals("phone"))) {
					content.put("phoneNumber", system.getString("value"));
				}*/
	
				/* Email */
				/*if((system.has("system"))&&(system.getString("system").equals("email"))) {
					content.put("email", system.getString("value"));
				}*/
				temp = checkExistsAndPut(((system.has("system"))&&(system.getString("system").equals("email"))),
						"system", system,temp, "email", system.optString("value"));

			}			
		}
		
		
		
		return temp;
	}
	
	protected JSONObject setMetaData(JSONObject patient, JSONObject content) {
		JSONObject temp = content;
		if(patient.has("meta")) {
			if(patient.getJSONObject("meta").has("lastUpdated"))
				temp.put("dateChanged", patient.getJSONObject("meta").optString("lastUpdated"));
		}
		return temp;
	}
	
	protected JSONObject setPersonIdentifier( JSONObject patient,JSONObject content) {
		JSONObject temp = content;
		if(patient.has("identifier")) {
			JSONArray personIdentifier = new JSONArray();
	
			/**
			 *
			 * TODO: Get all the identifiers
			 * 
			 */
			JSONArray receivedIdentifiers = patient.getJSONArray("identifier");
			
			if(receivedIdentifiers.length()>0) {
				for(int j=0; j<receivedIdentifiers.length(); j++) {
					JSONObject identifier = new JSONObject();
					JSONObject systemID = receivedIdentifiers.getJSONObject(j);
					identifier.put("identifier", systemID.optString("value"));
					JSONObject identifierDomain = new JSONObject();
					identifierDomain.put("identifierDomainName", systemID.optString("system"));	
					identifier.put("identifierDomain", identifierDomain);				
					personIdentifier.put(identifier);
				}
				
			}	
			temp.put("personIdentifiers", personIdentifier);
		}
		return temp;
	}
	
}
