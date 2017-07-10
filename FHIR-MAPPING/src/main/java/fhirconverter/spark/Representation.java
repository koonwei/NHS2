
package fhirconverter.spark;

public enum Representation{
    XML,
    JSON,
    UNKNOWN;

    private static final String XML_STRINGS_REGEX = "xml|text/xml|application/xml|application/fhir xml99*";
    private static final String JSON_STRINGS_REGEX = "json|text/json|application/json|application/fhir json";

    public static Representation fromString(String repr_string){
        Representation repr = UNKNOWN;
        System.out.println(repr_string);
        System.out.println(XML_STRINGS_REGEX);
        if (repr_string.matches(XML_STRINGS_REGEX)){
            repr = Representation.XML;
        } else if (repr_string.matches(JSON_STRINGS_REGEX)){
            repr = Representation.JSON;
        }

        return repr;
    }
}

