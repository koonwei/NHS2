package fhirconverter.converter; 
import fhirconverter.utilities.PatientHelper; 

import ca.uhn.fhir.model.dstu2.resource.Observation;

import org.json.JSONObject;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObservationFHIR{
	private PatientHelper patientHelper = new PatientHelper();
	private Logger LOGGER = LogManager.getLogger(ObservationFHIR.class);

	protected List<Observation> search(String patientId, String domainName) throws Exception{ 
		String nhsNumber = patientHelper.retrieveNHSbyId(patientId);
		LOGGER.info("nhsNumber" + nhsNumber);
		OpenEHRConnector openEHRconnector = new OpenEHRConnector(domainName); // Future developers, Note this line of code is placed here to be thread safe.
		JSONObject observationObj = openEHRconnector.getGrowthChartObservations(nhsNumber);			
		observationObj.put("patientId", patientId);
		LOGGER.info("observationObj " + observationObj.toString(3));
		OpenEHRConvertor openEHRconvertor = new OpenEHRConvertor();
		return openEHRconvertor.jSONToObservation(observationObj);
	}

}
