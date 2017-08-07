/**
 * 
 */
package fhirconverter.converter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
/**
 * @author Shruti Sinha
 *
 */
public class OpenEHRConvertorTests {

	public static String ex = "{" 
			+ "		\"patientId\": \"123456\", " 
			+ "		\"meta\": 	{"
			+ "	    \"href\": \"http://test.operon.systems/rest/v1/query/\"" 
			+ "	  				},"
			+ "	  \"aql\": \"select\"," 
			+ "	  \"executedAql\": \"select\"," 
			+ "	  \"resultSet\": [" 
			+ "	    {"
			+ "	      \"3141_9_magnitude\": 10.9," 
			+ "	      \"39156_5_units\": null,"
			+ "	      \"3141_9_units\": \"kg\","
			// + " \"Head_circumference_units\": \"cm\","
			// + " \"Skeletal_age_date\": null,"
			// + " \"Skeletal_age\": null,"
			+ "	      \"8302_2_date\": \"2011-04-11T00:00:00+02:00\"," 
			+ "	      \"8302_2_magnitude\": 82.3,"
			// + " \"Head_circumference_date\": \"2011-04-11T00:00:00+02:00\","
			+ "	      \"3141_9_date\": \"2011-04-11T00:00:00+02:00\"," 
			+ "	      \"8302_2_units\": \"cm\","
			+ "	      \"39156_5_magnitude\": null," 
			+ "	      \"39156_5_date\": null,"
			// + " \"Head_circumference_magnitude\": 52"
			+ "	    }," 
			+ "	    {" 
			+ "	      \"3141_9_magnitude\": 9.8," 
			+ "	      \"39156_5_units\": \"kg/m2\","
			+ "	      \"3141_9_units\": \"kg\","
			// + " \"Head_circumference_units\": \"cm\","
			// + " \"Skeletal_age_date\": \"2010-04-10T00:00:00+02:00\","
			// + " \"Skeletal_age\": \"P6Y\","
			+ "	      \"8302_2_date\": \"2010-04-10T00:00:00+02:00\"," 
			+ "	      \"8302_2_magnitude\": 177,"
			// + " \"Head_circumference_date\": \"2010-04-10T00:00:00+02:00\","
			+ "	      \"3141_9_date\": \"2010-04-10T00:00:00+02:00\"," 
			+ "	      \"8302_2_units\": \"cm\","
			+ "	      \"39156_5_magnitude\": 17.2," 
			+ "	      \"39156_5_date\": \"2010-04-10T00:00:00+02:00\","
			// + " \"Head_circumference_magnitude\": 50"
			+ "	    }" 
			+ "	  ]" 
			+ "	}";
	
	@Test
	public void testConversion() throws Exception {
		
		OpenEHRConvertor convertor = new OpenEHRConvertor();
		JSONObject json = new JSONObject(ex);
		
		List<Observation> obtainedObsList = convertor.jsonToObservation(json);
		List<Observation> expectedObsList = new ArrayList<>();
		
		String expectedString = "" ;
		String obtainedString = "";
		
		Observation obs1 = new Observation();
		Observation obs2 = new Observation();
		Observation obs3 = new Observation();
		Observation obs4 = new Observation();
		Observation obs5 = new Observation();
		Observation obs6 = new Observation();		
		QuantityDt qd1 = new QuantityDt();
		QuantityDt qd2 = new QuantityDt();
		QuantityDt qd3 = new QuantityDt();
		QuantityDt qd4 = new QuantityDt();
		QuantityDt qd5 = new QuantityDt();
		QuantityDt qd6 = new QuantityDt();
		
		obs1.getCode().addCoding(new CodingDt("http://loinc.org", "3141-9"));
		obs1.getCode().setText("Weight");
		qd1.setValue(10.9);
		qd1.setCode("kg");
		qd1.setUnit("kg");
		qd1.setSystem("http://unitsofmeasure.org");
		obs1.setValue(qd1);
		obs1.setEffective(new DateTimeDt("2011-04-11T00:00:00+02:00"));
		obs1.getSubject().setReference("123456");
		
		obs2.getCode().addCoding(new CodingDt("http://loinc.org", "8302-2"));
		obs2.getCode().setText("Height");
		qd2.setValue(82.3);
		qd2.setCode("cm");
		qd2.setUnit("cm");
		qd2.setSystem("http://unitsofmeasure.org");
		obs2.setValue(qd2);
		obs2.setEffective(new DateTimeDt("2011-04-11T00:00:00+02:00"));
		obs2.getSubject().setReference("123456");
		
		obs3.getCode().addCoding(new CodingDt("http://loinc.org", "39156-5"));
		obs3.getCode().setText("BMI");
		qd3.setValue(0.0);
		qd3.setCode("");
		qd3.setUnit("");
		qd3.setSystem("http://unitsofmeasure.org");
		obs3.setValue(qd3);
		obs3.setEffective(new DateTimeDt(""));
		obs3.getSubject().setReference("123456");
		
		obs4.getCode().addCoding(new CodingDt("http://loinc.org", "3141-9"));
		obs4.getCode().setText("Weight");
		qd4.setValue(9.8);
		qd4.setCode("kg");
		qd4.setUnit("kg");
		qd4.setSystem("http://unitsofmeasure.org");
		obs4.setValue(qd4);
		obs4.setEffective(new DateTimeDt("2010-04-10T00:00:00+02:00"));
		obs4.getSubject().setReference("123456");
		
		obs5.getCode().addCoding(new CodingDt("http://loinc.org", "8302-2"));
		obs5.getCode().setText("Height");
		qd5.setValue(177.0);
		qd5.setCode("cm");
		qd5.setUnit("cm");
		qd5.setSystem("http://unitsofmeasure.org");
		obs5.setValue(qd5);
		obs5.setEffective(new DateTimeDt("2010-04-10T00:00:00+02:00"));
		obs5.getSubject().setReference("123456");
		
		obs6.getCode().addCoding(new CodingDt("http://loinc.org", "39156-5"));
		obs6.getCode().setText("BMI");
		qd6.setValue(17.2);
		qd6.setCode("kg/m2");
		qd6.setUnit("kg/m2");
		qd6.setSystem("http://unitsofmeasure.org");
		obs6.setValue(qd6);
		obs6.setEffective(new DateTimeDt("2010-04-10T00:00:00+02:00"));
		obs6.getSubject().setReference("123456");
			
		expectedObsList.add(obs1);
		expectedObsList.add(obs2);
		expectedObsList.add(obs6);
		expectedObsList.add(obs4);
		expectedObsList.add(obs5);
		expectedObsList.add(obs3);

		FhirContext ctx = FhirContext.forDstu2();
		
		/*** Converts the obtained observation list to JSON ***/
	    for(Observation observation : obtainedObsList){
	            String resourceJson = ctx.newJsonParser().encodeResourceToString(observation);    
	            JSONObject obtained_object = new JSONObject(resourceJson);
	            obtainedString = obtained_object.toString();
	    }
	    
	    /*** Converts the expected observation list to JSON ***/
	    for(Observation observation : expectedObsList){
            String resourceJson = ctx.newJsonParser().encodeResourceToString(observation);    
            JSONObject expected_object = new JSONObject(resourceJson);    
            expectedString = expected_object.toString();
    }
	    System.out.println("Obtained size : " + obtainedObsList.size() + " Expected size : "+ expectedObsList.size());
	    Assert.assertEquals(obtainedObsList.size(), expectedObsList.size());
	    System.out.println("expected : " + expectedString);
	    System.out.println("obtained : " + obtainedString);
	    Assert.assertEquals(expectedString, obtainedString);
	}
}
