package fhirconverter.spark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Filters {
    private static Logger LOGGER = LogManager.getLogger(Filters.class);

//    public static Filter formatFilter = (Request req, Response response) ->
//    {
//        String request_format_raw = req.contentType();
//        Representation request_format = Representation.fromString(request_format_raw);
//        req.attribute("request_format", request_format);
//
//        String reply_format_raw = req.queryParams("_format");
//        Representation reply_format = Representation.fromString(reply_format_raw);
//        if (reply_format == Representation.UNKNOWN)
//            reply_format = Representation.JSON;
//        req.attribute("reply_format", reply_format);
//
//        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);
//    };
//
//    public static Filter responseFormatValidater = (Request req, Response response) ->
//    {
//        if (req.attribute("reply_format") == Representation.XML)
//        {
//            //TODO Check format of response
//            response.type("application/fhir+xml");
//        }
//        else
//        {
//            //TODO Check format of response
//            response.type("application/fhir+json");
//        }
//
//    };
}
