package fhirconverter;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatch;  
import com.github.fge.jsonpatch.JsonPatchException; 

import ca.uhn.fhir.parser.IParser;
import fhirconverter.spark.FHIRParser;

import org.json.JSONException; 
import org.json.JSONObject;
import org.json.JSONArray;

import fhirconverter.exceptions.*;
public class PatientTestCases{
    private ObjectMapper mapper;

    final private String updateNotExist = "{\r\n" + 
    		" \r\n" + 
    		"\"resourceType\": \"Patient\",\r\n" + 
    		"  \"meta\": {\r\n" + 
    		"    \"versionId\": \"1\",\r\n" + 
    		"    \"lastUpdated\": \"2017-07-12T10:19:56.629-04:00\"\r\n" + 
    		"  },\r\n" + 
    		"  \"identifier\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"OpenMRS\",\r\n" + 
    		"      \"value\": \"56874987549879873\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"name\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"family\": \"Elizabeth\",\r\n" + 
    		"      \"given\": [\r\n" + 
    		"        \"Stamos\",\r\n" + 
    		"        \"Mary\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"prefix\": [\r\n" + 
    		"        \"Miss\"\r\n" + 
    		"      ]\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"telecom\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"email\",\r\n" + 
    		"      \"value\": \"elizabet@gmail.com\"\r\n" + 
    		"    },\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"phone\",\r\n" + 
    		"      \"value\": \"6789754597\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"gender\": \"female\",\r\n" + 
    		"  \"birthDate\": \"1970-09-24\",\r\n" + 
    		"  \"address\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"text\": \"Kings Cross Penton Rise London London 589632 UK\",\r\n" + 
    		"      \"line\": [\r\n" + 
    		"        \"Kings Cross\",\r\n" + 
    		"        \"Penton Rise\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"city\": \"London\",\r\n" + 
    		"      \"state\": \"London\",\r\n" + 
    		"      \"postalCode\": \"589632\",\r\n" + 
    		"      \"country\": \"UK\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"maritalStatus\": {\r\n" + 
    		"    \"text\": \"married\"\r\n" + 
    		"  },\r\n" + 
    		"  \"multipleBirthInteger\": 2\r\n" + 
    		"}\r\n" + 
    		"";
    final private String updateExists = "{\r\n" + 
    		" \r\n" + 
    		"\"resourceType\": \"Patient\",\r\n" + 
    		"\"id\": \"300\",\r\n" + 
    		"  \"meta\": {\r\n" + 
    		"    \"versionId\": \"1\",\r\n" + 
    		"    \"lastUpdated\": \"2017-07-12T10:19:56.629-04:00\"\r\n" + 
    		"  },\r\n" + 
    		"  \"identifier\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"SSN\",\r\n" + 
    		"      \"value\": \"54645987312\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"name\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"family\": \"Marianna\",\r\n" + 
    		"      \"given\": [\r\n" + 
    		"        \"Antonopoulou\",\r\n" + 
    		"        \"Malama\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"prefix\": [\r\n" + 
    		"        \"Mrs\"\r\n" + 
    		"      ]\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"telecom\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"email\",\r\n" + 
    		"      \"value\": \"mariannaantonopoulou@gmail.com\"\r\n" + 
    		"    },\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"phone\",\r\n" + 
    		"      \"value\": \"6789797\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"gender\": \"female\",\r\n" + 
    		"  \"birthDate\": \"1990-07-11\",\r\n" + 
    		"  \"maritalStatus\": {\r\n" + 
    		"    \"text\": \"married\"\r\n" + 
    		"  },\r\n" + 
    		"  \"multipleBirthInteger\": 2\r\n" + 
    		"}\r\n" + 
    		"";
    final private String updateExistsCreate = "{\r\n" + 
    		"  \r\n" + 
    		"\"resourceType\": \"Patient\",\r\n" + 
    		"  \"meta\": {\r\n" + 
    		"    \"versionId\": \"1\",\r\n" + 
    		"    \"lastUpdated\": \"2017-07-12T10:19:56.629-04:00\"\r\n" + 
    		"  },\r\n" + 
    		"  \"identifier\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"SSN\",\r\n" + 
    		"      \"value\": \"54645987312\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"name\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"family\": \"Marina\",\r\n" + 
    		"      \"given\": [\r\n" + 
    		"        \"Antoniou\",\r\n" + 
    		"        \"Malama\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"prefix\": [\r\n" + 
    		"        \"Mrs\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"suffix\": [\r\n" + 
    		"        \"Dr\"\r\n" + 
    		"      ]\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"telecom\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"email\",\r\n" + 
    		"      \"value\": \"elenaioannou@gmail.com\"\r\n" + 
    		"    },\r\n" + 
    		"    {\r\n" + 
    		"      \"system\": \"phone\",\r\n" + 
    		"      \"value\": \"0442564616748963\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"gender\": \"female\",\r\n" + 
    		"  \"birthDate\": \"1990-07-11\",\r\n" + 
    		"  \"address\": [\r\n" + 
    		"    {\r\n" + 
    		"      \"text\": \"Kings Cross Penton Rise London London 589632 UK\",\r\n" + 
    		"      \"line\": [\r\n" + 
    		"        \"Kings Cross\",\r\n" + 
    		"        \"Penton Rise\"\r\n" + 
    		"      ],\r\n" + 
    		"      \"city\": \"London\",\r\n" + 
    		"      \"state\": \"London\",\r\n" + 
    		"      \"postalCode\": \"589632\",\r\n" + 
    		"      \"country\": \"UK\"\r\n" + 
    		"    }\r\n" + 
    		"  ],\r\n" + 
    		"  \"maritalStatus\": {\r\n" + 
    		"    \"text\": \"married\"\r\n" + 
    		"  },\r\n" + 
    		"  \"multipleBirthInteger\": 2\r\n" + 
    		"}\r\n" + 
    		"";
    
    
    
    
	final private String searchParameters = "{\r\n" + 
			"  \"name\": \"Kathrin\",\r\n" + 
			"}";
	
	final private String Record = "{\r\n" + 
			"  \r\n" + 
			"\"resourceType\": \"Patient\",\r\n" + 
			"  \"meta\": {\r\n" + 
			"    \"lastUpdated\": \"2017-07-14T00:00:00.000+00:00\"\r\n" + 
			"  },\r\n" + 
			"  \"identifier\": [\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"SSN\",\r\n" + 
			"      \"value\": \"54645989875566587312\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"name\": [\r\n" + 
			"    {\r\n" + 
			"      \"family\": \"Papantwnopulou\",\r\n" + "\"use\":\"official\","+
			"      \"given\": [\r\n" + 
			"        \"Georgiana\",\r\n" + 
			"        \"Alexandra\"\r\n" + 
			"      ]\r\n" +  
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"telecom\": [\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"email\",\r\n" + 
			"      \"value\": \"papantwnopoulougeorgiana@gmail.com\"\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"phone\",\r\n" + 
			"      \"value\": \"044225216553\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"gender\": \"female\",\r\n" + 
			"  \"birthDate\": \"1991-06-04\",\r\n" + 
			"  \"address\": [\r\n" + 
			"    {\r\n" + 
			"      \"text\": \"Kings Cross Pentoville Road London London 589632 UK\",\r\n" + 
			"      \"line\": [\r\n" + 
			"        \"Kings Cross\",\r\n" + 
			"        \"Pentoville Road\"\r\n" + 
			"      ],\r\n" + 
			"      \"city\": \"London\",\r\n" + 
			"      \"state\": \"London\",\r\n" + 
			"      \"postalCode\": \"589632\",\r\n" + 
			"      \"country\": \"UK\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"maritalStatus\": {\r\n" + 
			"    \"text\": \"single\"\r\n" + 
			"  },\r\n" + 
			"  \"multipleBirthInteger\": 1\r\n" + 
			"}";
	
	
	final private String jsonRecord = "{\n" +
            "  \"resourceType\": \"Patient\",\n" +
            "  \"id\": \"170445\",\n" +
            "  \"meta\": {\n" +
            "    \"versionId\": \"1\",\n" +
            "    \"lastUpdated\": \"2017-07-06T13:12:42.328-04:00\",\n" +
            "    \"profile\": [\n" +
            "      \"http://standardhealthrecord.org/fhir/StructureDefinition/shr-demographics-PersonOfRecord\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"text\": {\n" +
            "    \"status\": \"generated\",\n" +
            "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">Generated by <a href=\\\"https://github.com/synthetichealth/synthea\\\">Synthea</a>. Version identifier: b38b8877256db0b7ec1ede270729a1c940ad8b33</div>\"\n" +
            "  },\n" +
            "  \"extension\": [\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/us/core/StructureDefinition/us-core-race\",\n" +
            "      \"valueCodeableConcept\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/v3/Race\",\n" +
            "            \"code\": \"2106-3\",\n" +
            "            \"display\": \"White\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"text\": \"race\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity\",\n" +
            "      \"valueCodeableConcept\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/v3/Ethnicity\",\n" +
            "            \"code\": \"2186-5\",\n" +
            "            \"display\": \"Nonhispanic\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"text\": \"ethnicity\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/StructureDefinition/birthPlace\",\n" +
            "      \"valueAddress\": {\n" +
            "        \"city\": \"Fitchburg\",\n" +
            "        \"state\": \"MA\",\n" +
            "        \"country\": \"US\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName\",\n" +
            "      \"valueString\": \"Juliet345 Pollich990\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex\",\n" +
            "      \"valueCode\": \"F\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://hl7.org/fhir/StructureDefinition/patient-interpreterRequired\",\n" +
            "      \"valueBoolean\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://standardhealthrecord.org/fhir/StructureDefinition/shr-actor-FictionalPerson-extension\",\n" +
            "      \"valueBoolean\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://standardhealthrecord.org/fhir/StructureDefinition/shr-demographics-FathersName-extension\",\n" +
            "      \"valueHumanName\": {\n" +
            "        \"text\": \"Dane156 Stroman117\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"url\": \"http://standardhealthrecord.org/fhir/StructureDefinition/shr-demographics-SocialSecurityNumber-extension\",\n" +
            "      \"valueString\": \"999-27-8612\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"identifier\": [\n" +
            "    {\n" +
            "      \"system\": \"https://github.com/synthetichealth/synthea\",\n" +
            "      \"value\": \"1b837734-5083-4590-b588-4c7732e5ac8f\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/identifier-type\",\n" +
            "            \"code\": \"SB\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"http://hl7.org/fhir/sid/us-ssn\",\n" +
            "      \"value\": \"999278612\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
            "            \"code\": \"DL\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"urn:oid:2.16.840.1.113883.4.3.25\",\n" +
            "      \"value\": \"S99969306\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
            "            \"code\": \"PPN\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"http://standardhealthrecord.org/fhir/StructureDefinition/passportNumber\",\n" +
            "      \"value\": \"X77003736X\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/v2/0203\",\n" +
            "            \"code\": \"MR\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"system\": \"http://hospital.smarthealthit.org\",\n" +
            "      \"value\": \"1b837734-5083-4590-b588-4c7732e5ac8f\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"name\": [\n" +
            "    {\n" +
            "      \"use\": \"official\",\n" +
            "      \"family\": \"Hansen448\",\n" +
            "      \"given\": [\n" +
            "        \"Elissa46\"\n" +
            "      ],\n" +
            "      \"prefix\": [\n" +
            "        \"Mrs.\"\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"use\": \"maiden\",\n" +
            "      \"family\": \"Stroman117\",\n" +
            "      \"given\": [\n" +
            "        \"Elissa46\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"telecom\": [\n" +
            "    {\n" +
            "      \"system\": \"phone\",\n" +
            "      \"value\": \"(448) 739-2195\",\n" +
            "      \"use\": \"home\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gender\": \"female\",\n" +
            "  \"birthDate\": \"1956-04-22\",\n" +
            "  \"deceasedDateTime\": \"2015-01-04T07:06:43-05:00\",\n" +
            "  \"address\": [\n" +
            "    {\n" +
            "      \"extension\": [\n" +
            "        {\n" +
            "          \"url\": \"http://hl7.org/fhir/StructureDefinition/geolocation\",\n" +
            "          \"extension\": [\n" +
            "            {\n" +
            "              \"url\": \"latitude\",\n" +
            "              \"valueDecimal\": 42.610683558943805\n" +
            "            },\n" +
            "            {\n" +
            "              \"url\": \"longitude\",\n" +
            "              \"valueDecimal\": -71.07136500645896\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"line\": [\n" +
            "        \"3992 Metz Mountains\"\n" +
            "      ],\n" +
            "      \"city\": \"North Andover\",\n" +
            "      \"state\": \"MA\",\n" +
            "      \"postalCode\": \"01845\",\n" +
            "      \"country\": \"US\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"maritalStatus\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"http://hl7.org/fhir/v3/MaritalStatus\",\n" +
            "        \"code\": \"M\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"text\": \"M\"\n" +
            "  },\n" +
            "  \"multipleBirthBoolean\": false,\n" +
            "  \"communication\": [\n" +
            "    {\n" +
            "      \"language\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"http://hl7.org/fhir/ValueSet/languages\",\n" +
            "            \"code\": \"en-US\",\n" +
            "            \"display\": \"English (United States)\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"generalPractitioner\": [\n" +
            "    {\n" +
            "      \"reference\": \"Organization/170444\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
	final private String expectedSearch = "{\"entry\":[{\"resource\":" + Record + "}],\"resourceType\":\"Bundle\"}";
	final private String expectedRead = "{\"entry\":"+ Record + ",\"message\":\"Read Patient 176\"}";
	
	final private String createPatient = "{\r\n" + 
			"  \"resourceType\": \"Patient\",\r\n" + 
			"  \"meta\": {\r\n" + 
			"    \"lastUpdated\": \"2017-07-14T00:00:00.000+00:00\"\r\n" + 
			"  },\r\n" + 
			"  \"identifier\": [\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"VirginiaDLN\",\r\n" + 
			"      \"value\": \"54645987312\"\r\n" + 
			"    }],\r\n" + 
			"  \"name\": [\r\n" + 
			"    {\r\n" + 
			"      \"family\": \"Andreas\",\r\n" + 
			"      \"given\": [\r\n" + 
			"        \"Oliver\"\r\n" + 
			"      ],\r\n" + "\"use\":\"official\", "+
			"      \"prefix\": [\r\n" + 
			"        \"Mr\"\r\n" + 
			"      ],\r\n" + 
			"      \"suffix\": [\r\n" + 
			"        \"Dr\"\r\n" + 
			"      ]\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"telecom\": [\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"email\",\r\n" + 
			"      \"value\": \"mariaantoniou@gmail.com\"\r\n" + 
			"    },\r\n" + 
			"    {\r\n" + 
			"      \"system\": \"phone\",\r\n" + 
			"      \"value\": \"044225216748963\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"gender\": \"male\",\r\n" + 
			"  \"birthDate\": \"2017-07-11\",\r\n" + 
			"  \"address\": [\r\n" + 
			"    {\r\n" + 
			"      \"text\": \"Penton Street Caledonian Road Manchester Manchester 656498 UK\",\r\n" + 
			"      \"line\": [\r\n" + 
			"        \"Penton Street\",\r\n" + 
			"        \"Caledonian Road\"\r\n" + 
			"      ],\r\n" + 
			"      \"city\": \"Manchester\",\r\n" + 
			"      \"state\": \"Manchester\",\r\n" + 
			"      \"postalCode\": \"656498\",\r\n" + 
			"      \"country\": \"UK\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"maritalStatus\": {\r\n" + 
			"    \"text\": \"single\"\r\n" + 
			"  },\r\n" + 
			"  \"deceasedDateTime\": \"2017-08-08T07:06:12Z\",\r\n" + 
			"\r\n" + 
			"  \"multipleBirthInteger\": 2\r\n" + 
			"}";
	
	
	/*
	 * Change to diff to compare results
	@Test
	public void testPatientWorkFlow(){
		PatientFHIR tester = new PatientFHIR();
		String data = tester.convertFHIR();
		JSONObject jsonObj = new JSONObject(data);
		String expected = "{resourceType:\"Patient\",identifier:[{system:\"http://ns.electronichealth.net.au/id/hi/ihi/1.0\",value: 8003608166690503}], name:[{use: \"official\", given:[\"Sam\"], prefix:[ \"Mr\"]}]}";
		Assert.assertTrue(jsonObj.has("resourceType"));		
	}*/

	@Test
	public void testPatientSearch() throws Exception {
		PatientFHIR tester = new PatientFHIR();	
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());
		JSONObject expected = patient;
		expected.remove("meta");
		String death = expected.optString("deceasedDateTime");
		if(expected.has("deceasedDateTime")) {
			expected.remove("deceasedDateTime");
			expected.put("deceasedDateTime", death.substring(0,19) + "Z");
		}
		String newRecordID = tester.create(patient);
		JSONObject parameters = new JSONObject(searchParameters);
		JSONObject obtained_object = tester.search(parameters);
		JSONObject resultSearch = obtained_object.getJSONArray("entry").getJSONObject(0).getJSONObject("resource");
		resultSearch.remove("id");
		resultSearch.remove("meta");					
        	OpenEMPIbase delete = new OpenEMPIbase();
		System.out.println(newRecordID+ "FOCUS HERE");
        	delete.commonRemovePersonById(newRecordID);
		String obtained_string = resultSearch.toString();		
        	Assert.assertEquals("Search operation failed \n",expected.toString(), resultSearch.toString());
	}
	@Test
	public void testPatientUpdate() throws ResourceNotFoundException, Exception {
		PatientFHIR tester = new PatientFHIR();	
        	OpenEMPIbase delete = new OpenEMPIbase();
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());
  		String newRecordID = tester.create(patient);
		JsonNode fhirResourceUpdate = JsonLoader.fromPath("resource/updateResourceFhir.json");		
		JSONObject updateCreate = new JSONObject(fhirResourceUpdate.toString());
		String replyExists = tester.update(newRecordID, updateCreate);
        	delete.commonRemovePersonById(newRecordID);
		assertEquals("Update Operation if the record exists failed: ", "Updated", replyExists );		

	}
	@Test(expected = FhirSchemeNotMetException.class)
	public void testPatientPatchPathNotExist() throws Exception{
		PatientFHIR tester = new PatientFHIR();
		final String jsonPatchTest = "[ { \"op\": \"replace\", \"path\": \"/gender\", \"value\": \"male\" }, {\"op\": \"add\", \"path\": \"/what is this\", \"value\": \"male\" } ]";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode patchNode = mapper.readTree(jsonPatchTest);
		final JsonPatch patch = JsonPatch.fromJson(patchNode);
		OpenEMPIbase delete = new OpenEMPIbase();
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());
  		String newRecordID = tester.create(patient);
		try{
			tester.patch(newRecordID,patch);
		}finally{
			delete.commonRemovePersonById(newRecordID);
		}
	}
	@Test(expected = JsonPatchException.class)
	public void testPatientPatchOperatorsNotExist() throws Exception{
		PatientFHIR tester = new PatientFHIR();
		final String jsonPatchTest = "[ { \"op\": \"replace\", \"path\": \"/gende\", \"value\": \"male\" } ]";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode patchNode = mapper.readTree(jsonPatchTest);
		final JsonPatch patch = JsonPatch.fromJson(patchNode);
		OpenEMPIbase delete = new OpenEMPIbase();
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());
  		String newRecordID = tester.create(patient);
		try{
			tester.patch(newRecordID,patch);
		}finally{
		       delete.commonRemovePersonById(newRecordID);
		}
	}
	@Test
	public void testPatientPatchRecord() throws Exception{
		PatientFHIR tester = new PatientFHIR();
		final String jsonPatchTest = "[ { \"op\": \"replace\", \"path\": \"/gender\", \"value\": \"male\" } ]";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode patchNode = mapper.readTree(jsonPatchTest);
		final JsonPatch patch = JsonPatch.fromJson(patchNode);
		OpenEMPIbase delete = new OpenEMPIbase();
		JsonNode fhirResource = JsonLoader.fromPath("resource/ResourceFHIR.json");		
		JSONObject patient = new JSONObject(fhirResource.toString());
  		String newRecordID = tester.create(patient);
		try{
			assertEquals(tester.patch(newRecordID,patch), "Updated");
		}finally{
		      	delete.commonRemovePersonById(newRecordID);
		}
	}
	/*
	@Test
	public void testPatientRead() throws Exception {
		PatientFHIR tester = new PatientFHIR();
		JSONObject expected = new JSONObject(Record);
		JSONObject obtained = tester.read("3");
		Assert.assertEquals("Read operation failed: \nRead Result: \n" + obtained.toString() + " \n" + expected.toString() , expected.toString(), obtained.toString());		
	} 
	@Test
	public void testPatientCreate() throws Exception {
		PatientFHIR tester = new PatientFHIR();	
		JSONObject create = new JSONObject(createPatient);
		
		
		String newRecordID = tester.create(create);
		JSONObject exists = tester.read(newRecordID);
		
        OpenEMPIbase delete = new OpenEMPIbase();
        delete.commonRemovePersonById(newRecordID);
		exists.remove("meta");
		create.remove("meta");
		exists.remove("id");

		Assert.assertEquals("Create operation failed: \nCreate Result: \n" + exists.toString() + " \n" + create.toString() , exists.toString(), create.toString());		
	}*/
	@Test
	public void testPatientDelete() {
	}
}
