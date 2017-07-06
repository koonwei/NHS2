package fhirconverter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


public class PropertiesApp{

	public void createProperties() {

		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream("config.properties");

			// set the properties value
			prop.setProperty("OpenEMPI-baseURL", "http://localhost:8000/openempi-admin");
			prop.setProperty("OpenEMPI-username", "admin");
			prop.setProperty("OpenEMPI-password", "admin");
		        prop.setProperty("MAPPER","mapper");	
			// save properties to project root folder
			prop.store(output, "config");

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}	

		}	
  	}

}

