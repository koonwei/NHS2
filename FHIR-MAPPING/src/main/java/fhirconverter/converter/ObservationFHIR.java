package fhirconverter.converter; 
import fhirconverter.utilities.PatientHelper; 

import ca.uhn.fhir.model.dstu2.resource.Observation;

import org.json.JSONObject;
import java.util.List;
public class ObservationFHIR{
	PatientHelper patientHelper = new PatientHelper();

	protected List<Observation> search(String patientId, String domainName) throws Exception{ 
		String nhsNumber = patientHelper.retrieveNHSbyId(patientId);
		OpenEHRConnector openEHRconnector = new OpenEHRConnector(domainName); // Future developers, Note this line of code is placed here to be thread safe.
		JSONObject observationObj = openEHRconnector.getGrowthChartObservations(domainName);			
		//Add Shruti's call for mapper!
		return null;
		
	}

}
