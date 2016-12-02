package collectingDictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	public LinkedHashMap<String,List<String>> dicMap;
	public List<String> writings;
	
	public String onlineDic;	// どの辞書を探索するかのスイッチ(現状gooのみ)
	public int depth;
	public int interval;
	
	public String urlHead;
	public String urlTail;
	public String urlCat;
	public String urlSyl;
	public String idxURL;
	
	public String textDir = "writings/";
	public String textFile;
	public String textPath;
	public String urlDir = "urls/";
	public String urlFile;
	public String urlPath;
	public String expansion = ".txt";

	public Crawler(String dic, int dep, int itv) {
		dicMap = new LinkedHashMap<String,List<String>>();
		writings = new ArrayList<String>();
		onlineDic = dic;
		depth = dep;
		interval = itv;
		
		switch (onlineDic){
		case "goo":
			urlHead = "http://dictionary.goo.ne.jp/jn/";
			urlTail = "/meaning/m1u/";
			urlCat = "category/";
			break;
		case "weblio":
			urlHead = "http://www.weblio.jp/";
			urlCat = "cat/";
			System.out.println(onlineDic+"は未対応です。");
			break;
		default:
			System.out.println(onlineDic+"には対応しておりません。");
			break;
		}
	}
	
	/* 探索，読み込み，文書整形，ファイル出力の一連を行う */
	public void runAll(String[] cats, String syl) {
		String fileCat = "";
		for(String cat: cats) {
			urlCat += cat + "/";
			fileCat += cat + "-";
		}
		urlSyl = syl + "/";
		urlFile = onlineDic + "URL" + fileCat + syl;
		urlPath = urlDir + urlFile + expansion;
		textFile = onlineDic + "Text" + fileCat + syl;
		textPath = textDir + textFile + expansion; 
		collectURL(cats, syl);
		search();
		setJpnWritings();
		saveTextFile();
	}
	
	public void run(String[] cats, String syl) {
		String fileCat = "";
		for(String cat: cats) {
			urlCat += cat + "/";
			fileCat += cat + "-";
		}
		urlSyl = syl + "/";
		urlFile = onlineDic + "URL" + fileCat + syl;
		urlPath = urlDir + urlFile + expansion;
		textFile = onlineDic + "Text" + fileCat + syl;
		textPath = textDir + textFile + expansion; 
		search();
		setJpnWritings();
		saveTextFile();
	}
	
	/* カテゴリ中のURLを順に収集する */
	/* 単純に全単語から五十音順で探索 */
	public void collectURL(int start) {
		File file = new File(urlPath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(int d=0; d<depth; d++) {
				int dicnum = d + start;			
				String url = urlHead + dicnum + urlTail;
				bw.write(url);
				bw.newLine();				
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* カテゴリ中のURLを順に収集する */
	/* 指定カテゴリから五十音順で探索 */
	public void collectURL(String[] cats, String syl) {
		try {
			File file = new File(urlPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			int sum = 1;
			for(int i = 1; sum < depth; i++) {
				String urlPage = i+"/";
				String url = urlHead + urlCat + urlSyl + urlPage;
				System.out.println("\t" + url);
				/* クローラーの礼儀として何秒か間隔をあける */
				Thread.sleep(interval*1000);	// 1000=1秒
				Connection connection = Jsoup.connect(url);
				connection.timeout(0);
				Document document = connection.get();
				
				Element elem_list = document.getElementsByClass("list-search-a list-index").first();
				if(elem_list == null) break;
				Elements elems_href = elem_list.select("a[href]");
				
				Iterator<Element> itr = elems_href.iterator();
				while(itr.hasNext()) {
					if(sum > depth) break;
					Element elm = itr.next();
					String link = elm.attr("abs:href");
					System.out.println(sum + ":" + link);
					bw.write(link);
					bw.newLine();
					sum++;
				}
				bw.flush();
			}	
			bw.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* ファイルに書かれたURLを読み取り探索する */
	/* 見出し語と語釈のMapを作る */
	public void search() {
		int sum = 0;
		File file = new File(urlPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String url = br.readLine();
			while(url != null){
				sum++;
				/* クローラーの礼儀として何秒か間隔をあける */
				Thread.sleep(interval*1000);	// 1000=1秒
				Connection connection = Jsoup.connect(url);
				connection.timeout(0);
				Document document = connection.get();
				String entry = "entry" + url;						// デフォルトの見出し語
				String interpretation = "";		// デフォルトの語釈
				// 何らかの原因でデータを得られなければこれらデフォルト文字列がそのまま出力される
				entry = document.getElementsByTag("input").attr("value");	// 見出し語取得
				System.out.print(sum + ":" + entry + "\t");
				if(entry == null) break;			// 見出し語を得られないような状況ならばループを抜け終了
				
				Element expElem = document.getElementsByClass("explanation").first();
				if(expElem == null) break;
				Iterator<Element> itr = expElem.getElementsByClass("list-data-b").iterator();
				while(itr.hasNext()) {
					Element elem = itr.next();
					interpretation += cleanText(elem.text());	// 数字+スペース形式の箇条書きを消す
				}
				System.out.println(interpretation);
				/* 複数の文章からなる語釈を読点で区切ってMap<見出し語,List<語釈>>として登録 */
				List<String> interpretationl = Arrays.asList(interpretation.split("。"));
				dicMap.put(entry, interpretationl);
				url = br.readLine();
			}
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* 日本語テキストから余計なタグ,スペース,かっこを除去する */
	public String cleanText(String text) {
		String regexTag = "<(\".*?\"|'.*?'|[^'\"])*?>";
		text = text.replaceAll(regexTag, "");		// タグ除去
		text = text.replaceAll("[０-９]+ ", "");		// 箇条書きの全角数字とスペース除去
		text = text.replaceAll(" ", "");			// 特殊なスペース&nbsp;除去
		text = text.replaceAll("\\s| ", "");		// 空白文字除去
		text = text.replaceAll("\\(.+?\\)", "");	// 半角かっこ除去()
		text = text.replaceAll("\\[.+?\\]", "");	// 半角かっこ除去[]
		text = text.replaceAll("\\《.+?\\》", "");	// 全角かっこ除去《》
		return text;
	}
	
	/* 辞書(Map)を扱いやすいようList<String>にして返す */
	public void setJpnWritings() {
		for(String entry: dicMap.keySet()) {
			List<String> interpretationList = dicMap.get(entry);
			for(String interpretation: interpretationList) {
				String writing = entry+"は"+interpretation;			// *要注意*(雑な日本語文形成)
				writings.add(writing);
			}
		}
	}
		
	public void saveTextFile() {
		System.out.println("---------------------------------------------------------------");
		File file = new File(textPath);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for(String writing: writings) {
				System.out.println(writing);
				bw.write(writing);
				bw.newLine();
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
