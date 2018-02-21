package data.text.clawler;

import org.jsoup.nodes.Document;

public class WeblioClawler extends DictionaryClawler{

	private static final String urlDir = "./resource/input/weblio/url/";
	private static String urlFile;
	private static String urlPath;
	private static final String dicDir = "./resource/input/weblio/dic/";
	private static String dicFile;
	private static String dicPath;
	private static final String textDir = "./resource/input/weblio/text/";
	private static String textFile;
	private static String textPath;
	private static final String urlHead = "http://www.weblio.jp/";
	private static final String urlTail = "/meaning/m1u/";
	private static final String urlCat = "cat/";
	private String urlSyl;
	private String idxURL;


	
	
	/************************************/
	/**********   Constructor  **********/
	/************************************/
	public WeblioClawler(int interval, int depth) {
		super(interval, depth);
	}
	
	
	/************************************/
	/********** AbstractMethod **********/
	/************************************/
	String dicName() {return "weblio";}
	boolean collectIndexURL() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	boolean collectEntryURL() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	String headword(Document document) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	String interpretation(Document document) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}