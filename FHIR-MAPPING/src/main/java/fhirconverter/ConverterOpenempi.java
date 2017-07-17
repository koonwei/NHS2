package fhirconverter;

import com.github.fge.jsonpatch.JsonPatch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fhirconverter.spark.Representation;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;

public class ConverterOpenempi{
	OpenEMPIbase mapper;
  
  	public ConverterOpenempi(){
		mapper = new PatientFHIR();	
	}

	// Patient

	public String patientCreate(JSONObject params, Representation format) {
		JSONObject response_raw = new JSONObject().put("message","Created Patient ");
		try {
			response_raw.put("entry", mapper.create(params));
		} catch (Exception e) {
			response_raw.put("error", e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw) + "This is the patient received: " + params.toString();
		return response_raw.toString();
	}

	public String patientRead(String id, Representation format) {
		JSONObject response_raw = new JSONObject().put("message","Read Patient " + id);
		try {
			response_raw.put("entry", mapper.read(id));
		} catch (Exception e) {
			response_raw.put("error", e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientSearch(JSONObject params, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Search Patient ");
		try {
			response_raw.put("entry", mapper.search(params));
		} catch (Exception e) {
			response_raw.put("error", e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientUpdate(String id, JSONObject params, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Update Patient " + id);
		try{
			response_raw.put("entry", mapper.update(id));
		}catch (Exception e){
			response_raw.put("error",e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientDelete(String id, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Delete Patient " + id);
		try {
			response_raw.put("entry", mapper.delete(id));
		} catch (Exception e) {
			response_raw.put("error", e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();

	}

	public String patientPatch(String id, JsonPatch patch, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Patch Patient " + id);
		try {
			response_raw.put("entry", mapper.patch(id, patch));
		} catch (Exception e) {
			response_raw.put("error", e);
		}
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

}
