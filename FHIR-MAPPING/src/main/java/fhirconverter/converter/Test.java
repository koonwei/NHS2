/**
 * 
 */
package fhirconverter.converter;

/**
 * @author Shruti Sinha
 *
 */
public class Test {

	public static String removeTest = "<person><address1>8 TowerHouse</address1>"
			+ "<address2>Main Road</address2>"
			+ "<birthOrder>3</birthOrder>"
			+ "<birthPlace>Ipswich</birthPlace>"
			+ "<city>London</city>"
			+ "<country>United Kingdom</country>"
			+ "<countryCode>UK</countryCode>"
			+ "<dateOfBirth>1982-10-19T00:00:00Z</dateOfBirth>"
			+ "<deathInd>23</deathInd>"
			+ "<degree>Masters</degree>"
			+ "<familyName>Priti</familyName>"
			+ "<fatherName>Tom</fatherName>"
			+ "<gender>"
			+ "	<genderCd>1</genderCd>"
			+ "	<genderCode>F</genderCode>"
			+ "	<genderDescription>Female</genderDescription>"
			+ "	<genderName>Female</genderName>"
			+ "</gender>"
			+ "<givenName>Gemma</givenName>"
			+ "<maritalStatusCode>Single</maritalStatusCode>"
			+ "<middleName>Hen</middleName>"
			+ "<motherName>Jolly</motherName>"
			+ "<mothersMaidenName>Lisa</mothersMaidenName>"
			+ "<multipleBirthInd>2</multipleBirthInd>"
			+ "<personIdentifiers>"
			+ "<identifier>555</identifier>"
			+ "<identifierDomain>"
			+ "<identifierDomainName>NHS</identifierDomainName>"
			+ "</identifierDomain>"
			+ "</personIdentifiers>"
			+ "<personIdentifiers>"
			+ "<identifier>444</identifier>"
			+ "<identifierDomain>"
			+ "<identifierDomainName>TIN</identifierDomainName>"
			+ "</identifierDomain>"
			+ "</personIdentifiers>"
			+ "<phoneAreaCode>44</phoneAreaCode>"
			+ "<phoneCountryCode>44</phoneCountryCode>"
			+ "<phoneExt>888</phoneExt>"
			+ "<phoneNumber>90909090</phoneNumber>"
			+ "<postalCode>SE1234L</postalCode>"
			+ "<prefix>Ms</prefix>"
			+ "<ssn>666</ssn>"
			+ "<state>London</state>"
			+ "<suffix>SS</suffix>"
			+ "<language>English</language>"
			+ "<email>gen@gmail.com</email>"
			+ "</person>";
	
	public static void main(String ars[]) throws Exception{
		
		OpenEMPIbase o = new OpenEMPIbase();
		//System.out.println("remove Identifier " + o.removeOpenEMPIIdentifier(removeTest));
		//System.out.println("extractIdentifier " + o.fetchIdDomainsInRequest(removeTest));
		System.out.println("extractIdentifier " + o.getIdentifierDomains());
	}
}
