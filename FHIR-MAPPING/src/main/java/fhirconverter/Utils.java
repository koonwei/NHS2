package fhirconverter;
import java.util.ArrayList;
import java.util.List;

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
	public static String removeDuplicateRecords(String finalresponse) {
		
		return finalresponse;
		// TODO Auto-generated method stub
		
	}	
}
