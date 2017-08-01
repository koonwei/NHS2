package fhirconverter.converter;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObservationFHIRTests{
 	private	Logger LOGGER = LogManager.getLogger(ObservationFHIRTests.class);
	@Test
	public void getObservationIntegrationTest() throws Exception{
		ObservationFHIR observationFHIR = new ObservationFHIR();
		List<Observation> observations = observationFHIR.search("3185", "openEhrApi");
		FhirContext ctx = FhirContext.forDstu2();
		for(Observation observation : observations){
			String resourceJson = ctx.newJsonParser().encodeResourceToString(observation);	
			JSONObject obtained_object = new JSONObject(resourceJson);	
			LOGGER.info(obtained_object.toString(3));	
			assertNotNull(observation);
		}
	}
}







