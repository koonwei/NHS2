package fhirconverter.exceptions;

/*
 * @author Shruti Sinha
 *
 */
public class ConversionException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 6787973;
    
    private String message;
    
    public ConversionException(String message) {
           super(message);
        }
    
}
