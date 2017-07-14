package fhirconverter.spark;

import ca.uhn.fhir.parser.DataFormatException;
import com.github.fge.jsonpatch.JsonPatch;
import fhirconverter.ConverterOpenempi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Patient;
import org.json.JSONObject;
import org.json.XML;
import spark.*;

import java.io.IOException;
import java.util.Map;

import static spark.Spark.stop;

public class PatientController {
    private static Logger LOGGER = LogManager.getLogger(Patient.class);

    private static FHIRParser parser = new FHIRParser(Patient.class);

    private static ConverterOpenempi converterOpenempi = new ConverterOpenempi();

    private static JSONObject parseResource(String data, Representation format) throws DataFormatException, ClassCastException
    {
        if (format == Representation.JSON)
            return  parser.parseJSONResource(data);
        else if (format == Representation.XML)
            return parser.parseXMLResource(data);
        else
        {
            LOGGER.fatal("Impossible value for representation!!!");
            stop();
            return null;
        }
    }

    private static JsonPatch parsePatch(String data, Representation format) throws IOException
    {
        if (format == Representation.JSON)
            return  parser.parseJSONPatch(data);
        else if (format == Representation.XML)
            throw new DataFormatException();
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
        return jsonToString(response_raw, format);
    }

    private static String jsonToString(JSONObject jsonObject, Representation format)
    {
		if(format == Representation.XML)
			return XML.toString(jsonObject);
        return jsonObject.toString();
    }

    ///////////////////////
    //  Routing
    ///////////////////////

    public static Route createPatient = (request, response) -> {
        Representation request_format = request.attribute("request_format");
        Representation reply_format = request.attribute("reply_format");

        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);

        try {
            JSONObject resource = parseResource(request.body(), request_format);
            String reply = converterOpenempi.patientCreate(resource);
            JSONObject response_raw = new JSONObject().put("message", "Patient " + reply + " Created");
            response.body(jsonToString(response_raw, reply_format));
        }
        catch (DataFormatException | ClassCastException e)
        {
            response.body(generateError("Invalid Value", reply_format));
            response.status(400);
            LOGGER.info("Invalid Parameter Received", e);
            return response;
        }
        catch (Exception e)
        {
            response.body(generateError(e.getMessage(), reply_format));
            response.status(400);
            LOGGER.info("Exception Caught", e);
            return response;
        }

        return response;
    };

    public static Route searchPatientByGet = (request, response) -> {
        Representation reply_format = request.attribute("reply_format");

        Map<String, String[]> params_map = request.queryMap().toMap();
        JSONObject search_params = new JSONObject(params_map);
        try {
            JSONObject reply = converterOpenempi.patientSearch(search_params);
            JSONObject response_raw = new JSONObject();
            response_raw.put("entry", reply);
            response.body(jsonToString(response_raw, reply_format));
        }
        catch (Exception e)
        {
            response.body(generateError(e.getMessage(), reply_format));
            response.status(400);
            LOGGER.info("Exception Caught", e);
            return response;
        }

        return response;
    };

    public static Route searchPatientByPost = (request, response) -> {
        Representation request_format = request.attribute("request_format");
        Representation reply_format = request.attribute("reply_format");

        JSONObject resource = parseResource(request.body(), request_format);

        JSONObject search_params = new JSONObject(resource);

        JSONObject reply = converterOpenempi.patientSearch(search_params);
        JSONObject response_raw = new JSONObject();
        response_raw.put("entry", reply);

        response.body(jsonToString(response_raw, reply_format));

        return response;
    };

    public static Route readPatient = (request, response) -> {
        Representation reply_format = request.attribute("reply_format");
        JSONObject reply = converterOpenempi.patientRead(request.params("id"));
        JSONObject response_raw = new JSONObject();
        response_raw.put("entry", reply);

        response.body(jsonToString(response_raw, reply_format));
        return response;
    };

    public static Route updatePatient = (request, response) -> {
        String id = request.params("id");
        Representation request_format = request.attribute("request_format");
        Representation reply_format = request.attribute("reply_format");
        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);

        try {
            JSONObject resource = parseResource(request.body(), request_format);
            String reply = converterOpenempi.patientUpdate(id, resource);
            JSONObject response_raw = new JSONObject();
            response_raw.put("message", reply);

            response.body(jsonToString(response_raw, reply_format));
            return response;
        }
        catch (DataFormatException e)
        {
            response.body(generateError("Invalid Value", reply_format));
            LOGGER.info("Invalid Parameter Received", e);
            return response;
        }
        catch (ClassCastException e)
        {
            response.body(generateError("Incompatible Type", reply_format));
            LOGGER.info("Incompatible Type Received", e);
            return response;
        }
    };

    public static Route patchPatient = (request, response) -> {
        String id = request.params("id");
        Representation request_format = request.attribute("request_format");
        Representation reply_format = request.attribute("reply_format");
        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);

        try {
            JsonPatch patch = parsePatch(request.body(), request_format);
            String reply = converterOpenempi.patientPatch(id, patch);
            JSONObject response_raw = new JSONObject();
            response_raw.put("message", reply);
            response.body(jsonToString(response_raw, reply_format));
            return response;
        }
        catch (IOException e)
        {
            response.body(generateError("Invalid Patch", reply_format));
            LOGGER.info("Invalid Patch Received", e);
            return response;
        }
    };

    public static Route deletePatient = (request, response) -> {
        Representation reply_format = request.attribute("reply_format");
        try {
            String reply = converterOpenempi.patientDelete(request.params("id"));
            JSONObject response_raw = new JSONObject();
            response_raw.put("message", reply);
            response.body(jsonToString(response_raw, reply_format));
        }
        catch (Exception e)
        {
            response.body(generateError(e.getMessage(), reply_format));
            response.status(400);
            LOGGER.info("Exception Caught", e);
            return response;
        }
        return response;
    };
}
