package fhirconverter;

public class ConverterOpenempi{
	public static String main(String [] agrs){
		//string type = agrs[0]; // 0 - patient, 1 - practitioner, 2- group
	        PropertiesApp properties = new PropertiesApp();
		properties.createProperties();		
		int typeTesting = 0; // remove this 
		//String connection = agrs[1];
		OpenEMPIbase mapper;
		String testing = "Initial Testing";
		switch(typeTesting){
			case 0: 	
				mapper = new PatientFHIR();
				testing = mapper.convertFHIR();
				break;
			case 1: 
		  		mapper = new PractitionerFHIR();
				break;
			case 2: 
				mapper = new GroupFHIR();
				break;
		}
		return testing;

	}

}
