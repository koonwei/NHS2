package fhirconverter;

public class ConverterOpenempi{




	public String practitionerRead(String id,  Representation format) {


		return "You are calling practitionerRead";
	}
	public String patientRead(String id, Representation format) {




		return "You are calling patientRead";
	}
	public String groupRead(String id, Representation format) {


		return "You are calling grouRead";
	}

	public String practitionerSearch(String parameters, Representation format) {


		return "You are calling practitionerSearch";
	}

	public static String patientSearch(String parameters, Representation format) {

		return "You are calling patientSearch";
	}
	public String groupSearch(String parameters, Representation format) {


		return "You are calling groupSearch";
	}
	public String patientUpdate(String id, Representation format) {

		return "You are calling patientUpdate";
	}
	public String practitionerUpdate(String id, Representation format) {

		return "You are calling practitionerUpdate";
	}
	public String groupUpdate(String id, Representation format) {

		return "You are calling groupUpdate";
	}
	public String patientPatch(String id, Representation format) {

		return "You are calling patientPatch";
	}
	public String groupPatch(String id, Representation format) {

		return "You are calling groupPatch";
	}
	public String practitionerPatch(String id, Representation format) {

		return "You are calling practitionerPatch";
	}
	public static String patientCreate(String format) {

		return "You are calling patientCreate" + format;
	}
	public String practitionerCreate(Representation format) {

		return "You are calling practitionerCreate";
	}
	public String groupCreate(Representation format) {

		return "You are calling groupCreate";
	}
	public String patientDelete(String id) {

		return "You are calling patientDelete";
	}
	public String practitionerDelete(String id) {

		return "You are calling practitionerDelete";
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