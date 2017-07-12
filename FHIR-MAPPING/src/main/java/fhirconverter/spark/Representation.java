package fhirconverter.spark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum Representation{
    XML,
    JSON;

    private static Logger LOGGER = LogManager.getLogger(Representation.class);

    private static final String XML_STRINGS_REGEX = "xml|text/xml|application/xml|application/fhir xml";

    public static Representation fromString(String repr_string) {

        LOGGER.debug("Format Received: " + repr_string);

        if (repr_string == null) {
            return Representation.JSON;
        }
        if (repr_string.matches(XML_STRINGS_REGEX)){
            return Representation.XML;
        }

        return Representation.JSON;
    }
}

