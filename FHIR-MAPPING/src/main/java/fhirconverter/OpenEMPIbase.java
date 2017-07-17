package fhirconverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

//import com.github.fge.jsonpatch.JsonPatch;

import fhirconverter.exceptions.ResourceNotFoundException;
import fhirconverter.exceptions.OpenEMPIAuthenticationException;
import fhirconverter.exceptions.ResourceNotCreatedException;

/**
 * @author Koon, Shruti Sinha
 *
 */
public class OpenEMPIbase {

	private static String sessionCode;
	/*
	 * protected abstract JSONObject search(JSONObject parameters) throws
	 * Exception; protected abstract JSONObject read(String id) throws
	 * Exception; protected abstract String patch(String id, JsonPatch
	 * parameters) throws Exception; protected abstract String create(JSONObject
	 * patient) throws Exception; protected abstract String delete(String id)
	 * throws Exception; protected abstract String update(String id, JSONObject
	 * patient) throws Exception;
	 */
	private static final OpenEMPISession _instance = OpenEMPISession.initialize();

	/**
	 * This methods call the OpenEMPI API to get the session code
	 * 
	 * @throws Exception
	 */
	private static void getSessionCode() throws Exception {

		URL url = new URL(_instance.baseURL + "/openempi-admin/openempi-ws-rest/security-resource/authenticate");

		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml"); // application/json
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
	 * retrieves person details based on family name, given name or date of
	 * birth
	 * 
	 * @param parameters
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	protected String commonSearchPersonByAttributes(JSONObject parameters) throws Exception {

		getSessionCode();
		String familyName = null;
		String givenName = null;
		String dob = null;
		URL url = new URL(
				_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/findPersonsByAttributes");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("POST");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("Accept", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		if (parameters.length() == 0)
			return null;

		String payload = "<person>";

		if (parameters.has("familyName")) {
			familyName = parameters.getString("familyName");
			payload = payload + "<familyName>" + familyName + "</familyName>";
		}
		if (parameters.has("givenName")) {
			givenName = parameters.getString("givenName");
			payload = payload + "<givenName>" + givenName + "</givenName>";
		}
		if (parameters.has("dateOfBirth")) {
			dob = parameters.getString("dateOfBirth");
			payload = payload + "<dateOfBirth>" + dob + "</dateOfBirth>";
		}
		payload = payload + "</person>";

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
			System.out.println("Abstract Class: OpenEMPIbase Method: CommonSerachByAttributes Response:" + response);
			return response;
		} catch (Exception e) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods invokes loadPerson API of OpenEMPI and retrieves person
	 * details based on personId
	 * 
	 * @param parameter: personId
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	protected String commonReadPerson(String parameter) throws Exception {

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
			System.out.println("Abstract Class: OpenEMPIbase Method: commonReadPerson Response:" + response);
			return response;
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This methods invokes loadPerson API of OpenEMPI and updates person
	 * details
	 * 
	 * @param parameters: person element in XML string format 
	 * @return String in XML format: person details
	 * @throws Exception
	 */
	protected String commonUpdatePerson(String parameters) throws Exception {

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

		String payload = parameters;

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(payload);
		osw.flush();
		osw.close();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			if (in.toString().equals("")) {
				this.commonAddPerson(parameters);
				return "Created";
			} else {
				String line;
				String response = "";
				while ((line = in.readLine()) != null) {
					response += line;
				}
				System.out.println("Abstract Class: OpenEMPIbase Method: commonUpdateById Response:" + response);
				return "Updated";
			}
		} catch (Exception ex) {
			throw new ResourceNotCreatedException("Resource Not Updated");
		}
	}

	/**
	 * This method invokes addPerson API of OpenEMPI to create a new patient
	 * record
	 * 
	 * @param parameters: person element in XML string format
	 * @return newly created person element in XML string format
	 * @throws Exception
	 */
	protected String commonAddPerson(String parameters) throws Exception {

		getSessionCode();

		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addPerson");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
		hurl.setRequestMethod("PUT");
		hurl.setDoOutput(true);
		hurl.setRequestProperty("Content-Type", "application/xml");
		hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);

		String payload = parameters;

		OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
		osw.write(payload);
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
			System.out.println("Abstract Class: OpenEMPIbase Method: commonAddPerson Response:" + response);
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
	 *         exception
	 * @throws Exception
	 */
	protected String commonDeletePersonById(String parameters) throws Exception {

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
		;
		osw.flush();
		osw.close();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
			String line;
			String response = "";
			while ((line = in.readLine()) != null) {
				response += line;
			}
			System.out.println("Abstract Class: OpenEMPIbase Method: commonDeletePersonById Response:" + response);
			if (response == "")
				return "Delete Successful";
			else
				throw new ResourceNotFoundException("Resource Not Found");
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	/**
	 * This method takes personId as parameter and invokes removePersonById API
	 * with the person details and removes the person form OpenEMPI
	 * 
	 * @param parameters
	 * @return String: Successful if remove is successful otherwise throws
	 *         Exception
	 * @throws Exception
	 */
	protected String commonRemovePersonById(String parameter) throws Exception {

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
			System.out.println("Abstract Class: OpenEMPIbase Method: commonRemovePersonById Response:" + response);
			if (response == "")
				return "Remove Successful";
			else
				throw new ResourceNotFoundException("Resource Not Found");
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Resource Not Found");
		}
	}

	protected boolean validateJsonScheme() {
		// File schemaFile = new File("/resource/openempi.json");
		return true;

	}

}
