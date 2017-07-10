package fhirconverter;

import fhirconverter.spark.Representation;

public class ConverterOpenempi{

	// Patient

	public String patientRead(String id, Representation format) {
		return "You are calling patientRead";
	}

	public String patientSearch(String parameters, Representation format) {
		return "You are calling patientSearch";
	}

	public String patientUpdate(String id, Representation format) {

		return "You are calling patientUpdate";
	}

	public String patientDelete(String id) {

		return "You are calling patientDelete";
	}

	public String patientPatch(String id, Representation format) {
		return "You are calling patientPatch";
	}

	// Practitioner

	public String practitionerRead(String id,  Representation format) {
		return "You are calling practitionerRead";
	}

	public String practitionerSearch(String parameters, Representation format) {
		return "You are calling practitionerSearch";
	}

	public String practitionerUpdate(String id, Representation format) {
		return "You are calling practitionerUpdate";
	}

	public String practitionerDelete(String id) {
		return "You are calling practitionerDelete";
	}

	// Group

	public String groupSearch(String parameters, Representation format) {
		return "You are calling groupSearch";
	}

	public String groupUpdate(String id, Representation format) {

		return "You are calling groupUpdate";
	}

	public String groupPatch(String id, Representation format) {

		return "You are calling groupPatch";
	}

	public String groupCreate(Representation format) {
		return "You are calling groupCreate";
	}

	public String groupRead(String id, Representation format) {
		return "You are calling grouRead";
	}

	public String groupDelete(String id) {
		return "You are calling groupDelete";
	}





	public static void main(String [] agrs){
		//string type = agrs[0]; // 0 - patient, 1 - practitioner, 2- group

		PropertiesApp properties = new PropertiesApp();
		properties.createProperties();
		int typeTesting = 0; // remove this

		//String connection = agrs[1];
		OpenEMPIbase mapper;
		switch(typeTesting){
			case 0:
				mapper = new PatientFHIR();
				String testing = mapper.convertFHIR();
				System.out.println(testing);
				break;
			case 1:
				mapper = new PractitionerFHIR();
				break;
			case 2:
				mapper = new GroupFHIR();
				break;
			default: break;
		}

	}

}