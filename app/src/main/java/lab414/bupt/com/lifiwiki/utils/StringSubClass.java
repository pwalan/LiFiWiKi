package lab414.bupt.com.lifiwiki.utils;

public class StringSubClass {

	//(temp, "guess-info-text", "</div>")
	public String subStringOne(String content, String Starttag, String Endtag) {
		
		int firstOccur = content.indexOf(Starttag);
		
		int lastOccur = content.indexOf(Endtag, firstOccur);
		
		return content.substring(firstOccur, lastOccur);
	}
	
	public String subStringTwo(String content, String Starttag, String Endtag) {
		
		int firstOccur = content.indexOf(Starttag);
		
		int lastOccur = content.indexOf(Endtag, firstOccur);
		
		return content.substring(firstOccur + Starttag.length(), lastOccur);
	}
}
