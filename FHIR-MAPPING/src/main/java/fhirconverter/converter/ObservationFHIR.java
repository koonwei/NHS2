package fhirconverter.converter; 
import fhirconverter.utilites.PatientHelper; 

import ca.uhn.fhir.model.dstu2.resource.Observation;

import org.json.JSONObject;

public class ObservationFHIR{
	PatientHelper patientHelper = new PatientHelper();
	protected List<Observation> search(String patientId) throws Exception{
		String nhsNumber = patientHelper.retrieveNHSbyId(patientId);
		OpenEHRConnector openEHRconnector = new OpenEHRConnector(); // Future developers, Note this line of code is placed here to be thread safe.
		JSONObject observationObj = openEHRconnector.getGrowthChartObservations();			
		//Add Shruti's call for mapper!
		
	}

}
