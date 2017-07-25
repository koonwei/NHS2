/**
 * 
 */
package fhirconverter.converter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Shruti Sinha
 *
 */
public class OpenEMPISession {

	public String baseURL;
	public String username;
	public String password;
	
	/**
	 * The static method reads config.properties file and initialises the common properties for invoking OpenEMPI 
	 * like base URL, user name and password 
	 * @return
	 */
	public static OpenEMPISession initialize(){
		OpenEMPISession newInstance = new OpenEMPISession();
		
		try {
			Properties properties = new Properties();		
			FileReader reader = new FileReader("config.properties");
			properties.load(reader);
			newInstance.baseURL = properties.getProperty("OpenEMPI-baseURL");
			newInstance.username = properties.getProperty("OpenEMPI-username");
			newInstance.password = properties.getProperty("OpenEMPI-password");
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return newInstance;
	}
	

}
