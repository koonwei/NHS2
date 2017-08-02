package fhirconverter.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.logging.log4j.*;
import org.json.JSONObject;

import fhirconverter.exceptions.ResourceNotFoundException;
import fhirconverter.exceptions.OpenEMPIAuthenticationException;
import fhirconverter.exceptions.ResourceNotCreatedException;

/**
 * @author Koon, Shruti Sinha
 *
 */
public class OpenEMPIbase {

	private static String sessionCode; 
	private static final OpenEMPIbase _instance = initialize();
	private String baseURL;
	private String username;
	private String password;


	static final Logger logger = LogManager.getLogger(OpenEMPIbase.class.getName());
	
	/**
	 * The static method reads config.properties file and initialises the common properties for invoking OpenEMPI 
	 * like base URL, user name and password 
	 * @return
	 */
	public static OpenEMPIbase initialize(){
		
		OpenEMPIbase newInstance = new OpenEMPIbase();
		HashMap<String,String> connectionCreds = Utils.getProperties("OpenEMPI");
		newInstance.baseURL = connectionCreds.get("baseURL");
	 	newInstance.username = connectionCreds.get("username");
		newInstance.password =  connectionCreds.get("password");
		
		/* Shruti, can we remove this?
		try {
			Properties properties = new Properties();		
			FileReader reader = new FileReader("config.properties");
			properties.load(reader);
			newInstance.baseURL = properties.getProperty("OpenEMPI-baseURL");
			newInstance.username = properties.getProperty("OpenEMPI-username");
			newInstance.password = properties.getProperty("OpenEMPI-password");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	*/	
		return newInstance;
	}

	/**
	 * This methods call the OpenEMPI API to get the session code
	 * 
	 * @throws Exception
	 */
	private static void getSessionCode() throws Exception {
		
		if(sessionCode != null)
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
	 * @param parameters
	 *            : JSONObjects
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonSearchPersonByAttributes(JSONObject parameters) throws Exception {

		if (parameters.length() == 0)
			return loadAllPersons();
//			throw new ResourceNotFoundException("Resource Not Found");

		getSessionCode();
		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/findPersonsByAttributes");

		String familyName = null;
		String dob = null;
		String gender = null;
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
			for (int i = 0; i < 3; i++) {

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
					payload = payload + "<gender><genderName>" + gender + "</genderName></gender>";
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
					//break;
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
	 * @param parameters
	 *            : JSONObjects
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
		String payload = "	<personIdentifier>" 
						+ "<identifier>" + identifier + "</identifier>" 
						+ "<identifierDomain>"
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
	 * @param parameter:
	 *            personId
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonReadPerson(String parameter) throws Exception {

		getSessionCode();

		int id = Integer.parseInt(parameter);

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
			logger.info("*** Method: commonReadPerson Response: " + response + " ***");
			if (response.equals("")) {
				throw new ResourceNotFoundException("Resource Not Found");
			}
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods invokes updatePersonById API of OpenEMPI and updates person
	 * details
	 * 
	 * @param parameters:
	 *            person element in XML string format
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	public String commonUpdatePerson(String parameters) throws Exception {

		getSessionCode();

		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/updatePersonById");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		if (parameters.isEmpty())
			return null;

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(parameters);
		osw.flush();
		osw.close();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			if (response.equals("")) {
				this.commonAddPerson(parameters);
				logger.info("*** Method: commonUpdate Response: ** Created ** " + response + " ***");
				return "Created";
			} else {
				logger.info("*** Method: commonUpdate Response: ** Updated ** " + response + " ***");
				return "Updated";
			}
		} catch (Exception ex) {
			throw new ResourceNotCreatedException("Resource Not Created/Updated");
		}
	}

	/**
	 * This method invokes addPerson API of OpenEMPI to create a new patient
	 * record
	 * 
	 * @param parameters:
	 *            person element in XML string format
	 * @return newly created person element in XML string format
	 * @throws Exception
	 */
	public String commonAddPerson(String parameters) throws Exception {

		getSessionCode();

		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addPerson");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		/*
		 * Checks if NHS identifier exist in OpenEMPI database. If NHS
		 * identifier doesn't exist, then a new identifier is created for NHS
		 * otherwise proceed to add person
		 */
		if (parameters.contains("NHS") || parameters.contains("https://fhir.nhs.uk/Id/nhs-number")) {
			logger.info("*** Identifier Domain is NHS ***");
			if (!this.checkIfIdendifierExists("NHS")) {
				logger.info("*** Identifier Domain is NHS does not exist in OpenEMPI database ***");
				this.addIdentifier("NHS");
			}
		}

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(parameters);
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
	 * This method takes personId and calls the commomReadPerson to retrieves
	 * the person details and then calls the deletePersonById API with the
	 * person details and delete the person form OpenEMPI
	 * 
	 * @param parameters
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
			if (response == "") {
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
	 * @param parameters:
	 *            String
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
	 * This method takes identifier name and invokes openEMPI API to retrieve
	 * all the existing identifier domain. It then checks if the given
	 * identifier exists or not. Returns true or false accordingly
	 * 
	 * @param identifierName
	 *            : String
	 * @return Boolean: True if identifier exists in openEMPI database,
	 *         otherwise false
	 * @throws Exception
	 */
	protected Boolean checkIfIdendifierExists(String identifierName) throws Exception {

		getSessionCode();

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
			if (response.contains(identifierName)){
				logger.info("*** Method: checkIfIdendifierExists Response: true ***");
				return true;
			}
			else{
				logger.info("*** Method: checkIfIdendifierExists Response: false ***");
				return false;
			}
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}

	}

	/**
	 * This methods takes identifier name and invokes openEMPI API to add the
	 * identifier details
	 * 
	 * @param identifierName:
	 *            String
	 * @return String: newly created identifier details
	 * @throws Exception
	 */
	protected String addIdentifier(String identifierName) throws Exception {

		getSessionCode();

		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addIdentifierDomain");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("mediaType", "*/*");
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		String payload = "<identifierDomain>" + 
				"<identifierDomainName>" + identifierName + "</identifierDomainName>"
				+ "<namespaceIdentifier>" + identifierName + "</namespaceIdentifier>" 
				+ "<universalIdentifier>" + identifierName + "</universalIdentifier>" 
				+ "<universalIdentifierTypeCode>" + identifierName+ "</universalIdentifierTypeCode>" 
				+ "</identifierDomain>";

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
			logger.info("*** Method: addIdentifier Response: " + response + " ***");
			logger.info("*** Identifier Domain is NHS added to OpenEMPI database ***");
			return response;
		} catch (Exception ex) {
			throw new ResourceNotCreatedException("Resource Not Created");
		}
	}

	/**
	 * 
	 * @param firstRecord
	 * @param maxRecords
	 * @return String
	 * @throws Exception
	 */
	public String loadAllPersons(Integer firstRecord, Integer maxRecords) throws Exception {
		getSessionCode();

		URL url = new URL(
				_instance.baseURL +
						"openempi-admin/openempi-ws-rest/person-query-resource/loadAllPersonsPaged?firstRecord="
						+ firstRecord + "&maxRecords=" + maxRecords);
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
			logger.debug("*** Method: loadAllPerson Response:" + response + "***");
			if (response == "") {
				throw new ResourceNotFoundException("Resource Not Found");
			}
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String loadAllPersons() throws Exception {
		return loadAllPersons(0, 1000);
	}
}
