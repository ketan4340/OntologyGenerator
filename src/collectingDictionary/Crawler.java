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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	public static void main(String[] args) {
		int depth = 500;
		int interval = 25;
		Crawler crw = new Crawler("goo", depth, interval);

		String[] categories = {"生物", "動物名"}; 
		String syllabary = "さ";
		crw.run(3, false, categories, syllabary);
		
		//Crawler.gatheringTexts("writings", "gooText生物-動物名-All.txt");
	}
	
	public String onlineDic;	// どの辞書を探索するかのスイッチ(現状gooのみ)
	public int depth;			// 何項目まで探索するか
	public int interval;		// クローリングのスリープ間隔
	
	public String urlHead;
	public String urlTail;
	public String urlCat;
	public String urlSyl;
	public String idxURL;
	
	public String urlDir = "urls/";
	public String urlFile;
	public String urlPath;	
	public String dicDir = "dics/";
	public String dicFile;
	public String dicPath;
	public String textDir = "writings/";
	public String textFile;
	public String textPath;
	public String expansion = ".txt";

	public Crawler(String dic, int dep, int itv) {
		onlineDic = dic;
		depth = dep;
		interval = itv;
		
		switch(onlineDic) {
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
	public void run(int phase, boolean only, String[] cats, String syl) {
		String fileCat = new String();
		for(String cat: cats) {	// ここはgoo辞書向けの処理
			urlCat += cat + "/";
			fileCat += cat + "-";
		}
		urlSyl = syl + "/";
		urlFile = onlineDic + "URL" + fileCat + syl;
		urlPath = urlDir + urlFile + expansion;	
		dicFile = onlineDic + "Dic" + fileCat + syl;
		dicPath = dicDir + dicFile + expansion; 
		textFile = onlineDic + "Text" + fileCat + syl;
		textPath = textDir + textFile + expansion; 
		switch(phase) {
		case 1:			// URL集め
			collectURL();
			if(only==true) break;
		case 2:			// 見出し語，語釈収集
			search();
			if(only==true) break;
		case 3:			// 語釈整形
			saveJpnWritings();
			if(only==true) break;
		default:
			System.out.println("Finished.");
		}
	}
	
	/* 単純に全単語から五十音順で探索 */
	/* goo辞書のみ有効 */
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
	public void collectURL() {
		try {
			File writefile = new File(urlPath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(writefile));
			
			int sum = 1;
			for(int i = 1; sum < depth; i++) {
				String urlPage = i+"/";
				String url = urlHead + urlCat + urlSyl + urlPage;
				System.out.println("\t" + url);
				
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
			
				Thread.sleep(interval*1000);	// 1000=1秒
			}	
			bw.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* ファイルに書かれたURLを読み取り探索する */
	/* 見出し語と語釈のMapを作る */
	public void search() {
		int sum = 0;		// 読み込んだ見出し語数のカウンタ
		File readfile = new File(urlPath);
		File writefile = new File(dicPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(readfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(writefile));
			String url = br.readLine();
			for(; url != null; url = br.readLine()){
				sum++;
				String entry = new String();
				String interpretation = new String();
				
				Connection connection = Jsoup.connect(url);
				connection.timeout(0);
				Document document = connection.get();
				
				entry = document.getElementsByTag("input").attr("value");	// 見出し語取得
				System.out.print(sum + ":" + entry + "\t");
				if(entry == null) break;			// 見出し語を得られないような状況ならばループを抜け終了
				
				Element expElem = document.getElementsByClass("explanation").first();
				if(expElem == null) break;
				Iterator<Element> itr = expElem.getElementsByClass("list-data-b").iterator();
				while(itr.hasNext()) {
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

	/* HTMLテキストから余計なタグ,スペース,かっこを除去する */
	public String cleanHTML(String text) {
		//text = text.replaceAll("<strong>[0-9０-９]+</strong>", "");	// 箇条書きの全角数字とスペース除去
		text = text.replaceAll("<(\".*?\"|'.*?'|[^'\"])*?>", "");	// 残りのタグ除去
		text = text.replaceAll("[ \n]", "");						// &nbsp;と改行除去
		Pattern ptnKagi = Pattern.compile("^「(.+?)」");
		Matcher mchKagi = ptnKagi.matcher(text);
		text = mchKagi.replaceFirst("$1");			// 文頭の「」は消す．他は用例とみなして残す
		return text;
	}
	
	/* 日本語テキストから余計なかっこ,記号を除去し，見出し語の代入を行う */
	public String cleanText1(String text, String entry) {
		text = text.replaceAll("\\(.+?\\)", "");	// 半角かっこ()除去
		//text = text.replaceAll("\\[.+?\\]", "");	// 半角かっこ[]除去
		text = text.replaceAll("（.+?）", "");		// 全角かっこ（）除去
		text = text.replaceAll("［.+?］", "");		// 全角かっこ［］除去
		text = text.replaceAll("〈.+?〉", "");		// 全角かっこ〈〉除去
		text = text.replaceAll("《.+?》", "");		// 全角かっこ《》除去
		text = text.replaceAll("―", entry);			// 例文の―を見出し語に置き換える
		//text = text.replaceAll("[㋐-㋾]+", "");		// 囲み文字(カタカナ)除去
		text = text.replaceAll("→|⇒", "");			// 矢印除去
		return text;
	}
	
	/* テキストからスペースを除去する */
	public String cleanText2(String text) {
		text = text.replaceAll("\\[.+?\\]", "");	// 半角かっこ[]除去
		//text = text.replaceAll("[「」]", "");		// 全角鉤かっこ「」除去
		text = text.replaceAll("[\\s　]", "");		// 空白文字除去
		return text;
	}
	
	/* 辞書を扱いやすいよう主語を加えた文章にして出力 */
	public void saveJpnWritings() {
		File readfile = new File(dicPath);
		File writefile = new File(textPath);

		// 繰り返しつかうのでここでコンパイル
		Pattern ptnExm1 = Pattern.compile("「(.+?)／(.+?)」");			// 鉤括弧で囲まれた用例を探す正規表現
		Pattern ptnExm2 = Pattern.compile("「(.+?)」(?![あ-ん])");		// 鉤括弧で囲まれた用例を探す正規表現
		Pattern ptnNum = Pattern.compile("[１-９\\d{2}][ ㋐-㋾]");		// 語釈文頭の箇条書きの数字を探す正規表現
		Pattern ptnSplm = Pattern.compile("\\[補説\\].+");				// 補説とそこから行末までを探す正規表現
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(readfile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(writefile));
			String line = br.readLine();
			for(; line != null; line = br.readLine()) {
				String[] item = line.split("\t", 2);
				String entry = item[0];
				String serialInterpretation = item[1];
				serialInterpretation = cleanText1(serialInterpretation, entry);	// 余計なかっこを消す
				Matcher mchExm1 = ptnExm1.matcher(serialInterpretation);
				serialInterpretation = mchExm1.replaceAll("");	// 「用例」を消す
				Matcher mchExm2 = ptnExm2.matcher(serialInterpretation);
				serialInterpretation = mchExm2.replaceAll("");	// 「用例」を消す
				Matcher mchNum = ptnNum.matcher(serialInterpretation);
				serialInterpretation = mchNum.replaceAll("");	// 語釈文頭の箇条書きの数字を消す
				Matcher mchSplm = ptnSplm.matcher(serialInterpretation);
				serialInterpretation = mchSplm.replaceAll("");	// 文末の補説を消す
				serialInterpretation = cleanText2(serialInterpretation);		// 残しておいたスペースを消す

				String[] interpretations = serialInterpretation.split("。", 0);
				for(String interpretation: interpretations) {
					String writing = entry+"は"+interpretation;			// *要注意*(雑な日本語文形成)
					System.out.println(writing);
					bw.write(writing);
					bw.newLine();
				}
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/* ディレクトリ内のファイルの内容を全て纏めた一つのファイルを出力する */
	public static void gatheringTexts(String dirName, String opFileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(opFileName));
		
			File dir = new File(dirName);
			File[] files = dir.listFiles();
			if( files == null )
				System.out.println("file is null.");
			for(File file: files) {
				if(!file.exists()) {
					continue;
				}else if(file.isFile()) {
			        BufferedReader br = new BufferedReader(new FileReader(file));
			        String line = br.readLine();
			        while(line != null) {
			        	bw.write(line);
			        	bw.newLine();
			        	line = br.readLine();
			        }
			        br.close();
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
