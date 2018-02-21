package data.text.clawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

public abstract class DictionaryClawler extends Clawler{
	
	protected int depth;			// 何項目まで探索するか

	protected static final String expansion = ".txt";
	

	/************************************/
	/**********   Constructor  **********/
	/************************************/
	public DictionaryClawler(int interval, int depth) {
		super(interval);
		this.depth = depth;
	}
	
	
	/************************************/
	/**********  MemberMethod  **********/
	/************************************/
	/** HTMLテキストから余計なタグ,スペース,かっこを除去する */
	public String cleanHTML(String text) {
		//text = text.replaceAll("<strong>[0-9０-９]+</strong>", "");	// 箇条書きの全角数字とスペース除去
		text = text.replaceAll("<(\".*?\"|'.*?'|[^'\"])*?>", "");		// 残りのタグ除去
		text = text.replaceAll("[ \n]", "");							// &nbsp;と改行除去
		Pattern ptnKagi = Pattern.compile("^「(.+?)」");
		Matcher mchKagi = ptnKagi.matcher(text);
		text = mchKagi.replaceFirst("$1");							// 文頭の「」は消す．他は用例とみなして残す
		return text;
	}
	
	/************************************/
	/********** AbstractMethod **********/
	/************************************/
	abstract String dicName();
	abstract boolean collectIndexURL();
	abstract boolean collectEntryURL();
	abstract String headword(Document document);
	abstract String interpretation(Document document);
}