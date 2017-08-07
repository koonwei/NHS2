/**
 * 
 */
package fhirconverter.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;

/**
 * @author Shruti Sinha
 *
 */
public class OpenEHRConvertor {

	private static final Logger logger = LogManager.getLogger(OpenEHRConvertor.class.getName());

	public Map<String, String> codeMap() {
		Map<String, String> codeMap = new HashMap<>();

		codeMap.put("3141-9", "Weight");
		codeMap.put("8302-2", "Height");
		codeMap.put("39156-5", "BMI");
		codeMap.put("8287-5", "Head circumference");
		return codeMap;
	}

	/**
	 * This method is the accepts the JSON object from EHR system and returns
	 * and equivalent list of Observation objects
	 * 
	 * @param jsonResult
	 * @return List<Observation>
	 * 
	 * @throws Exception
	 */
	public List<Observation> jsonToObservation(JSONObject jsonResult) throws Exception {

		List<Observation> observationList = new ArrayList<>();
		String patientId = "";
		this.prepareInputJSON(jsonResult);
		if (jsonResult.has("patientId")) {
			patientId = jsonResult.optString("patientId");
		}
		/*** Checks if the JSON body contains resutlSet node or not ***/
		if (jsonResult.has("resultSet")) {
			/*
			 * resultSet node is an array, so if resultSet node exist, the
			 * values are stored in resultSetJSONArray and iterated to retrieve
			 * each resulSet element and
			 */
			logger.info("resultSet exists in JSON");
			JSONArray resultSetJSONArray = jsonResult.optJSONArray("resultSet");
			logger.info("Array size" + resultSetJSONArray.length());
			for (int i = 0; i < resultSetJSONArray.length(); i++) {
				JSONObject resultSet = resultSetJSONArray.getJSONObject(i);
				observationList.addAll(mapping(resultSet, patientId));
			}
		}
		logger.info("observationList size: " + observationList.size());
		return observationList;
	}

	/**
	 * 
	 * @param resultSet
	 * @param patientId
	 * @return
	 * 
	 * @throws Exception
	 */
	public List<Observation> mapping(JSONObject resultSet, String patientId) throws Exception {

		Map<String, String> codeMap = this.codeMap();
		Iterator<?> jsonKeys = resultSet.keys();
		Map<String, Observation> obsMap = new HashMap<>();
		while (jsonKeys.hasNext()) {

			String jsonNode = jsonKeys.next().toString();
			logger.info("jsonNode = " + jsonNode);
			jsonNode = jsonNode.replaceFirst("_", "-");
			String key = jsonNode.substring(0, jsonNode.indexOf("_"));
			if (obsMap.containsKey(key)) {
				this.setParameters(obsMap, resultSet, jsonNode, key);
			} else {
				Observation observation = new Observation();
				logger.info("Adding new Observation for key  = " + key);
				observation.getCode().addCoding(new CodingDt("http://loinc.org", key));
				observation.getCode().setText(codeMap.get(key));
				QuantityDt quantity = new QuantityDt();
				quantity.setValue(0.0);
				quantity.setUnit("");
				quantity.setCode("");
				quantity.setSystem("http://unitsofmeasure.org");
				observation.setValue(quantity);
				observation.getSubject().setReference(patientId);
				obsMap.put(key, observation);
				this.setParameters(obsMap, resultSet, jsonNode, key);
			}
		}
		return this.getObservationList(obsMap);
	}

	/**
	 * 
	 * @param obsMap
	 * @param json
	 * @param newjson
	 * @param key
	 * 
	 * @throws Exception
	 */
	protected void setParameters(Map<String, Observation> obsMap, JSONObject resultSet, String jsonNode, String key)
			throws Exception {

		QuantityDt quantity = new QuantityDt();

		if (jsonNode.equals(key + "_date")) {
			obsMap.get(key).setEffective(new DateTimeDt(resultSet.optString(jsonNode)));
			logger.info("date of " + key + " = " + resultSet.optString(jsonNode));
		} else if (jsonNode.equals(key + "_magnitude")) {
			String magnitude = resultSet.optString(jsonNode);
			quantity = (QuantityDt) obsMap.get(key).getValue();
			if (magnitude != null && !magnitude.equals("")) {
				quantity.setValue(Double.parseDouble(magnitude));
				logger.info("magitude of " + key + " = " + resultSet.optDouble(jsonNode));
				obsMap.get(key).setValue(quantity);
			}
		} else if (jsonNode.equals(key + "_units")) {
			quantity = (QuantityDt) obsMap.get(key).getValue();
			quantity.setCode(resultSet.optString(jsonNode));
			quantity.setUnit(resultSet.optString(jsonNode));
			logger.info("units of " + key + " = " + resultSet.optString(jsonNode));
		}
	}

	/**
	 * 
	 * @param obsMap
	 * @return
	 * @throws Exception
	 */
	protected List<Observation> getObservationList(Map<String, Observation> obsMap) throws Exception {

		List<Observation> observationList = new ArrayList<>();
		logger.info("*** Observations Map readings : ***");
		for (Map.Entry<String, Observation> obs : obsMap.entrySet()) {
			observationList.add(obs.getValue());
			logger.info("Code: " + obs.getValue().getCode().getCoding().get(0).getCode());
			logger.info("Text: " + obs.getValue().getCode().getText());
			QuantityDt q = (QuantityDt) obs.getValue().getValue();
			logger.info("Magnitude: " + q.getValue());
			logger.info("Unit Code: " + q.getCode());
			logger.info("Unit: " + q.getUnit());
			logger.info("System: " + q.getSystem());
			logger.info("Date: " + obs.getValue().getEffective());
			logger.info("---------------------");
		}
		return observationList;
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	protected JSONObject prepareInputJSON(JSONObject json) throws Exception {

		String jsonString = json.toString().replaceAll("LONIC_", "");
		
		logger.info("*** Prepared json *** /n" + jsonString);
		JSONObject j = new JSONObject(jsonString);
		return j;
	}
}
