package data.text;

import java.nio.file.Paths;

import data.text.clawler.GooCrawler;

public class Main4CollectText {


	public static void main(String[] args) {
		int depth = 500;
		int interval = 10;
		GooCrawler crw = new GooCrawler(depth, interval);
		/*
		String[] categories = {"生物", "動物名"};
		String syllabary = "あ";
		crw.run(1, false, categories, syllabary);

		//Crawler.runAll(3);
		//Crawler.gatheringTexts("writings", "gooText生物-動物名-All.txt");
		 */
		
		DictionaryEditor de = new DictionaryEditor();
		/*
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
		for (String syl : syllabaries) {
			Path dictionaryFile = Paths.get("resource/input/goo/dic/gooDic生物-動物名-"+syl+".txt");
			Path outputFile = Paths.get("resource/input/goo/text2/gooText生物-動物名-"+syl+"2.txt");
			de.dictionary2Text(dictionaryFile, outputFile);
		}
		*/
		
		de.gatheringTexts(Paths.get("resource/input/goo/text2"), 
				Paths.get("resource/input/goo/text2/gooText生物-動物名-All2.txt"));

	}
}
