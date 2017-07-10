package fhirconverter;
public abstract class OpenEMPIbase{
	private String connection; // connection string? can remove it if not neeeded
	public abstract String convertFHIR();

	public String getOpenEMPI(String connection, String type){
		// connection to openEMPI
		return "HI";
	}

}
