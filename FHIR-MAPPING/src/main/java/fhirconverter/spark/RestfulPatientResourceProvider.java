package fhirconverter.spark;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.github.fge.jsonpatch.JsonPatch;
import fhirconverter.ConverterOpenempi;
import fhirconverter.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.Map;

public class RestfulPatientResourceProvider implements IResourceProvider {
    private static Logger LOGGER = LogManager.getLogger(RestfulPatientResourceProvider.class);

    private static FHIRParser<Patient> parser = new FHIRParser<>(Patient.class);

    private static ConverterOpenempi converterOpenempi = new ConverterOpenempi();

    //TODO: Create config.java for all configs
    private static String serverAddress = "http://localhost:4567/fhir";

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }

    ///////////////////////
    //  Routing
    ///////////////////////
    @Create()
    public MethodOutcome createPatient (@ResourceParam Patient thePatient) {
//        Representation request_format = request.attribute("request_format");
//        Representation reply_format = request.attribute("reply_format");

//        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);
        String jsonStringPatient = FhirContext.forDstu3().newJsonParser().encodeResourceToString(thePatient);
        JSONObject resource = new JSONObject(jsonStringPatient);


        MethodOutcome retVal = new MethodOutcome();

        OperationOutcome outcome = new OperationOutcome();




        try {

            String reply = converterOpenempi.patientCreate(resource);
            retVal.setId(new IdType("Patient", reply, "1"));
            return retVal;
        }
        catch (DataFormatException | ClassCastException e)
        {
            LOGGER.info("Invalid Parameter Received", e);
            outcome.addIssue().setDiagnostics("Invalid Parameter Received");
            retVal.setOperationOutcome(outcome);
            return retVal;
        }
        catch (Exception e)
        {
            LOGGER.info("Exception Caught", e);
            outcome.addIssue().setDiagnostics("e.getMessage()");
            retVal.setOperationOutcome(outcome);
            return retVal;
        }
    };

//    public static Route searchPatientByGet = (request, response) -> {
//        Representation reply_format = request.attribute("reply_format");
//
//        Map<String, String[]> params_map = request.queryMap().toMap();
//        JSONObject search_params = new JSONObject(params_map);
//        try {
//            JSONObject reply = converterOpenempi.patientSearch(search_params);
//            JSONObject response_raw = new JSONObject();
//            response_raw.put("entry", reply);
//            response.body(jsonToString(response_raw, reply_format));
//            response.status(200);
//            return response;
//        }
//        catch (Exception e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(400);
//            LOGGER.info("Exception Caught", e);
//            return response;
//        }
//
//    };
//
//    public static Route searchPatientByPost = (request, response) -> {
//        Representation request_format = request.attribute("request_format");
//        Representation reply_format = request.attribute("reply_format");
//        try {
//            JSONObject resource = parseToJSON(request.body(), request_format);
//            JSONObject reply = converterOpenempi.patientSearch(resource);
//            JSONObject response_raw = new JSONObject();
//            response_raw.put("entry", reply);
//            response.body(jsonToString(response_raw, reply_format));
//            response.status(200);
//            return response;
//        }
//        catch (DataFormatException e)
//        {
//            response.body(generateError("Invalid Value", reply_format));
//            LOGGER.info("Invalid Parameter Received", e);
//            response.status(400);
//            return response;
//        }
//        catch (Exception e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(400);
//            LOGGER.info("Exception Caught", e);
//            return response;
//        }
//    };
//
//    public static Route readPatient = (request, response) -> {
//        String resourceId = request.params("id");
//        Representation reply_format = request.attribute("reply_format");
//
//        try {
//
//            int intId = Integer.parseInt(resourceId);
//            JSONObject reply = converterOpenempi.patientRead(resourceId);
//            response.header("location", serverAddress + "/Patient/" + resourceId);
//            response.body(jsonToString(reply, reply_format));
//            return response;
//        }
//        catch (ResourceNotFoundException e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(204);
//            LOGGER.info("No Resource Found for id " + resourceId, e);
//            return response;
//        }
//        catch (NumberFormatException e)
//        {
//            response.body(generateError("Unacceptable id " + resourceId, reply_format));
//            response.status(400);
//            LOGGER.info("Unacceptable id " + resourceId, e);
//            return response;
//        }
//        catch (Exception e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(400);
//            LOGGER.info("Exception Caught", e);
//            return response;
//        }
//    };
//
//    public static Route updatePatient = (request, response) -> {
//
//        String resourceId = request.params("id");
//        Representation request_format = request.attribute("request_format");
//        Representation reply_format = request.attribute("reply_format");
//        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);
//
//        try {
//            int intId = Integer.parseInt(resourceId);
//            JSONObject resource = parseResource(request.body(), request_format);
//            String reply = converterOpenempi.patientUpdate(resourceId, resource);
//            JSONObject response_raw = new JSONObject();
//            response_raw.put("message", reply);
//            if(reply.equals("Created"))
//                response.status(201);
//            else
//                response.status(200);
//
//            response.body(jsonToString(response_raw, reply_format));
//
//            return response;
//        }
//        catch (DataFormatException | ClassCastException e)
//        {
//            response.body(generateError("Invalid Value", reply_format));
//            LOGGER.info("Invalid Parameter Received", e);
//            response.status(400);
//            return response;
//        }
//        catch (ResourceNotFoundException e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(204);
//            LOGGER.info("No Resource Found for id " + resourceId, e);
//            return response;
//        }
//        catch (NumberFormatException e)
//        {
//            response.body(generateError("Unacceptable id " + resourceId, reply_format));
//            response.status(400);
//            LOGGER.info("Unacceptable id " + resourceId, e);
//            return response;
//        }
//        catch (Exception e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            LOGGER.info("Exception Caught", e);
//            response.status(422);
//            return response;
//        }
//    };
//
//    public static Route patchPatient = (request, response) -> {
//        String resourceId = request.params("id");
//        Representation request_format = request.attribute("request_format");
//        Representation reply_format = request.attribute("reply_format");
//        LOGGER.debug("Request format: " + request_format + " Reply format: " + reply_format);
//
//        try {
//            JsonPatch patch = parsePatch(request.body(), request_format);
//            String reply = converterOpenempi.patientPatch(resourceId, patch);
//            JSONObject response_raw = new JSONObject();
//            response_raw.put("message", reply);
//            response.body(jsonToString(response_raw, reply_format));
//            return response;
//        }
//        catch (DataFormatException e)
//        {
//            response.body(generateError("Invalid Value", reply_format));
//            LOGGER.info("Invalid Parameter Received", e);
//            response.status(400);
//            return response;
//        }
//        catch (ResourceNotFoundException e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(204);
//            LOGGER.info("No Resource Found for id " + resourceId, e);
//            return response;
//        }
//        catch (NumberFormatException e)
//        {
//            response.body(generateError("Unacceptable id " + resourceId, reply_format));
//            response.status(400);
//            LOGGER.info("Unacceptable id " + resourceId, e);
//            return response;
//        }
//        catch (IOException e)
//        {
//            response.body(generateError("Invalid Patch", reply_format));
//            LOGGER.info("Invalid Patch Received", e);
//            return response;
//        }
//    };
//
//    public static Route deletePatient = (request, response) -> {
//        String resourceId = request.params("id");
//        Representation reply_format = request.attribute("reply_format");
//        try {
//            int intId = Integer.parseInt(resourceId);
//            String reply = converterOpenempi.patientDelete(resourceId);
//            JSONObject response_raw = new JSONObject();
//            response_raw.put("message", reply);
//            response.body(jsonToString(response_raw, reply_format));
//            response.status(204);
//        }
//        catch (NumberFormatException e)
//        {
//            response.body(generateError("Unacceptable id " + resourceId, reply_format));
//            response.status(400);
//            LOGGER.info("Unacceptable id " + resourceId, e);
//            return response;
//        }
//        catch (IOException e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(405);
//            LOGGER.info("Not able to delete id " + resourceId, e);
//            return response;
//        }
//        catch (Exception e)
//        {
//            response.body(generateError(e.getMessage(), reply_format));
//            response.status(400);
//            LOGGER.info("Exception Caught", e);
//            return response;
//        }
//        return response;
//    };

    ///////////////////////
    //  Private Methods
    ///////////////////////

    private static JSONObject parseToJSON(String data, Representation format) throws DataFormatException, ClassCastException
    {
        if (format == Representation.JSON)
            return  new JSONObject(data);
        else if (format == Representation.XML)
            return XML.toJSONObject(data);
        else
        {
            throw new DataFormatException("Unacceptable format type");
        }
    }

    private static JSONObject parseResource(String data, Representation format) throws DataFormatException, ClassCastException
    {
        if (format == Representation.JSON)
            return  parser.parseJSONResource(data);
        else if (format == Representation.XML)
            return parser.parseXMLResource(data);
        else
        {
            throw new DataFormatException("Unacceptable format type");
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
            throw new DataFormatException("Unacceptable format type");
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
}
