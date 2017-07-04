import java.util.Scanner;

public class ConverterOpenempi{
	public static void main(String [] agrs){
		//string type = agrs[0]; // 0 - patient, 1 - practitioner, 2- group
		
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
