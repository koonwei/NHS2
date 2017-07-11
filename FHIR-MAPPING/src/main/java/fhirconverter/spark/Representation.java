package fhirconverter.spark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Representation{
    XML,
    JSON;

    private static Logger LOGGER = LogManager.getLogger(Representation.class);

    private static final String XML_STRINGS_REGEX = "xml|text/xml|application/xml|application/fhir xml";
//    private static final String JSON_STRINGS_REGEX = "json|text/json|application/json|application/fhir json";

    public static Representation fromString(String repr_string) {
        Representation repr = JSON;
        LOGGER.debug("Format Received: " + repr_string);

        if (repr_string.matches(XML_STRINGS_REGEX)){
            repr = Representation.XML;
        }

        return repr;
    }
}

