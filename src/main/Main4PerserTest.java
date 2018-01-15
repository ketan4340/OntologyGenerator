package main;

import java.nio.file.Path;
import java.nio.file.Paths;

import data.original.Ontology;
import modules.Generator;

public class Main4PerserTest {

	public static void main(String[] args) {
		//String readFile = "gooText生物-動物名-All.txt";
		//String readFile = "writings/gooText生物-動物名-お.txt";
		Path textFile = Paths.get("./writings/gooText生物-動物名-さ.txt");
		/*
		String text1 = "クジラは哺乳類である。";
		String text2 = "カニの味噌汁は美味しいぞ";
		String text3 = "葵貝は雌は貝殻を持ち、殻は扁平で直径10〜25センチ、白色で放射状のひだがある。";
		NaturalLanguage nl1 = new NaturalLanguage(text1);
		NaturalLanguage nl2 = new NaturalLanguage(text2);
		NaturalLanguage nl3 = new NaturalLanguage(text3);
		NaturalLanguage[] nls = new NaturalLanguage[]{nl1, nl2, nl3};
		 */
		/*
		Cabocha cabocha = new Cabocha();
	
		List<Sentence> sents = cabocha.texts2sentences(textFile);
		sents.forEach(s -> s.printDep());
		 */
		
		Generator generator = new Generator();
		Ontology ontology = generator.generate(textFile);
				
		ontology.getTriples().forEach(System.out::println);
	}
}