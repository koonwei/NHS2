package fhirconverter.spark;

import com.google.gson.Gson;
import org.hl7.fhir.dstu3.model.Patient;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResourceParserTest {

    private Gson gson;
    private ResourceParser resource_parser;

    @Before
    public void setUp(){
        gson = new Gson();
        resource_parser = new ResourceParser(Patient.class);
    }

    @Test
    public void testParseXMLPatient()
    {
        JSONObject obtained_patient = resource_parser.parseXML(INPUT_XML_PATIENT);
        String patient_string = gson.toJson(obtained_patient);
        Assert.assertEquals(patient_string, EXPECTED_PATIENT);
    }

    @Test
    public void testParseJSONPatient()
    {

    }

    @Test
    public void testInvalidParamKey()
    {

    }

    @Test
    public void testInvalidParamValue()
    {

    }

    @Test
    public void testIncompatibleType()
    {

    }
}
