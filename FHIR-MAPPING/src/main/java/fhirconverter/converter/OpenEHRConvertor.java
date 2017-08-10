/**
 * 
 */
package fhirconverter.converter;

import java.util.ArrayList;
import java.util.Arrays;
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

import java.util.concurrent.ThreadLocalRandom;
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
		codeMap.put("37362-1", "XR Bone age");
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
	    logger.info(jsonResult.toString(3));	
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
		JSONObject newResultSet = this.prepareResultSet(resultSet);
		Iterator<?> jsonKeys = newResultSet.keys();
		Map<String, Observation> obsMap = new HashMap<>();
		while (jsonKeys.hasNext()) {

			String jsonNode = jsonKeys.next().toString();
			logger.info("jsonNode = " + jsonNode);
			String key = jsonNode.substring(0, jsonNode.lastIndexOf("-"));
			if (obsMap.containsKey(key)) {
				this.setParameters(obsMap, newResultSet, jsonNode, key);
			} else {
				Observation observation = new Observation();
				logger.info("Adding new Observation for key  = " + key);
				observation.getCode().addCoding(new CodingDt("http://loinc.org", key));
				observation.getCode().setText(codeMap.get(key));
				QuantityDt quantity = new QuantityDt();
				quantity.setValue(0.0);
				quantity.setUnit("");
				quantity.setCode("");
				observation.setValue(quantity);
				observation.getSubject().setReference(patientId);
                observation.setId(String.valueOf(ThreadLocalRandom.current().nextInt()));
				obsMap.put(key, observation);
				this.setParameters(obsMap, newResultSet, jsonNode, key);
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

		if (jsonNode.equals(key + "-date")) {
			obsMap.get(key).setEffective(new DateTimeDt(resultSet.optString(jsonNode)));
			logger.info("date of " + key + " = " + resultSet.optString(jsonNode));
		} else if (jsonNode.equals(key + "-magnitude")) {
			String magnitude = resultSet.optString(jsonNode);
			quantity = (QuantityDt) obsMap.get(key).getValue();
			if (magnitude != null && !magnitude.equals("")) {
				quantity.setValue(Double.parseDouble(magnitude));
				logger.info("magitude of " + key + " = " + resultSet.optDouble(jsonNode));
				obsMap.get(key).setValue(quantity);
			}
		} else if (jsonNode.equals(key + "-units")) {
			quantity = (QuantityDt) obsMap.get(key).getValue();
			quantity.setCode(resultSet.optString(jsonNode));
			quantity.setUnit(resultSet.optString(jsonNode));
			logger.info("units of " + key + " = " + resultSet.optString(jsonNode));
		} else if (jsonNode.equals(key + "-value")) {
			String value = resultSet.optString(jsonNode);
			quantity = (QuantityDt) obsMap.get(key).getValue();
			if (value != null && !value.equals("")) {
				quantity.setValue(OpenEHRConvertor.parsePeriodToMonths(value));
				quantity.setUnit("months");
				logger.info("value of " + key + " = " + resultSet.optDouble(jsonNode));
				obsMap.get(key).setValue(quantity);
			}
			logger.info("value of " + key + " = " + resultSet.optString(jsonNode));
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
		logger.info("*** Prepared json *** " + jsonString);
		JSONObject newJSON = new JSONObject(jsonString);
		
		return newJSON;
	}
	
	/**
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	protected JSONObject prepareResultSet(JSONObject json) throws Exception {
		
		String jsonString = json.toString().replaceAll("_", "-");
		logger.info("*** Prepared resultSet *** " + jsonString);
		JSONObject newJSON = new JSONObject(jsonString);
		
		return newJSON;
		
	}
	
	/**
	 * ISO_8601 : As from Wikipedia
	 * P6Y
	 * P[n]Y[n]M[n]DT[n]H[n]M[n]S or P[n]W
	 * 
	 *  P is the duration designator (for period) placed at the start of the duration representation.
	 *	Y is the year designator that follows the value for the number of years.
	 *	M is the month designator that follows the value for the number of months.
	 *	W is the week designator that follows the value for the number of weeks.
	 *	D is the day designator that follows the value for the number of days.
	 *	T is the time designator that precedes the time components of the representation.
	 *	H is the hour designator that follows the value for the number of hours.
	 *	M is the minute designator that follows the value for the number of minutes.
	 *	S is the second designator that follows the value for the number of seconds.
	 *	For example, "P3Y6M4DT12H30M5S" represents a duration of "three years, six months, four days, twelve hours, thirty minutes, and five seconds".
	 * 
	 * This method only parse the period till month, i.e. P[n]Y[n]M anything after M is discarded.
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected static Double parsePeriodToMonths(String value) throws Exception {
		
		String period = "";
		logger.info("value: " + value);
		if(value.substring(0,1).equalsIgnoreCase("P"))
			period = value.substring(1);

		char[] p = period.toCharArray();
		char[] iso = {'Y', 'M'};
		
		Double months = 0.0;
		List<String> duration = new ArrayList<>(Arrays.asList("", ""));
		
		int j = 0;
		for(int i = 0; i < p.length ; i++){
			if(p[i]!= iso[j]){
				duration.set(j, duration.get(j)+p[i]);
			}
			else
				j++;
			if(j > 1)
				break;
		}
		if(duration.get(0) != null && !duration.get(0).equals("")){
			months = (Double.parseDouble(duration.get(0)) * 12);
		}
		if(duration.get(1) != null && !duration.get(1).equals("")){
			months = months + (Double.parseDouble(duration.get(1)));
		}
		logger.info("Months = " + months);
		return months;
	}
	
}
