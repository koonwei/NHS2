package fhirconverter.converter;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import com.github.fge.jsonpatch.JsonPatch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.List;

public class ConverterOpenempi{

	PatientFHIR mapper = new PatientFHIR();

    	private Logger LOGGER = LogManager.getLogger(ConverterOpenempi.class);
 	// Patient
	public String patientCreate(JSONObject params) throws Exception {
		return mapper.create(params);
	}

	public Patient patientRead(String id) throws Exception {
		return  mapper.read(id);
	}

	public List<Patient> patientSearch(JSONObject params) throws Exception {
        LOGGER.info(params);
		return mapper.search(params);
	}
	public String patientUpdate(String id, JSONObject params) throws Exception {
  	    // TODO: Accept params for update
		return  mapper.update(id, params);
	}

	public String patientDelete(String id) throws Exception {
		return mapper.delete(id);
	}

	public String patientPatch(String id, JsonPatch patch) throws Exception {
		return mapper.patch(id, patch);
	}
}
