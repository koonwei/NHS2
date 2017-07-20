/**
 * ConversionFHIR_to_OpenEMPI
 * 
 * v2.0
 * 
 * Date: 13-7-2017
 * 
 * Copyrights: Koon Wei Teo, Evanthia Tingiri, Shuti Shina
 * 
 * Description: This class contains the necessary functions to convert FHIR to OpenEMPI.
 * 				It is called by PatientFHIR in the case of the following requests:
 * 				Create, Update and Patch
 * 
 */

package fhirconverter;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConversionFHIRToOpenEmpi {
	
	
	protected JSONObject conversionToOpenEMPI(JSONObject patient) {
		JSONObject content = new JSONObject();
		
		/* SET THE FULL NAME FROM FHIR TO OPENEMPI */		
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
							content.put("mothersMaidenName", details.getJSONArray("family").getString(0));
						}
					}
					
					if((details.has("use"))&&(details.optString("use").equals("official"))) {
						officialFound = true;
						content = createName(content,details);
						continue;
					}				
				}
				
				//didn't find official - gets the first one
				if(!officialFound) {
					JSONObject details = Namesarray.getJSONObject(0);
					content = createName(content,details);
				}
			}			
		}
		
		/* SET THE ADDRESS FROM FHIR TO OPENEMPI */		
		if(patient.has("address")) {
			
			/* Multiple addresses in FHIR but OpenEMPI takes only one */
			JSONArray addresses = patient.getJSONArray("address");
			if(addresses.length()>0) {
				JSONObject address = addresses.getJSONObject(0);
				content = createAddress(content, address);			
			}									
		}
		
		/* SET THE MARITAL STATUS FROM FHIR TO OPENEMPI */
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
					content.put("maritalStatusCode", codeName);
				}
			}
		}

		/* SET PHONE NUMBER AND EMAIL FROM FHIR TO OPENEMPI */		
		if(patient.has("telecom")) {
			
			/* Telecom is an array of all the contact details of the patient*/
			JSONArray telecom = patient.getJSONArray("telecom");
			for(int i=0; i<telecom.length(); i++) {
				JSONObject system = telecom.getJSONObject(i);
				
				/* Phone */
				if((system.has("system"))&&(system.getString("system").equals("phone"))) {
					content.put("phoneNumber", system.getString("value"));
				}
	
				/* Email */
				if((system.has("system"))&&(system.getString("system").equals("email"))) {
					content.put("email", system.getString("value"));
				}
			}			
		}
		
		/* SET **GENDER** FROM FHIR TO OPENEMPI */	
		JSONObject genderDetails = setGender(patient);		
		content.put("gender", genderDetails);	
		
		
		/* SET BIRTH ORDER FROM FHIR TO OPENEMPI */		
		if(patient.has("multipleBirthInteger")) {
			content.put("birthOrder", patient.optString("multipleBirthInteger"));
		}
		
		/* SET DEATH TIME FROM FHIR TO OPENEMPI */
		if(patient.has("deceasedDateTime")) {
			String date = patient.optString("deceasedDateTime");
			date = date.substring(0, 19);
			content.put("deathTime", date);
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
			content.put("personIdentifiers", personIdentifier);
		}
		
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
			
			/* - Family Name - */
			if(details.has("family")) {
				if(details.getJSONArray("family").length() > 0){
					content.put("familyName", new String(details.getJSONArray("family").getString(0)));
				}
			}
			/* Given Name: JSONArray because it contails First & Middle Name */
			if(details.has("given")) {
				JSONArray given = details.getJSONArray("given");
				
				/* Check that there is at least one given Name (first name) */
				if(given.length()>0)
					content.put("givenName", given.getString(0));
				
				/* If there are more than 1 given names, the next one is the middle name */
				if(given.length()>1) 
					content.put("middleName", given.getString(1));
			}
			
			/* Prefix & Prefix
			 * It is a JSONArray - but OpenEMPI accepts only one
			 * We get the first one
			 */
			if(details.has("prefix")) {
				JSONArray prefix = details.getJSONArray("prefix");
				if(prefix.length()>0)
					content.put("prefix", prefix.getString(0));
	
			}
			if(details.has("suffix")) {
				JSONArray suffix = details.getJSONArray("suffix");
				if(suffix.length()>0)
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
			
			/* Postal Code */
			if(address.has("postalCode")) {
				content.put("postalCode", address.get("postalCode"));
			}
			return content;
		}

	
	
}
