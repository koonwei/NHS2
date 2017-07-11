package fhirconverter.spark;

import spark.*;

public class Filters {
    public static Filter requestFormatFilter = (Request req, Response response) ->
    {
        String format_raw = req.queryParams("_format");
        Representation format;

        if(format_raw == null)
            format = Representation.JSON;
        else
            format = Representation.fromString(format_raw);

        req.attribute("format", format);
    };

    public static Filter responseFormatValidater = (Request req, Response response) ->
    {
        if (req.attribute("format") == Representation.XML)
        {
            response.type("application/fhir+xml");
        }
        else
        {
            response.type("application/fhir+json");
        }

    };
}
