package data.text;

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
		de.gatheringTexts("resource/input/goo/text2", "gooText生物-動物名-All2.txt");

	}
}
