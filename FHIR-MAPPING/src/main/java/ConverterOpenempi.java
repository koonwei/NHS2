package fhirconverter;

public class ConverterOpenempi{




	public String practitionerRead(String id,  Representation format) {


		return "";
	}
	public String patientRead(String id, Representation format) {




		return "";
	}
	public String groupRead(String id, Representation format) {


		return "";
	}

	public String practitionerSearch(String parameters, Representation format) {


		return "";
	}

	public String patientSearch(String parameters, Representation format) {

		return "";
	}
	public String groupSearch(String parameters, Representation format) {


		return "";
	}
	public String patientUpdate(String id, Representation format) {

		return "";
	}
	public String practitionerUpdate(String id, Representation format) {

		return "";
	}
	public String groupUpdate(String id, Representation format) {

		return "";
	}
	public String patientPatch(String id, Representation format) {

		return "";
	}
	public String groupPatch(String id, Representation format) {

		return "";
	}
	public String practitionerPatch(String id, Representation format) {

		return "";
	}
	public String patientCreate(Representation format) {

		return "";
	}
	public String practitionerCreate(Representation format) {

		return "";
	}
	public String groupCreate(Representation format) {

		return "";
	}
	public String patientDelete(String id) {

		return "";
	}
	public String practitionerDelete(String id) {

		return "";
	}
	public String groupDelete(String id) {

		return "";
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