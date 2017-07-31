/**
 * 
 */
package fhirconverter.converter;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * This method is the accepts the JSON object from EHR system and returns
	 * and equivalent list of Observation objects
	 * 
	 * @param jsonResult
	 * @return List<Observation>
	 * 
	 * @throws Exception
	 */
	public List<Observation> JSONToObservation(JSONObject jsonResult) throws Exception {

		List<Observation> observationList = new ArrayList<>();
		String patientId = "";
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
				observationList.addAll(observationMapping(resultSet, patientId));
			}
		}
		logger.info("observationList size" + observationList.size());
		return observationList;
	}

	/**
	 * This method maps the elements of JSON object into different Observation
	 * object based on observation type like Weight, Height and BMI. Finally
	 * adds all the Observation object to the observationList
	 * 
	 * @param resultSet
	 * @param patientId
	 * @return List<Observation>
	 * 
	 * @throws Exception
	 */
	protected List<Observation> observationMapping(JSONObject resultSet, String patientId) throws Exception {
		List<Observation> observationList = new ArrayList<>();

		observationList.addAll(createHeight(resultSet, patientId));
		observationList.addAll(createWeight(resultSet, patientId));
		observationList.addAll(createBMI(resultSet, patientId));

		/****** Head Circumference: Not required right now *******/

		/*
		 * if (resultSet.has("Head_circumference_units")) {
		 * 
		 * } if (resultSet.has("Head_circumference_magnitude")) {
		 * 
		 * } if (resultSet.has("Skeletal_age_date")) {
		 * 
		 * } if (resultSet.has("Skeletal_age")) {
		 * 
		 * } if (resultSet.has("Head_circumference_date")) {
		 * 
		 * }
		 */

		return observationList;
	}

	/**
	 * 
	 * @param resultSet
	 * @param patientId
	 * @return List<Observation>
	 * @throws Exception
	 */
	protected List<Observation> createWeight(JSONObject resultSet, String patientId) throws Exception {
		/****** Weight *******/
		List<Observation> observationList = new ArrayList<>();
		Observation observationWeight = new Observation();
		QuantityDt quantityWeight = new QuantityDt();

		if (resultSet.has("Weight_magnitude")) {
			observationWeight.getCode().addCoding(new CodingDt("http://loinc.org", "3141-9"));
			observationWeight.getCode().setText("weight");
			String value = resultSet.optString("Weight_magnitude");
			if (!value.equals(null) && !value.equals(""))
				quantityWeight.setValue(Double.parseDouble(value));
		}
		if (resultSet.has("Weight_units")) {
			quantityWeight.setUnit(resultSet.optString("Weight_units"));
			quantityWeight.setCode(resultSet.optString("Weight_units"));
			quantityWeight.setSystem("http://unitsofmeasure.org");
		}

		if (resultSet.has("Weight_date")) {
			observationWeight.setEffective(new DateTimeDt(resultSet.optString("Weight_date")));
		}
		/*** set common attributes for Weight ***/
		setCommonAttributes(observationList, observationWeight, quantityWeight, patientId);
		return observationList;
	}

	/**
	 * 
	 * @param resultSet
	 * @param patientId
	 * @return List<Observation>
	 * @throws Exception
	 */
	protected List<Observation> createHeight(JSONObject resultSet, String patientId) throws Exception {

		/****** Height *******/
		List<Observation> observationList = new ArrayList<>();
		Observation observationHeight = new Observation();
		QuantityDt quantityHeight = new QuantityDt();

		if (resultSet.has("Height_Length_magnitude")) {
			observationHeight.getCode().addCoding(new CodingDt("http://loinc.org", "8302-2"));
			observationHeight.getCode().setText("height");
			String value = resultSet.optString("Height_Length_magnitude");
			if (!value.equals(null) && !value.equals(""))
				quantityHeight.setValue(Double.parseDouble(value));
		}

		if (resultSet.has("Height_Length_units")) {
			quantityHeight.setUnit(resultSet.optString("Height_Length_units"));
			quantityHeight.setCode(resultSet.optString("Height_Length_units"));
			quantityHeight.setSystem("http://unitsofmeasure.org");
		}

		if (resultSet.has("Height_Length_date")) {
			observationHeight.setEffective(new DateTimeDt(resultSet.optString("Height_Length_date")));
		}

		/*** set common attributes for Height ***/
		setCommonAttributes(observationList, observationHeight, quantityHeight, patientId);
		return observationList;
	}

	/**
	 * 
	 * @param resultSet
	 * @param patientId
	 * @return List<Observation>
	 * @throws Exception
	 */
	protected List<Observation> createBMI(JSONObject resultSet, String patientId) throws Exception {

		List<Observation> observationList = new ArrayList<>();
		Observation observationBMI = new Observation();
		QuantityDt quantityBMI = new QuantityDt();
		/****** BMI *******/

		if (resultSet.has("Body_Mass_Index_magnitude")) {
			observationBMI.getCode().addCoding(new CodingDt("http://loinc.org", "39156-5"));
			observationBMI.getCode().setText("bmi");
			String value = resultSet.optString("Body_Mass_Index_magnitude");
			if (!value.equals(null) && !value.equals(""))
				quantityBMI.setValue(Double.parseDouble(value));
		}

		if (resultSet.has("Body_Mass_Index_units")) {
			quantityBMI.setUnit(resultSet.optString("Body_Mass_Index_units"));
			quantityBMI.setCode(resultSet.optString("Body_Mass_Index_units"));
			quantityBMI.setSystem("http://unitsofmeasure.org");
		}

		if (resultSet.has("Body_Mass_index_date")) {
			observationBMI.setEffective(new DateTimeDt(resultSet.optString("Body_Mass_index_date")));
		}

		/*** set common attributes for BMI ***/
		setCommonAttributes(observationList, observationBMI, quantityBMI, patientId);

		return observationList;
	}

	/**
	 * This methods do common checks and sets common attribute in Observation
	 * and then adds Observation to observationList
	 * 
	 * @param observationList
	 * @param observation
	 * @param quantity
	 * @param patientId
	 * @return List<Observation>
	 * 
	 * @throws Exception
	 */
	protected List<Observation> setCommonAttributes(List<Observation> observationList, Observation observation,
			QuantityDt quantity, String patientId) throws Exception {

		if (!observation.isEmpty()) {
			if (!quantity.isEmpty())
				observation.setValue(quantity);
			observation.getSubject().setReference(patientId);
			observationList.add(observation);
		}
		return observationList;
	}
}
