package fhirconverter;

import fhirconverter.spark.Representation;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;

public class ConverterOpenempi{

	// Patient

	public String patientCreate(String id, JSONObject params, Representation format) {
		JSONObject response_raw = new JSONObject().put("message","Created Patient " + id);
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientRead(String id, Representation format) {
		JSONObject response_raw = new JSONObject().put("message","Read Patient " + id);
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientSearch(JSONObject params, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Search Patient ");
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientUpdate(String id, JSONObject params, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Update Patient " + id);
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	public String patientDelete(String id, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Delete Patient " + id);
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();

	}

	public String patientPatch(String id, JSONObject params, Representation format) {
		JSONObject response_raw =  new JSONObject().put("message","Patch Patient " + id);
		if(format == Representation.XML)
			return XML.toString(response_raw);
		return response_raw.toString();
	}

	///////////////////////////
	// Practitioner
	//////////////////////////

//	public String practitionerRead(String id,  Representation format) {
//		return "You are calling practitionerRead";
//	}
//
//	public String practitionerSearch(String parameters, Representation format) {
//		return "You are calling practitionerSearch";
//	}
//
//	public String practitionerUpdate(String id, Representation format) {
//		return "You are calling practitionerUpdate";
//	}
//
//	public String practitionerDelete(String id) {
//		return "You are calling practitionerDelete";
//	}
//
//	// Group
//
//	public String groupSearch(String parameters, Representation format) {
//		return "You are calling groupSearch";
//	}
//
//	public String groupUpdate(String id, Representation format) {
//
//		return "You are calling groupUpdate";
//	}
//
//	public String groupPatch(String id, Representation format) {
//
//		return "You are calling groupPatch";
//	}
//
//	public String groupCreate(Representation format) {
//		return "You are calling groupCreate";
//	}
//
//	public String groupRead(String id, Representation format) {
//		return "You are calling grouRead";
//	}
//
//	public String groupDelete(String id) {
//		return "You are calling groupDelete";
//	}
}