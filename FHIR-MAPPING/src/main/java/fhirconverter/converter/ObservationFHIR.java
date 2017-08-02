package fhirconverter.converter; 
import fhirconverter.utilities.PatientHelper; 

import ca.uhn.fhir.model.dstu2.resource.Observation;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObservationFHIR{
	private PatientHelper patientHelper = new PatientHelper();
	private Logger LOGGER = LogManager.getLogger(ObservationFHIR.class);

	protected List<Observation> search(String patientId, ArrayList<String> searchParams) throws Exception{ 
		String nhsNumber = patientHelper.retrieveNHSbyId(patientId);
		String domainName = "openEhrApi";
		LOGGER.info("nhsNumber" + nhsNumber);
		OpenEHRConnector openEHRconnector = new OpenEHRConnector(domainName); // Future developers, Note this line of code is placed here to be thread safe.		
		org.json.simple.JSONObject aqlPaths = Utils.readJsonFile();
		JSONObject aqlJSONObj =  new JSONObject(aqlPaths.toString());
		LOGGER.info(aqlJSONObj.toString(3));
		String ehrNumber = openEHRconnector.getEHRIdByNhsNumber(nhsNumber);		
		JSONObject aqlFilteredObj = filterPathsByParams(aqlJSONObj, searchParams);
		String aqlQuery = constructDynamicAQLquery(ehrNumber,aqlFilteredObj, searchParams);
		JSONObject observationObj = openEHRconnector.getObservations(aqlQuery);
		LOGGER.info(observationObj.toString(3));
	//	observationObj.put("patientId", patientId);
	//	LOGGER.info("observationObj " + observationObj.toString(3));
		OpenEHRConvertor openEHRconvertor = new OpenEHRConvertor();
	//	return openEHRconvertor.jSONToObservation(observationObj);
		return null;
	}
	protected String constructDynamicAQLquery(String ehrNumber, JSONObject aqlFilteredObj, ArrayList<String> searchParams){
		String selectString = "select";
		String fromString = " from EHR [ehr_id/value='"+ehrNumber+"'] contains COMPOSITION c" ;
		String containmentString = " contains (";
		Iterator<String> iter = aqlFilteredObj.keys();
		while (iter.hasNext()) {
    			String key = iter.next();
    			try {
				JSONObject pathObj = aqlFilteredObj.getJSONObject(key).getJSONObject("path");
				String archetypeIdentifier = aqlFilteredObj.getJSONObject(key).getString("text");
				String archetypeString = aqlFilteredObj.getJSONObject(key).getString("archetype");
				selectString += constructSelectStatement(key,pathObj, archetypeIdentifier);
				containmentString +=  constructContainmentStatement(archetypeIdentifier, archetypeString);
    			} catch (Exception e) {
        			e.printStackTrace();
   				}
		}
		selectString = selectString.substring(0, selectString.length() - 1);
		containmentString = containmentString.substring(0, containmentString.length() - 2);
		containmentString += ")";
		String constructedAQLString = selectString + fromString + containmentString;
		LOGGER.info("Select statement "+ constructedAQLString); 
		return constructedAQLString;
			
	}
	protected JSONObject filterPathsByParams(JSONObject aqlPaths, ArrayList<String> searchParams){
		JSONObject filteredPath = new JSONObject();
		for(String searchParam : searchParams){
			if(aqlPaths.has(searchParam)){
				filteredPath.put(searchParam, aqlPaths.getJSONObject(searchParam));
			}
		}
		LOGGER.info(filteredPath.toString(3));
		return filteredPath;
	}
	protected String constructSelectStatement(String aqlFilteredKey, JSONObject pathObj, String archetypeIdentifier){
		Set keys = pathObj.keySet();
		String selectString = "";
   		Iterator a = keys.iterator();
    		while(a.hasNext()) {
			String key = (String)a.next();
        		String value = (String)pathObj.get(key);
        		System.out.print("key : "+key);
        		System.out.println(" value :"+value);
			selectString += " "+archetypeIdentifier+"/"+value+ " as " + "LONIC_"+archetypeIdentifier+"_"+key+",";		
		//	selectString += " "+archetypeIdentifier+"/"+value+ " as " + "LONIC_"+aqlFilteredKey+"_"+key+","; Ask Shruti tml :d
		}
		return selectString;		
	}
	protected String constructContainmentStatement(String archetypeIdentifier, String archetypeString){
		String containmentStatementString = " OBSERVATION "+archetypeIdentifier+"["+archetypeString+"] or";
		return containmentStatementString;
	}
}
