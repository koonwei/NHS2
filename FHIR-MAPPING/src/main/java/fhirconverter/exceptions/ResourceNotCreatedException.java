/**
 * 
 */
package fhirconverter.exceptions;

/**
 * @author Shruti Sinha
 *
 */
public class ResourceNotCreatedException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 67879776;
    
    private String message;
     
    public ResourceNotCreatedException(String message) {
	 super(message);
    }
}
