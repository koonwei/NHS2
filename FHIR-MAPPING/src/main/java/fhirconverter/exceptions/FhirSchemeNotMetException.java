/**
 * 
 */
package fhirconverter.exceptions;

/**
 * @author Shruti Sinha
 *
 */
public class FhirSchemeNotMetException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 67879777;
    
    //private String message;
     
    public FhirSchemeNotMetException(String message) {
	 super(message);
	 System.out.println(message);
    }
}

