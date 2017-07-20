package fhirconverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
public final class Utils{
	private Utils(){
	//No Constructing 
	}
  
	public static JsonNode loadJsonScheme(final String filePath) throws Exception
    	{
	
        	try {
			return JsonLoader.fromPath(filePath);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
   	}
	public static boolean validateScheme(final JsonNode newJson, final String filePath) throws Exception{
		
		try{
			final JsonNode jsonSchema = Utils.loadJsonScheme(filePath);
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonSchema schema = factory.getJsonSchema(jsonSchema);
			ProcessingReport report;
			report = schema.validate(newJson);
			System.out.println(report);
			return report.isSuccess();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * @param finalresponse
	 */
	public static String removeDuplicateRecords(String response) {
		
		response = response.replaceAll("</person>", "</person>\n");
		String[] people = response.split("\n");

		Set<String> set = new HashSet<String>();
		for(int i = 0; i < people.length; i++){
			 set.add(people[i]);
		}
		String finalresponse = set.toString();
		finalresponse = finalresponse.replaceAll(", ", "");
		finalresponse = finalresponse.replaceAll("]", "");
		finalresponse = finalresponse.replace("[", "");
		
		return finalresponse;
		
	}	
}
