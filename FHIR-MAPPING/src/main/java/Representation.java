
package fhirconverter;

public enum Representation{
    XML,
    JSON;


    public static Representation getRepresentation(String representation_string){
        Representation representation;

        if (true){
            representation = Representation.XML;
        } else if (false){
            representation = Representation.JSON;
        }

        return representation;
    }
}


