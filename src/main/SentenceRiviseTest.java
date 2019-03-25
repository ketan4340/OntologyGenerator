package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import grammar.naturalLanguage.NaturalLanguage;
import grammar.sentence.Sentence;
import modules.syntacticParse.SyntacticParser;
import modules.textRevision.SentenceReviser;

public class SentenceRiviseTest {

	public static void main(String[] args) {
		String textFile_str = "resource/input/test/failed.txt";
		List<String> texts;
		try {
			texts = Files.readAllLines(Paths.get(textFile_str));
		} catch (IOException e) {
			e.printStackTrace();
			texts = new ArrayList<>(Arrays.asList("ジェネレータはファイルからテキストを読み込めませんでした。"));
		}
		List<NaturalLanguage> naturalLanguages = NaturalLanguage.toNaturalLanguageList(texts);
		
		Path propPath = Paths.get("resource/prop/CaboCha-property.xml");
		List<Sentence> sentenceList = new SyntacticParser(propPath).parseSentences(naturalLanguages);

		SentenceReviser sr = new SentenceReviser();
		sentenceList.forEach(s -> sr.connectWord(s));
		
		sentenceList.forEach(System.out::println);
		
		
	}
}
