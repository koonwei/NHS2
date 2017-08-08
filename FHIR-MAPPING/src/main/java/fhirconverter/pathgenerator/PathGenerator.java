package fhirconverter.pathgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import fhirconverter.converter.Utils;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;

import java.io.FileWriter;
import java.io.IOException;
public class PathGenerator{
	public static void main(String[] args){
		try {
			execute();
		}catch (FileNotFoundException e) {
        	 	e.printStackTrace();
      		}catch (XQException e) {
        		e.printStackTrace();
      		}
   	}
	private static void execute() throws FileNotFoundException, XQException{
		InputStream inputStream = new FileInputStream(new File("Xquery.xq"));
      		XQDataSource ds = new SaxonXQDataSource();
      		XQConnection conn = ds.getConnection();
      		XQPreparedExpression exp = conn.prepareExpression(inputStream);
      		XQResultSequence result = exp.executeQuery();      
		String pathString = "{";
     		while (result.next()) {
			String tempString = result.getItemAsString(null);	
			JSONObject objTemp = XML.toJSONObject(tempString);
			String text = objTemp.getJSONObject("type").getString("archetype").split("\\.")[1];
			String lonicCode = mapEHRcodeToLonic(objTemp.getJSONObject("type").getString("archetype"));
			objTemp.getJSONObject("type").put("text",text);
			objTemp = objTemp.getJSONObject("type");
			objTemp.remove("xmlns");
			JSONObject objModified = new JSONObject();
			objModified.put(lonicCode,objTemp);
			String pathTemp = objModified.toString().substring(1, objModified.toString().length());
			pathTemp = pathTemp.substring(0, pathTemp.length() - 1);
			pathString += pathTemp + ",";
		}
		pathString = pathString.substring(0, pathString.length() - 1);
		pathString += "}";
		JSONObject objString = new JSONObject(pathString);
		writeToJsonFile(objString);

   	}	
	private static String mapEHRcodeToLonic(String ehrCode){	
		org.json.simple.JSONObject mappings = Utils.readJsonFile("mapping.json");
		JSONObject mappingsJSONObj =  new JSONObject(mappings.toString());
		JSONArray mappingsJSONArray = mappingsJSONObj.getJSONArray("mapping");
		String lonicCode = "";
		for(Object mapping: mappingsJSONArray){
   			if ( mapping instanceof JSONObject ) {
        			JSONObject mappingObj = (JSONObject) mapping;
				if(mappingObj.getString("archetype").equals(ehrCode)){
					return mappingObj.getString("code");
				}	
   		 	}
		}	
		return lonicCode;	
	}
	private static void writeToJsonFile(JSONObject pathObj){
		try (FileWriter file = new FileWriter("aql_path.json")) {
            		file.write(pathObj.toString(3));
 	           	file.flush();
        	} catch (IOException e) {
            		e.printStackTrace();
        	}	
	}
}

