/**
 * ConversionOpenEMPI_to_FHIR
 * 
 * v2.0
 * 
 * Date: 13-7-2017
 * 
 * Copyrights: Koon Wei Teo, Evanthia Tingiri, Shuti Shina
 * 
 * Description: This class contains the necessary functions to convert OpenEMPI to FHIR.
 * 				It is called by PatientFHIR in the case of the following requests:
 * 				Read & Search
 * 
 */
package fhirconverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Bundle; 
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.PositiveIntType;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import ca.uhn.fhir.context.FhirContext;

public class ConversionOpenEmpiToFHIR {
	
	protected JSONObject conversion(String result){
        JSONObject xmlJSONObj = XML.toJSONObject(result); // converts to jsonobject hashmap
        int PRETTY_PRINT_INDENT_FACTOR = 4;
        //String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);// converts to human readable, add this if needed to system print to test
        //System.out.println(jsonPrettyPrintString);
	JSONObject resourceBundle = new JSONObject();	
	FhirContext ctx = FhirContext.forDstu3();
		if(xmlJSONObj.has("people")){
			Bundle bundle = new Bundle();
			if (xmlJSONObj.optJSONObject("people")!=null) {
				JSONArray persons = xmlJSONObj.optJSONObject("people").optJSONArray("person");
				
				/* "Persons" might be an array with multiple elements
				 * or an object with one record.
				 */
				if(persons==null) {
					JSONObject person = xmlJSONObj.getJSONObject("people").getJSONObject("person");
					//JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
					//searchResults.add(fhirPersonStructure);
					bundle = setBundle(bundle, personMapping(person));
				}
				else {
					for (int i=0; i<persons.length(); i++) {
						//JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString((personMapping(persons.getJSONObject(i)))));
						//searchResults.add(fhirPersonStructure);
						bundle = setBundle(bundle, personMapping(persons.getJSONObject(i)));
					}
				}		
			}
			resourceBundle = new JSONObject(ctx.newJsonParser().encodeResourceToString(bundle));
			String jsonPrettyPrintString = resourceBundle.toString(PRETTY_PRINT_INDENT_FACTOR);// converts to human readable, add this if needed to system print to test
        		System.out.println(jsonPrettyPrintString);
		}
		if(xmlJSONObj.has("person")){
			JSONObject person = xmlJSONObj.getJSONObject("person");
			JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
			return fhirPersonStructure;
		}
		return resourceBundle;
 	}	
		
	protected Bundle setBundle(Bundle bundle, Patient patient){
		bundle.addEntry()
   			.setResource(patient);
		/* Extend if needed Koon
			.getRequest()
      				.setUrl("Patient")
      				.setIfNoneExist("identifier=http://acme.org/mrns|12345"); */
		return bundle;
	}
	protected Patient personMapping(JSONObject node)  { //tried automated using generic objects methods, but inner classes methods differ too much. Unable to get it more automated. By koon.
		Patient p = new Patient();
		
		/* Full Name */
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
	    
		/* Primary Key */ 
		if(node.has("personId")){
	    	IdType id = new IdType(node.optString("personId"));
	    	p.setId(id);
		}	
		
		/* Date Changed */
		if(node.has("dateChanged")) {
			Meta v = new Meta();
			OpenEMPIbase date;
			v.setLastUpdated(convertStringtoDate(node.getString("dateChanged")));
			p.setMeta(v);
		}
		
		/* Marital Status */		
		if(node.has("maritalStatusCode")) {
			CodeableConcept value = new CodeableConcept();
			value.setText(node.getString("maritalStatusCode"));			
			p.setMaritalStatus(value);
		}
		
			
		/* Address */
		if(setAddress(node) !=null)
		p.addAddress(setAddress(node));
		System.out.println("Patient's address: " + p.getAddress().get(0).getText());
		
		
		/* -- Contact Details -- */
		ArrayList<ContactPoint> telecom = new ArrayList<ContactPoint>();

		/*email*/
		if(node.has("email")) {
			ContactPoint email = new ContactPoint();
			email.setSystem(ContactPointSystem.EMAIL).setValue(node.getString("email"));
			telecom.add(email);
		}
		
		/*Phone*/
		ContactPoint phone = new ContactPoint();
		phone = setPhone(node);
		/* REMEMBER TO CHECK IT */
		if(phone.getValue()!=null)
			telecom.add(phone);
		if(telecom.size()>0) {
			p.setTelecom(telecom);
			for(int z=0; z<telecom.size(); z++)
				System.out.println("Contact: " + p.getTelecom().get(z).getValue());
		}
		
		/* -- SET THE BIRTH DETAILS OF THE PATIENT -- */
		if(node.has("birthOrder")) {
			PositiveIntType birthOrder = new PositiveIntType(node.optInt("birthOrder"));			
			p.setMultipleBirth(birthOrder);
		}
		if(node.has("dateOfBirth")){
			p.setBirthDate(convertStringtoDate(node.optString("dateOfBirth")));
		}		
		if(node.has("gender")){
			JSONObject genders = node.getJSONObject("gender");
			if(genders.has("genderDescription")) {
				String gender = genders.optString("genderDescription");
				gender = gender.toUpperCase();
				p.setGender(AdministrativeGender.valueOf(gender));
			}
		}
		
		
		/* Person Identifiers */
		if(node.has("personIdentifiers")){
			p = setPersonIdentifiers(node,p);
		}
		
		/* Death Date & Time */ 
		if(node.has("deathTime")){
			DateTimeType deceasedDate = new DateTimeType(node.optString("deathTime"));
			p.setDeceased(deceasedDate);	
		}	
		return p;
	}

	/* It is used to construct the text attribute of Address */
	private String checkText(Address t) {
		if(t.getText()==null) {
			return "";
		}
		else {
			return t.getText();
		}
	}
	
	/* It is used to construct the Phone Number */
	private String checkPhone(ContactPoint p, boolean exists) {
		if (exists)
			return p.getValue();
		else
			return "";
	}
	
	
	/* Create a FHIR object that contains the Full Name details */ 
	protected HumanName setHumanName(JSONObject node){
		/* Full Name */
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
	
	
	/* Create a FHIR object that contains the address details */ 	
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
	
	protected ContactPoint setPhone(JSONObject node){
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
		
	
		return phone;
	}
	
	protected Patient setPersonIdentifiers(JSONObject node, Patient p) {
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
		
		return p;
	}
	
	/**
	 * This method converts date in string format to date format and removes the time/ 
	 * @param date
	 * @return dateTransform 
	 */
	protected Date convertStringtoDate(String date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		date = date.substring(0, date.length() - 8);
		Date dateTransform = null;
  		try{
         		dateTransform = formatter.parse(date);
			//SimpleDateFormat fhirFormat = new SimpleDateFormat("yyyy-MM-dd");
		} catch (ParseException e){
			e.printStackTrace();
		}
		return dateTransform;
	}	
}
