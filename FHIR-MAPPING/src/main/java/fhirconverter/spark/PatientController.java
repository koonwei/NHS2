package fhirconverter.spark;

import ca.uhn.fhir.parser.DataFormatException;
import fhirconverter.ConverterOpenempi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Patient;
import org.json.JSONObject;
import org.json.XML;
import spark.*;

import static spark.Spark.stop;

public class PatientController {
    private static Logger LOGGER = LogManager.getLogger(Patient.class);

    private static ResourceParser resourceParser = new ResourceParser(Patient.class);

    private static JSONObject parseResource(String data, Representation format) throws DataFormatException, ClassCastException
    {
        if (format == Representation.JSON)
            return  resourceParser.parseJSON(data);
        else if (format == Representation.XML)
            return resourceParser.parseXML(data);
        else
        {
            LOGGER.fatal("Impossible value for representation!!!");
            stop();
            return null;
        }
    }

    private static String generateError(String errorMessage, Representation format)
    {
        JSONObject response_raw = new JSONObject().put("error", errorMessage);
        if(format == Representation.XML)
            return XML.toString(response_raw);
        return response_raw.toString();
    }

    public static Route createPatient = (request, response) -> {
        Representation format = request.attribute("format");
        String id = request.queryParams("id");
        LOGGER.debug("Format: " + format);

        JSONObject patient = null;
        try {
            parseResource(request.body(), format);
        }
        catch (DataFormatException e)
        {
            response.body(generateError("Invalid Value", format));
            LOGGER.info("Invalid Parameter Received", e);
            return response;
        }
        catch (ClassCastException e)
        {
            response.body(generateError("Incompatible Type", format));
            LOGGER.info("Incompatible Type Received", e);
            return response;
        }

        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientCreate(id, body, format));
        return response;
    };

    public static Route searchPatientByGet = (request, response) -> {
        Representation format = request.attribute("format");
        LOGGER.debug("Body: " + request.body());
        response.body(new ConverterOpenempi().patientSearch(null, format));

        return response;
    };

    public static Route searchPatientByPost = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientSearch(body, format));

        return response;
    };

    public static Route readPatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientRead(request.params("id"), format));
        return response;
    };

    public static Route updatePatient = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientUpdate(request.params("id"), body, format));
        return response;
    };

    public static Route patchPatient = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientPatch(request.params("id"), body, format));
        return response;
    };

    public static Route deletePatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientDelete(request.params("id"), format));
        return response;
    };
}
