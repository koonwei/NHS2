package fhirconverter.spark;

import spark.*;

public class Filters {
    public static Filter formatFilter = (Request req, Response response) ->
    {

        String reply_format_raw = req.queryParams("_format");
        req.attribute("reply_format", Representation.fromString(reply_format_raw));

        String request_format_raw = req.contentType();
        req.attribute("request_format", Representation.fromString(request_format_raw));
    };

    public static Filter responseFormatValidater = (Request req, Response response) ->
    {
        if (req.attribute("reply_format") == Representation.XML)
        {
            //TODO Check format of response
            response.type("application/fhir+xml");
        }
        else
        {
            //TODO Check format of response
            response.type("application/fhir+json");
        }

    };
}
