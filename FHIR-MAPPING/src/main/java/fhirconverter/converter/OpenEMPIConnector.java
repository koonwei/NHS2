/**
 * ConversionOpenEMPI_to_FHIR
 *
 * v2.0
 *
 * Date: 13-7-2017
 *
 * Copyrights: Koon Wei Teo, Evanthia Tingiri, Shruti Sinha
 *
 * Description: This class contains the necessary functions call OpenEMPI APIs.
 *
 */

package fhirconverter.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import fhirconverter.exceptions.OpenEMPIAuthenticationException;
import fhirconverter.exceptions.ResourceNotCreatedException;
import fhirconverter.exceptions.ResourceNotFoundException;

/**
 * @author Koon, Shruti Sinha
 *
 */
public class OpenEMPIConnector {

	private static String sessionCode;
	private static final OpenEMPIConnector _instance = initialize();
	private String baseURL;
	private String username;
	private String password;

	private static final Logger logger = LogManager.getLogger(OpenEMPIConnector.class.getName());

	/**
	 * The static method getProperties method to initialise the common
	 * properties for invoking OpenEMPI like base URL, user name and password
	 * 
	 * @return
	 */
	public static OpenEMPIConnector initialize() {

		OpenEMPIConnector newInstance = new OpenEMPIConnector();
		HashMap<String, String> connectionCreds = Utils.getProperties("OpenEMPI");
		newInstance.baseURL = connectionCreds.get("baseURL");
		newInstance.username = connectionCreds.get("username");
		newInstance.password = connectionCreds.get("password");

		return newInstance;
	}

	/**
	 * This methods call the OpenEMPI API to get the session code
	 * 
	 * @throws Exception
	 */
	private static void getSessionCode() throws Exception {

		if (sessionCode != null)
			return;

		URL url = new URL(_instance.baseURL + "/openempi-admin/openempi-ws-rest/security-resource/authenticate");

		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("Accept", "application/xml");
		String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<authenticationRequest><password>" + _instance.password + "</password><username>"
				+ _instance.username + "</username></authenticationRequest>";

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(payload);
		osw.flush();
		osw.close();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			sessionCode = in.readLine();
		} catch (Exception ex) {
			throw new OpenEMPIAuthenticationException("Session Not Validated");
		}
	}

	/**
	 * This methods invokes findPersonsByAttributes API of OpenEMPI and
	 * retrieves person details based on family name, given name, suffix,
	 * prefix, gender or date of birth
	 * 
	 * @param JSONObject: parameters
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonSearchPersonByAttributes(JSONObject parameters) throws Exception {

		if (parameters.length() == 0)
			return loadAllPersons();
		// throw new ResourceNotFoundException("Resource Not Found");

		getSessionCode();
		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/findPersonsByAttributes");

		String familyName = null;
		String dob = null;
		String gender = null;
		String givenName = null;
		String finalresponse = "";
		String value = "";

		/*
		 * The FHIR request can contain given name, suffix or prefix in the name
		 * field. So openEMPI findPersonsByAttributes API is invoked 3 times
		 * with name as givenName, suffix and prefix and the three responses are
		 * combined and checked to remove any duplicate entries.
		 */
		String[] name = new String[] { "givenName", "suffix", "prefix" };
		try {
			for (int i = 0; i < name.length; i++) {

				String payload = "<person>";
				if (parameters.has("family")) {
					familyName = parameters.getString("family");
					payload = payload + "<familyName>" + familyName + "</familyName>";
				}
				if (parameters.has("name")) {
					value = parameters.getString("name");
					payload = payload + "<" + name[i] + ">" + value + "</" + name[i] + ">";
				}
				if (parameters.has("birthdate")) {
					dob = parameters.getString("birthdate");
					payload = payload + "<dateOfBirth>" + dob + "</dateOfBirth>";
				}
				if (parameters.has("gender")) {
					gender = parameters.getString("gender");
					if(("female").equals(gender))
						payload = payload + "<gender>"
								+ "<genderName>" + gender + "</genderName>"
								+ "<genderCode>F</genderCode>"
								+ "</gender>";
					else if(("male").equals(gender))
						payload = payload + "<gender>"
								+ "<genderName>" + gender + "</genderName>"
								+ "<genderCode>M</genderCode>"
								+ "</gender>";
					else if(("unknown").equals(gender))
						payload = payload + "<gender>"
								+ "<genderName>" + gender + "</genderName>"
								+  "<genderCode>U</genderCode>"
								+ "</gender>";
					else if(("other").equals(gender))
						payload = payload + "<gender>"
								+ "<genderName>" + gender + "</genderName>"
								+ "<genderCode>O</genderCode>"
								+ "</gender>";
				}
				if (parameters.has("given")) {
					givenName = parameters.getString("given");
					payload = payload + "<givenName>" + givenName + "</givenName>";
				}
				payload = payload + "</person>";

				HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
				hurl.setRequestMethod("POST");
				hurl.setDoOutput(true);
				hurl.setRequestProperty("Content-Type", "application/xml");
				hurl.setRequestProperty("Accept", "application/xml");
				hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
				OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
				osw.write(payload);
				osw.flush();
				osw.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
				String line;
				String response = "";
				while ((line = in.readLine()) != null) {
					response += line;
				}
				if (response.contains("<person>")) {
					finalresponse += response;
					// break;
				}
			}
			finalresponse = Utils.removeDuplicateRecords(finalresponse);
			logger.info("*** Method: CommonSerachByAttributes Response: " + finalresponse + " ***");
			return finalresponse;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods invokes findPersonById API of OpenEMPI and retrieves person
	 * details based on identifier
	 * 
	 * @param JSONObjects: parameters
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonSearchPersonById(JSONObject parameters) throws Exception {

		if (parameters.length() == 0)
			throw new ResourceNotFoundException("Resource Not Found");

		getSessionCode();

		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/findPersonById");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("POST");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("mediaType", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		String identifier = parameters.getString("identifier_value");
		String identifierDomainName = parameters.getString("identifier_domain");
		String payload = "	<personIdentifier>" + "<identifier>" + identifier + "</identifier>" + "<identifierDomain>"
				+ "<identifierDomainName>" + identifierDomainName + "</identifierDomainName>"
				+ "</identifierDomain> </personIdentifier>";

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(payload);
		osw.flush();
		osw.close();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			logger.info("*** Method: commonSearchPersonById Response: " + response + " ***");
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods invokes loadPerson API of OpenEMPI and retrieves person
	 * details based on personId
	 * 
	 * @param String : personId
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String loadPerson(String personId) throws Exception {

		getSessionCode();

		int id = Integer.parseInt(personId);

		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/loadPerson?personId=" + id);
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("GET");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			logger.info("*** Method: loadPerson Response: " + response + " ***");
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}
	
	
	/**
	 * This methods calls loadPerson() method and retrieves person
	 * details based on personId
	 * 
	 * @param parameter:
	 *            personId
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonReadPerson(String personId) throws Exception {

		try {
			String response = this.loadPerson(personId);
			logger.info("*** Method: commonReadPerson Response: " + response + " ***");
			if (("").equals(response)) {
				throw new ResourceNotFoundException("Resource Not Found");
			}
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * UPDATE ALWAYS HAS PERSON ID 
	 * This methods invokes updatePersonById API of OpenEMPI and updates person
	 * details
	 * 
	 * @param String : parameters
	 *            person element in XML string format
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonUpdatePerson(String parameters) throws Exception {

		getSessionCode();
		String updateParameter = parameters;
		String returnString = "Updated";
		String personId = "";
		if (!parameters.isEmpty() && parameters.contains("<personId>")) {
			personId = parameters.substring(parameters.indexOf("<personId>") + 10, parameters.indexOf("</personId>"));
		}
		String loadPersonId = this.loadPerson(personId);
		try {
			if (loadPersonId == null || loadPersonId.equals("") ) {
				returnString = "Created";
				if (parameters.contains("OpenEMPI") || parameters.contains("openEMPI")
						|| parameters.contains("openempi"))
					updateParameter = this.removeOpenEMPIIdentifier(parameters);
			} else {
				this.commonRemovePersonById(personId);
				returnString = "Updated";
			}
			this.commonAddPerson(updateParameter);
			return returnString;
		} catch (Exception ex) {
			throw new ResourceNotCreatedException("Resource Not Created/Updated");
		}

	}

	/**
	 * This method invokes addPerson API of OpenEMPI to create a new patient
	 * record
	 * 
	 * @param String : parameters:
	 *            person element in XML string format
	 * @return newly created person element in XML string format
	 * @throws Exception
	 */
	public String commonAddPerson(String parameters) throws Exception {

		getSessionCode();
		String addParameters = parameters;
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addPerson");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		List<String> newIdentifierDomainList = getDomainsNotInOpenEMPI(parameters);
		if (!newIdentifierDomainList.isEmpty()) {
			this.addIdentifier(newIdentifierDomainList);
		}
		if (parameters.contains("OpenEMPI")) {
			addParameters = this.removeOpenEMPIIdentifier(parameters);
		}

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(addParameters);
		osw.flush();
		osw.close();
		String response = "";

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			logger.info("*** Method: commonAddPerson Response: " + response + " ***");
			return response;
		} catch (Exception e) {
			throw new ResourceNotCreatedException("Resource Not Created");
		}
	}

	
	/**
	 * This method takes list of identifier domain names and invokes openEMPI
	 * API to retrieve all the existing identifier domain. It then checks if the
	 * given identifier exists or not. Returns true or false accordingly
	 * 
	 * @return List<String> : list of existing identifier domain name
	 * @throws Exception
	 */
	public List<String> getIdentifierDomains() throws Exception {

		getSessionCode();
		List<String> existingDomainList = new ArrayList<>();
		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/getIdentifierDomains");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("GET");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}

			existingDomainList = Utils.convertToList(response);
			return existingDomainList;

		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods takes identifier name and invokes openEMPI API to add the
	 * identifier details
	 * 
	 * @param  String : identifierName:
	 *            
	 * @return String: newly created identifier details
	 * @throws Exception
	 */
	public String addIdentifier(List<String> identifiersList) throws Exception {

		getSessionCode();

		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addIdentifierDomain");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("mediaType", "*/*");
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		try {
			if (!identifiersList.isEmpty()) {
				for (String identifierName : identifiersList) {
					String payload = "<identifierDomain>" + "<identifierDomainName>" + identifierName
							+ "</identifierDomainName>" + "<namespaceIdentifier>" + identifierName
							+ "</namespaceIdentifier>" + "<universalIdentifier>" + identifierName
							+ "</universalIdentifier>" + "<universalIdentifierTypeCode>" + identifierName
							+ "</universalIdentifierTypeCode>" + "</identifierDomain>";

					OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
					osw.write(payload);
					osw.flush();
					osw.close();

					BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
					String line;
					String response = "";
					while ((line = in.readLine()) != null) {
						response += line;
					}
					logger.info("*** Method: addIdentifier Response: " + response + " ***");
					logger.info("*** New Identifier Domain added to OpenEMPI ***");

				}
			}
			return "";
		} catch (Exception ex) {
			throw new ResourceNotCreatedException("Resource Not Created");
		}
	}

	/**
	 * This method takes personId and calls the commomReadPerson to retrieves
	 * the person details and then calls the deletePersonById API with the
	 * person details and delete the person form OpenEMPI
	 * 
	 * @param String : parameters
	 * @return String: Successful if delete is successful otherwise throws
	 *         ResourceNotFoundException
	 * @throws Exception
	 */
	public String commonDeletePersonById(String parameters) throws Exception {

		String payload = commonReadPerson(parameters);
		if (payload == null)
			throw new ResourceNotFoundException("Resource Not Found");

		getSessionCode();
		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/deletePersonById");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "text/xml");
		hurl.setRequestProperty("mediaType", "*/*");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(payload);
		osw.flush();
		osw.close();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			if (("").equals(response)) {
				logger.info("*** Method: commonDeletePersonById Response: Delete Successful ***");
				return "Delete Successful";
			} else
				throw new ResourceNotFoundException("Resource Not Found");
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This method takes personId as parameter and invokes removePersonById API
	 * with the person details and removes the person form OpenEMPI
	 * 
	 * @param String: parameters:
	 *            
	 * @return String: Successful if remove is successful otherwise throws
	 *         ResourceNotFoundException
	 * @throws Exception
	 */
	public String commonRemovePersonById(String parameter) throws Exception {

		getSessionCode();

		int id = Integer.parseInt(parameter);

		URL url = new URL(_instance.baseURL
				+ "openempi-admin/openempi-ws-rest/person-manager-resource/removePersonById?personId=" + id);
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("POST");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			if (response == "") {
				logger.info("*** Method: commonRemovePersonById Response: Remove Successful ***");
				return "Remove Successful";
			} else
				throw new ResourceNotFoundException("Resource Not Found");
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * 
	 * @param Integer : firstRecord
	 * @param Integer : maxRecords
	 * @return String in XML format
	 * @throws Exception
	 */
	public String loadAllPersons(Integer firstRecord, Integer maxRecords) throws Exception {
		getSessionCode();

		URL url = new URL(_instance.baseURL
				+ "openempi-admin/openempi-ws-rest/person-query-resource/loadAllPersonsPaged?firstRecord=" + firstRecord
				+ "&maxRecords=" + maxRecords);
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("GET");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			logger.info("*** Method: loadAllPerson Response:" + response + "***");
			if (("").equals(response)) {
				throw new ResourceNotFoundException("Resource Not Found");
			}
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * 
	 * @return String in XML format
	 * @throws Exception
	 */
	public String loadAllPersons() throws Exception {
		return loadAllPersons(0, 1000);
	}
	
	/**
	 * * This method iterates through the request for adding a new person. If the request contains 
	 * OpenEMPI identifier than that identifierDomain is removed from the request to avoid 
	 * any conflict while creating a new person in OpenEMPI.
	 * 
	 * @param String : parameters in XML format
	 * @return String: XML format
	 */
	protected String removeOpenEMPIIdentifier(String parameters) {

		String afterRemove = parameters;
		JSONObject jsonFromXML = XML.toJSONObject(parameters);
		if (jsonFromXML.has("person")) {
			JSONObject person = jsonFromXML.optJSONObject("person");
			if (person.has("personIdentifiers")) {
				JSONArray personIdentifiersArray = person.optJSONArray("personIdentifiers");
				JSONObject personIdentifierObj = person.optJSONObject("personIdentifiers");
				if (personIdentifierObj != null) {
					if (personIdentifierObj.has("identifierDomain")) {
						JSONObject identifierDomain = personIdentifierObj.getJSONObject("identifierDomain");
						if (identifierDomain.has("identifierDomainName")) {
							String identifierDomainName = identifierDomain.getString("identifierDomainName");
							if (identifierDomainName.equalsIgnoreCase("openempi")) {
								person.remove("personIdentifiers");
							}
						}
					}
				} else if (personIdentifiersArray != null) {
					for (int i = 0; i < personIdentifiersArray.length(); i++) {
						JSONObject personIdentifier = personIdentifiersArray.getJSONObject(i);
						if (personIdentifier.has("identifierDomain")) {
							JSONObject identifierDomain = personIdentifier.getJSONObject("identifierDomain");
							if (identifierDomain.has("identifierDomainName")) {
								String identifierDomainName = identifierDomain.getString("identifierDomainName");
								if (identifierDomainName.equalsIgnoreCase("openempi"))
									personIdentifiersArray.remove(i);
							}
						}
					}
				}
			}
		}
		afterRemove = XML.toString(jsonFromXML);
		return afterRemove;

	}
	
	/**
	 * This methods calls getIdentifierDomains() and fetchIdDomainsInRequest() methods to get the list
	 * of identifier domain present in openEMPI and list of identifier domain present in the person XML 
	 * request, compares the list and return any new identifier(s) which is present in person xml request
	 * but not in OpenEMPI
	 * 
	 * 
	 * @param String : parameters
	 * @return List<String> : List of identifier domain name  
	 * @throws Exception
	 */
	protected List<String> getDomainsNotInOpenEMPI(String parameters) throws Exception {

		List<String> existingIdDomains = this.getIdentifierDomains();
		List<String> obtainedIdDomains = this.fetchIdDomainsInRequest(parameters);
		List<String> newDomainList = new ArrayList<>();
		for (String item : obtainedIdDomains) {
			if (!existingIdDomains.contains(item)) {
				newDomainList.add(item);
			}
		}
		return newDomainList;
	}

	/**	
	 * This method iterates through the person XML and checks the identifier domain name present 
	 * in it and adds them to a List  
	 * 
	 * @param String : XML format 
	 * @return List<String> : list of identifier domain name 
	 */
	protected List<String> fetchIdDomainsInRequest(String xml) {
		List<String> obtainedIdDomainsList = new ArrayList<>();
		JSONObject jsonFromXML = XML.toJSONObject(xml);
		if (jsonFromXML.has("person")) {
			JSONObject person = jsonFromXML.optJSONObject("person");
			if (person.has("personIdentifiers")) {
				JSONArray personIdentifiersArray = person.optJSONArray("personIdentifiers");
				JSONObject personIdentifierObj = person.optJSONObject("personIdentifiers");
				if (personIdentifierObj != null) {
					if (personIdentifierObj.has("identifierDomain")) {
						JSONObject identifierDomain = personIdentifierObj.getJSONObject("identifierDomain");
						if (identifierDomain.has("identifierDomainName")) {
							String identifierDomainName = identifierDomain.getString("identifierDomainName");
							if (!identifierDomainName.equalsIgnoreCase("openempi")) {
								obtainedIdDomainsList.add(identifierDomainName);
							}
						}
					}
				} else if (personIdentifiersArray != null) {
					for (int i = 0; i < personIdentifiersArray.length(); i++) {
						JSONObject personIdentifier = personIdentifiersArray.getJSONObject(i);
						if (personIdentifier.has("identifierDomain")) {
							JSONObject identifierDomain = personIdentifier.getJSONObject("identifierDomain");
							if (identifierDomain.has("identifierDomainName")) {
								String identifierDomainName = identifierDomain.getString("identifierDomainName");
								if (!identifierDomainName.equalsIgnoreCase("openempi"))
									obtainedIdDomainsList.add(identifierDomainName);
							}
						}
					}
				}
			}
		}
		return obtainedIdDomainsList;
	}
}
