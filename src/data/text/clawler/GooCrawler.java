package data.text.clawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GooCrawler extends DictionaryClawler{
	
	private static String urlDir = "./resource/input/goo/url/";
	private static String urlFile;
	private static String urlPath;
	private static String dicDir = "./resource/input/goo/dic/";
	private static String dicFile;
	private static String dicPath;
	private static String textDir = "./resource/input/goo/text/";
	private static String textFile;
	private static String textPath;
	private static String urlHead = "http://dictionary.goo.ne.jp/jn/";
	private static String urlTail = "/meaning/m1u/";
	private static String urlCat = "category/";
	private String urlSyl;
	private String idxURL;

	/************************************/
	/**********   Constructor  **********/
	/************************************/
	public GooCrawler(int interval, int depth) {
		super(interval, depth);
	}

	/* 探索，読み込み，文書整形，ファイル出力の一連を行う */
	public void run(int phase, boolean only, String[] cats, String syl) {
		String fileCat = new String();
		for(String cat: cats) {	// ここはgoo辞書向けの処理
			urlCat += cat + "/";
			fileCat += cat + "-";
		}
		urlSyl = syl + "/";
		urlFile = dicName() + "URL" + fileCat + syl;
		urlPath = urlDir + urlFile + expansion;
		dicFile = dicName() + "Dic" + fileCat + syl;
		dicPath = dicDir + dicFile + expansion;
		textFile = dicName() + "Text" + fileCat + syl;
		textPath = textDir + textFile + expansion;
		switch(phase) {
		case 1:			// URL集め
			collectURL();
			if(only==true) break;
		case 2:			// 見出し語，語釈収集
			search();
			if(only==true) break;
		default:
			System.out.println("Finished.");
		}
	}

	/**
	 * カテゴリ中のURLを順に収集する.
	 * 指定カテゴリから五十音順で探索
	 */
	public void collectURL() {
		try {
			File writeFile = new File(urlPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));

			for (int i = 1, sum = 1; sum < depth; i++) {
				String urlPage = i+"/";
				String url = urlHead + urlCat + urlSyl + urlPage;
				System.out.println("\t" + url);

				Connection connection = Jsoup.connect(url);
				connection.timeout(0);
				Document document = connection.get();

				Element elem_list = document.getElementsByClass("list-search-a list-index").first();
				if (elem_list == null) break;
				Elements elems_href = elem_list.select("a[href]");

				Iterator<Element> itr = elems_href.iterator();
				while (itr.hasNext()) {
					if (sum > depth) break;
					Element elm = itr.next();
					String link = elm.attr("abs:href");
					System.out.println(sum + ":" + link);
					bw.write(link);
					bw.newLine();
					sum++;
				}
				bw.flush();

				Thread.sleep(interval*1000);	// 1000=1秒
			}
			bw.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** ファイルに書かれたURLを読み取り探索する.
	 * 見出し語と語釈のMapを作る
	 */
	public void search() {
		int sum = 0;		// 読み込んだ見出し語数のカウンタ
		File readfile = new File(urlPath);
		File writefile = new File(dicPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(readfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(writefile));
			String url = br.readLine();
			for (; url != null; url = br.readLine()){
				sum++;
				String interpretation = new String();

				Connection connection = Jsoup.connect(url);
				connection.timeout(0);
				Document document = connection.get();

				String entry = document.getElementsByTag("input").attr("value");	// 見出し語取得
				System.out.print(sum + ":" + entry + "\t");
				if (entry == null) break;			// 見出し語を得られないような状況ならばループを抜け終了

				Element expElem = document.getElementsByClass("explanation").first();
				if (expElem == null) break;
				Iterator<Element> itr = expElem.getElementsByClass("list-data-b").iterator();
				while (itr.hasNext()) {
					Element elem = itr.next();
					String html = elem.html();
					html = cleanHTML(html);
					interpretation += html;
				}
				System.out.println(interpretation);
				// 見出し語+タブ+語釈(原文)を書き出し
				bw.write(entry + "\t" + interpretation);	// 原文にtabが含まれていると破綻する*要注意*
				bw.newLine();
				bw.flush();

				Thread.sleep(interval*1000);	// 1000=1秒
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	public static void runAll(int o) {
		int depth = 500;
		int interval = 20;
		GooCrawler crw = new GooCrawler(depth, interval);
		String[] categories = {"生物", "動物名"};
		String[] syllabaries = {
				"あ","い","う","え","お",
				"か","き","く","け","こ",
				"さ","し","す","せ","そ",
				"ざ","じ","ず","ぜ","ぞ",
				"た","ち","つ","て","と",
				"だ",		  "で","ど",
				"な","に","ぬ","ね","の",
				"は","ひ","ふ","へ","ほ",
				"ば","び","ぶ","べ","ぼ",
				"ぱ","ぴ","ぷ","ぺ","ぽ",
				"ま","み","む","め","も",
				"や",	 "ゆ",	  "よ",
				"ら","り","る","れ","ろ",
				"わ"
		};

		for (final String syllabary : syllabaries) {
			crw.run(o, false, categories, syllabary);
		}
	}

	/************************************/
	/********** AbstractMethod **********/
	/************************************/
	String dicName() {return "goo";}
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