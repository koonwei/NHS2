package pathgenerator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public final class Utils{
	private Utils(){
	//No Constructing 
	}
  
	public static org.json.simple.JSONObject readJsonFile(String fileName){
		JSONParser parser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(fileName);
			Object obj = parser.parse(reader);
			org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) obj;	
			return jsonObj;
 		}catch (FileNotFoundException e) {
            		e.printStackTrace();
        	} catch (IOException e) {
            		e.printStackTrace();
        	} catch (ParseException e) {
            		e.printStackTrace();
        	} finally{
			try{
				reader.close();
			}catch (IOException e){
				e.printStackTrace();
			}
	
		}
		return null;
	}
}
