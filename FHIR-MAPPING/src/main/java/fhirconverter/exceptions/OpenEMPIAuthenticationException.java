/**
 * 
 */
package fhirconverter.exceptions;

/**
 * @author Shruti Sinha
 *
 */
public class OpenEMPIAuthenticationException extends ConversionException{

	/**
     * 
     */
    private static final long serialVersionUID = 67879776;
    
    private String message;
     
    public OpenEMPIAuthenticationException(String message) {
	 super(message);
    }
}
