package fhirconverter.spark;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONObject;

import java.io.IOException;

public class FHIRParser<V> {

    private Class<V> valueClass;
    private IParser jsonParser;
    private IParser xmlParser;
    private static Logger LOGGER = LogManager.getLogger(FHIRParser.class);

    public FHIRParser(Class<V> valueClass){
        FhirContext ctx = FhirContext.forDstu3();
        this.jsonParser = ctx.newJsonParser();
        this.xmlParser = ctx.newXmlParser();
        this.jsonParser.setParserErrorHandler(new StrictErrorHandler());
        this.xmlParser.setParserErrorHandler(new StrictErrorHandler());
        this.valueClass = valueClass;
    }

    public JSONObject parseJSONResource(String jsonData) throws DataFormatException, ClassCastException{

        Object request = jsonParser.parseResource(jsonData);
        Object resource = valueClass.cast(request);
        JSONObject response = new JSONObject(jsonParser.encodeResourceToString((IBaseResource) resource));
        return response;
    }

    public JSONObject parseXMLResource(String xmlData)
    {
        Object request = xmlParser.parseResource(xmlData);
        Object resource = valueClass.cast(request);
        JSONObject response = new JSONObject(jsonParser.encodeResourceToString((IBaseResource) resource));
        return response;
    }


    public JsonPatch parseJSONPatch(String jsonPatchData) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonPatchData, JsonPatch.class);
    }
}
