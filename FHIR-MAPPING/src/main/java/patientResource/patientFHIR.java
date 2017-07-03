import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;

import java.io.IOException;

public class patientFHIR {

	public static void main(String[] agrs) throws IOException {
        	FhirContext ctx = FhirContext.forDstu3();

        	Patient patient = new Patient();

        // you can use the Fluent API to chain calls
        // see http://hapifhir.io/doc_fhirobjects.html
        	patient.addName().setUse(HumanName.NameUse.OFFICIAL).addPrefix("Mr").addGiven("Sam"); //Removed addFamily. Causes error.
        	patient.addIdentifier().setSystem("http://ns.electronichealth.net.au/id/hi/ihi/1.0").setValue("8003608166690503");
        	System.out.println("Serialise Resource to the console to JSON.");
        // create a new XML parser and serialize our Patient object with it
        	String encoded = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        	System.out.println(encoded);
	}	


}


