package japaneseParse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Crawler {
	public String dictionaly;
	public String url;
	public String urlHead;
	public String urlTail;
	
	public List<String> documents;
	
	public Crawler(String dic) {
		dictionaly = dic;
		switch (dictionaly){
		case "goo":
			urlHead = "http://dictionary.goo.ne.jp/jn/";
			urlTail = "/meaning/m1u/";
			break;
		case "knp":
			System.out.println("KNPは未実装です。");
			break;
		default:
			System.out.println(dictionaly+"には対応しておりません。");
			break;
		}
	}
	
	public List<String> search(int start, int depth) throws IOException {
		List<String> sentences = new ArrayList<String>();
		
		for(int i=0; i<depth; i++) {
			url = urlHead + i + urlTail;
			//System.out.println(url);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Document document = Jsoup.connect(url).get();
			System.out.println(document.html());
			//System.out.println(document.getElementsByTag("strong").html());
			//System.out.println(document.getElementsByClass("in-ttl-b text-indent").html());
			String title = document.getElementsByTag("input").attr("value");
			System.out.println(title);
			
			List<String> entries = new ArrayList<String>();
			for(ListIterator<Element> itr = document.getElementsByClass("in-ttl-b text-indent").listIterator(); itr.hasNext(); ) {
				Element e = itr.next();
				String s = e.html();
				String regex = "<(\".*?\"|'.*?'|[^'\"])*?>.*?</.*?>";
				s = s.replaceAll(regex, "");
				s = s.replaceAll(" ", "");
				s = title+"は"+s;					// *要注意*(雑な日本語文形成)
				entries.add(s);
				System.out.println(s);
			}
			sentences.addAll(entries);
		}
		return sentences;
	}
}
