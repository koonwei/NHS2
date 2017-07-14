package fhirconverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.github.fge.jsonpatch.JsonPatch;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException; 
/**
 * @author Koon, Shruti Sinha
 *
 */
public abstract class OpenEMPIbase{
	

	protected abstract JSONObject search(JSONObject parameters) throws Exception;
	protected abstract JSONObject read(String id) throws Exception;
	protected abstract String patch(String id, JsonPatch parameters) throws Exception;
	protected abstract String create(JSONObject patient) throws Exception;
	protected abstract String delete(String id) throws Exception;
	protected abstract String update(String id) throws Exception;
	private static String sessionCode; 
	private static final OpenEMPISession _instance = OpenEMPISession.initialize();
	
	/**
	 * This methods call the OpenEMPI API to get the session cod
	 * @throws Exception
	 */
	private static void getSessionCode() throws Exception{
		
		URL url = new URL(_instance.baseURL + "/openempi-admin/openempi-ws-rest/security-resource/authenticate");
		
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("PUT");
        hurl.setDoOutput(true);                   
        hurl.setRequestProperty("Content-Type", "application/xml"); //application/json
        hurl.setRequestProperty("Accept", "application/xml");
        String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
        		+ "<authenticationRequest><password>"+ _instance.password + "</password><username>" 
        		+ _instance.username + "</username></authenticationRequest>";
        
        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
        osw.write(payload);
        osw.flush();
        osw.close();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
        sessionCode = in.readLine();
        
	}
	
	/**
	 * This methods invokes findPersonsByAttributes API of OpenEMPI and retrieves person details based on family name, given name or date of birth
	 * @param parameters
	 * @return String in XML format: person details 
	 * @throws Exception
	 */
	protected String commonSearchPersonByAttributes(JSONObject parameters) throws Exception{
		
		getSessionCode();
		String familyName = null;
		String givenName =null;
		String dob = null;
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/findPersonsByAttributes");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("POST");
        hurl.setDoOutput(true);
        hurl.setRequestProperty("Content-Type", "application/xml"); 
	    hurl.setRequestProperty("Accept", "application/xml");
        hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
        
        if(parameters.length() == 0)
        	return null;
        
        String payload = "<person>";
        
        if(parameters.has("familyName")){
        	familyName = parameters.getString("familyName");
        	payload = payload + "<familyName>" + familyName + "</familyName>";
        }
        if(parameters.has("givenName")){
        	givenName = parameters.getString("givenName");
        	payload = payload + "<givenName>" + givenName + "</givenName>";
        }
        if(parameters.has("dateOfBirth")){
        	dob = parameters.getString("dateOfBirth");
        	payload = payload + "<dateOfBirth>" + dob +"</dateOfBirth>";
        }
        payload = payload + "</person>";
        
        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
        osw.write(payload);
        osw.flush();
        osw.close();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
        String line;
        String response = "";
        while((line = in.readLine())!=null){
        	response += line;	
        }
        System.out.println("Abstract Class: OpenEMPIbase Method: CommonSerachByAttributes Response:" + response );
        return response;
	}
	
	/**
	 * This methods invokes loadPerson API of OpenEMPI and retrieves person details based on personId
	 * @param parameter
	 * @return String in XML format: person details 
	 * @throws Exception
	 */
	protected String commonReadPerson(String parameter) throws Exception, Exception{
		
		getSessionCode();
		
		int id = Integer.parseInt(parameter);
		
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-query-resource/loadPerson?personId=" + id);
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("GET");
        hurl.setDoOutput(true);
        hurl.setRequestProperty("Content-Type", "application/xml"); 
        hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
       
        BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
        String line;
        String response = "";
        while((line = in.readLine())!=null){
        	response += line;	
        }
        System.out.println("Abstract Class: OpenEMPIbase Method: commonReadPerson Response:" + response );
        return response;
	}
	
	/**
	 * This methods invokes loadPerson API of OpenEMPI and updates person details
	 * @param parameter
	 * @return String in XML format: person details 
	 * @throws Exception
	 */
	protected String commonUpdatePerson(String parameters) throws Exception{
		
		getSessionCode();
		
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/updatePersonById");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("PUT");
        hurl.setDoOutput(true);
        hurl.setRequestProperty("Content-Type", "application/xml");
        hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
        
        if(parameters.isEmpty())
        	return null;
        
        /* -------------- to be updated -----------------*/
        String payload = "<person>";
            
        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
        osw.write(payload);;
        osw.flush();
        osw.close();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
        String line;
        String response = "";
        while((line = in.readLine())!=null){
        	response += line;	
        }
        System.out.println("Abstract Class: OpenEMPIbase Method: commonUpdateById Response:" + response );
        return response;
	}
	
	/**
	 * This method invokes addPerson API of OpenEMPI to create a new patient record
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	protected String commonAddPerson(String parameters) throws Exception{
		
		getSessionCode();
		
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/addPerson");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("PUT");
        hurl.setDoOutput(true);
        hurl.setRequestProperty("Content-Type", "application/xml");
        hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
       
        String payload = parameters;
        /* -------------- to be updated -----------------*/
        //payload = payload + "</person>";
        
        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
        osw.write(payload);;
        osw.flush();
        osw.close();
	String response = "";

        try{ 
        	BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
		String line;
        	response = "";
        	while((line = in.readLine())!=null){
        		response += line;	
        	}
        	System.out.println("Abstract Class: OpenEMPIbase Method: commonAddPerson Response:" + response );
   		return response;	
	}catch (Exception e){
		throw new ResourceNotFoundException("Resource not created");	
	}
	}
	
	/**
	 * This method takes personId and calls the commomReadPerson to retrieves the person details and then calls the deletePersonById API with the 
	 * person details and delete the person form OpenEMPI
	 * @param parameters
	 * @return String: Successful if delete is successful otherwise unsuccessful
	 * @throws Exception
	 */
	protected String commonDeletePersonById(String parameters) throws Exception{
		
		String payload = commonReadPerson(parameters);		
		if(payload == null)
			return null;
		
		getSessionCode();
		URL url = new URL(_instance.baseURL + "openempi-admin/openempi-ws-rest/person-manager-resource/deletePersonById");
		HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
        hurl.setRequestMethod("PUT");
        hurl.setDoOutput(true);
        hurl.setRequestProperty("Content-Type", "text/xml");
        hurl.setRequestProperty("mediaType", "*/*");
        hurl.setRequestProperty("OPENEMPI_SESSION_KEY", sessionCode);
		
        OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
        osw.write(payload);;
        osw.flush();
        osw.close();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(hurl.getInputStream(), "UTF-8"));
        String line;
        String response = "";
        while((line = in.readLine())!=null){
        	response += line;	
        }
        System.out.println("Abstract Class: OpenEMPIbase Method: commonDeletePersonById Response:" + response );
        if (response == "")
        	return "Delete Success";
        return response;
	}
	
	protected String commonPatchPerson(String id){
		
		return null;
	}
	
	
}
