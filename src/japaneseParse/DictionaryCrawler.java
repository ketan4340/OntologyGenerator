package japaneseParse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DictionaryCrawler {
	public LinkedHashMap<String,List<String>> dicMap;
	public String onlineDic;
	public String url;
	public String urlHead;
	public String urlTail;
	
	public List<String> documents;
	public static String[] localEntries = {			// 辞書の見出し語群
			"アイアイ",
			"間鴨",
			"藍子",
			"秋沙"
	};
	public static String[] localInterpretations = {	// 辞書の語釈群
			"《その鳴き声から》アイアイ科の原始的な猿。頭胴長40センチくらいで、尾が長い。長い指は鉤爪 (かぎづめ) をもち、樹皮下の昆虫を掘り出して食う。マダガスカル島にのみ生息。指猿 (ゆびざる) 。",
			"アヒルの一品種。マガモと青首アヒルとの雑種。肉用。なきあひる。",
			"スズキ目アイゴ科の海水魚。全長約55センチ。体は楕円形で側扁し、淡褐色の地に小白点がある。背びれ・しりびれ・腹びれのとげに毒がある。本州中部以南の沿岸にすむ。冬季に美味。",
			"《「あきさ」の音変化》カモ科アイサ属の鳥の総称。くちばしは細長く、縁が鋸歯 (きょし) 状。潜水が巧みで、魚を捕食。日本では冬鳥であるが、北海道で繁殖するものもある。ウミアイサ・カワアイサ・ミコアイサの3種がみられる。のこぎりばがも。あいさがも。"
	};
	
	
	public DictionaryCrawler(String dic) {
		dicMap = new LinkedHashMap<String,List<String>>();
		onlineDic = dic;
		switch (onlineDic){
		case "goo":
			urlHead = "http://dictionary.goo.ne.jp/jn/";
			urlTail = "/meaning/m1u/";
			break;
		default:
			System.out.println(onlineDic+"には対応しておりません。");
			break;
		}
	}
	
	public LinkedHashMap<String, List<String>> search(int start, int depth, int interval) throws IOException {
		for(int d=start; d<start+depth; d++) {
			String entry = "entry"+d;
			String interpretation = "interpretation"+d;
			url = urlHead + d + urlTail;
			Document document = Jsoup.connect(url).get();
			//System.out.println(document.html());
			//System.out.println(document.getElementsByTag("strong").html());
			//System.out.println(document.getElementsByClass("in-ttl-b text-indent").html());
			entry = document.getElementsByTag("input").attr("value");
									System.out.print(entry + "\t");
			Iterator<Element> itr1 = document.getElementsByClass("in-ttl-b").iterator();
			while(itr1.hasNext()) {
				Element elm = itr1.next();
				interpretation = cleanText(elm.html().replaceAll("<strong>.*?</strong>", ""));
									System.out.println(interpretation);
			}

			System.out.println(d + "----------------------------------------------------------");
			//System.out.println(entry+"\n  "+interpretation);
			/* 複数の文章からなる語釈を読点で区切ってMap<見出し語,List<語釈>>として登録 */
			List<String> interpretationl = Arrays.asList(interpretation.split("。"));
			dicMap.put(entry, interpretationl);
			
			/* クローラーの礼儀として何秒か間隔をあける */
			try {
				Thread.sleep(interval*1000);	// 1000=1秒
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		return dicMap;
	}
	
	/* 日本語テキストから余計なタグ,スペース,かっこを除去する */
	public String cleanText(String text) {
		String regexTag = "<(\".*?\"|'.*?'|[^'\"])*?>";
		text = text.replaceAll(regexTag, "");		// タグ除去
		//text = text.replaceAll(" | |　", "");		// スペース&nbsp;除去
		text = text.replaceAll("\\s| ", "");			// 空白文字除去
		text = text.replaceAll("\\(.+?\\)", "");	// 半角かっこ除去()
		text = text.replaceAll("\\[.+?\\]", "");	// 半角かっこ除去[]
		text = text.replaceAll("\\《.+?\\》", "");	// 全角かっこ除去《》
		return text;
	}
	
	/* 辞書(Map)を扱いやすいようList<String>にして返す */
	public List<String> getJpnWritings() {
		List<String> writings = new ArrayList<String>();
		
		for(String entry: dicMap.keySet()) {
			List<String> interpretationList = dicMap.get(entry);
			for(String interpretation: interpretationList) {
				String writing = entry+"は"+interpretation;			// *要注意*(雑な日本語文形成)
				writings.add(writing);
			}
		}
		
		return writings;
	}
}
