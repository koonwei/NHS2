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
package fhirconverter.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.util.List;

import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.PositiveIntDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.base.resource.ResourceMetadataMap;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import ca.uhn.fhir.context.FhirContext;

public class ConversionOpenEmpiToFHIR {
	Logger LOGGER = LogManager.getLogger(ConversionOpenEmpiToFHIR.class);

	protected List<Patient> conversion(String result){
		JSONObject xmlJSONObj = XML.toJSONObject(result); // converts to jsonobject hashmap
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		//String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);// converts to human readable, add this if needed to system print to test
		//System.out.println(jsonPrettyPrintString);
		JSONObject resourceBundle = new JSONObject();
		FhirContext ctx = FhirContext.forDstu2();
		List<Patient> patients = new ArrayList<Patient>();
		if(xmlJSONObj.has("people")) {

            LOGGER.info("optJSONArray: " + xmlJSONObj.optJSONArray("people"));
            LOGGER.info("optJSONObj: " + xmlJSONObj.optJSONObject("people"));

//            JSONArray peopleArray = xmlJSONObj.optJSONArray("people");
            JSONObject people = xmlJSONObj.optJSONObject("people");

            if (people != null) {

                JSONObject person = people.optJSONObject("person");
                JSONArray personArray = people.optJSONArray("person");
                if(person != null) {
                    LOGGER.info("Only One Person: " + person);
                    patients.add(personMapping(person));
                }
                else if (personArray != null)
                {
                    for (int i = 0; i < personArray.length(); i++) {
                        JSONObject personData = personArray.getJSONObject(i);
                        LOGGER.info(i + "th Person: " + personData);
                        patients.add(personMapping(personData));
                    }
                }
            }
//            else if (peopleArray != null) {
//                for (int i = 0; i < personArray.length(); i++) {
//                    //JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString((personMapping(persons.getJSONObject(i)))));
//                    //searchResults.add(fhirPersonStructure);
////						bundle = setBundle(bundle, personMapping(persons.getJSONObject(i)));
//                    JSONObject personData = personArray.getJSONObject(i).optJSONObject("person");
//                    LOGGER.info(i + "th Person: " + personData);
//                    patients.add(personMapping(personData));
//                }
//            }
        }

//			if (persons != null) {
//
//				LOGGER.info("Persons: " + persons);
//
//				/* "Persons" might be an array with multiple elements
//				 * or an object with one record.
//				 */
//				if(persons.length()==1) {
//					JSONObject person = persons.getJSONObject(0);
//					//JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
//					//searchResults.add(fhirPersonStructure);
////					bundle = setBundle(bundle, personMapping(person));
//					LOGGER.info("Only One Person: " + person);
//					patients.add(personMapping(person));
//				}
//				else {
//
//				}
//			}
//			resourceBundle = new JSONObject(ctx.newJsonParser().encodeResourceToString(bundle));
//			String jsonPrettyPrintString = resourceBundle.toString(PRETTY_PRINT_INDENT_FACTOR);// converts to human readable, add this if needed to system print to test
//			System.out.println(jsonPrettyPrintString);

		///////////////////
        //  Read
        //////////////////
		if(xmlJSONObj.has("person")){
			JSONObject person = xmlJSONObj.getJSONObject("person");

//			JSONObject fhirPersonStructure = new JSONObject(ctx.newJsonParser().encodeResourceToString(personMapping(person)));
//			return fhirPersonStructure;
			patients.add(personMapping(person));
		}
		return patients;
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
			HumanNameDt maidenName = new HumanNameDt();
			maidenName.addFamily(node.optString("mothersMaidenName"));
			maidenName.setUse(NameUseEnum.MAIDEN);
			p.addName(maidenName);
		}

		System.out.println("Current patient's name: " + p.getName().get(0).getNameAsSingleString());
	    
		/* Primary Key */
		if(node.has("personId")){
			IdDt id = new IdDt(node.optString("personId"));
			p.setId(id);
		}	
		
		/* Date Changed */
		if(node.has("dateChanged")) {
			p.getResourceMetadata().put(ResourceMetadataKeyEnum.UPDATED,  new InstantDt(convertStringtoDate(node.optString("dateChanged"))));
		}
		
		/* Marital Status */
		if(node.has("maritalStatusCode")) {
			String martialStatus = node.getString("maritalStatusCode").toUpperCase();
			if(martialStatus.equals("MARRIED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.M);
			}else if(martialStatus.equals("ANNULLED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.A);
			}else if(martialStatus.equals("DIVORCED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.D);
			}else if(martialStatus.equals("INTERLOCUTORY")){
				p.setMaritalStatus(MaritalStatusCodesEnum.I);
			}else if(martialStatus.equals("LEGALLY SEPARATED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.L);
			}else if(martialStatus.equals("POLYGAMOUS")){
				p.setMaritalStatus(MaritalStatusCodesEnum.P);
			}else if(martialStatus.equals("NEVER MARRIED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.S);
			}else if(martialStatus.equals("DOMESTIC PARTNER")){
				p.setMaritalStatus(MaritalStatusCodesEnum.T);
			}else if(martialStatus.equals("WIDOWED")){
				p.setMaritalStatus(MaritalStatusCodesEnum.W);
			}else{
				p.setMaritalStatus(MaritalStatusCodesEnum.UNK);
			}
		}
		
			
		/* Address */
		if(setAddress(node) !=null)
			p.addAddress(setAddress(node));
		System.out.println("Patient's address: " + p.getAddress().get(0).getText());
		
		
		/* -- Contact Details -- */
		ArrayList<ContactPointDt> telecom = new ArrayList<ContactPointDt>();

		/*email*/
		if(node.has("email")) {
			ContactPointDt email = new ContactPointDt();
			email.setSystem(ContactPointSystemEnum.EMAIL).setValue(node.getString("email"));
			telecom.add(email);
		}
		
		/*Phone*/
		ContactPointDt phone = new ContactPointDt();
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
			PositiveIntDt birthOrder = new PositiveIntDt(node.optInt("birthOrder"));
			p.setMultipleBirth(birthOrder);
		}
		if(node.has("dateOfBirth")){
			String birthDateString = node.optString("dateOfBirth").substring(0,10);
			p.setBirthDate(new DateDt(birthDateString));
		}
		if(node.has("gender")){
			JSONObject genders = node.getJSONObject("gender");
			if(genders.has("genderDescription")) {
				String gender = genders.optString("genderDescription");
				gender = gender.toUpperCase();
				p.setGender(AdministrativeGenderEnum.valueOf(gender));
			}
		}
		
		
		/* Person Identifiers */
		if(node.has("personIdentifiers")){
			p = setPersonIdentifiers(node,p);
		}
		
		/* Death Date & Time */
		if(node.has("deathTime")){
			DateTimeDt deceasedDate = new DateTimeDt(node.optString("deathTime"));
			p.setDeceased(deceasedDate);
		}
		return p;
	}

	/* It is used to construct the text attribute of AddressDt */
	private String checkText(AddressDt t) {
		if(t.getText()==null) {
			return "";
		}
		else {
			return t.getText();
		}
	}

	/* It is used to construct the Phone Number */
	private String checkPhone(ContactPointDt p, boolean exists) {
		if (exists)
			return p.getValue();
		else
			return "";
	}


	/* Create a FHIR object that contains the Full Name details */
	protected HumanNameDt setHumanName(JSONObject node){
		/* Full Name */
		HumanNameDt n = new HumanNameDt();
		
		/*Given Name*/
		if(node.has("givenName")) {
			n.addGiven(node.optString("givenName"));
		}				
		
		/*Family Name*/
		if(node.has("familyName")) {
			n.addFamily(node.optString("familyName"));
			n.setUse(NameUseEnum.OFFICIAL);;
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
	protected AddressDt setAddress(JSONObject node){
		AddressDt t = new AddressDt();
		
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

	protected ContactPointDt setPhone(JSONObject node){
		ContactPointDt phone = new ContactPointDt();
		phone.setSystem(ContactPointSystemEnum.PHONE);
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
			IdentifierDt identifier = new IdentifierDt();
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
				IdentifierDt identifier = new IdentifierDt();
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
