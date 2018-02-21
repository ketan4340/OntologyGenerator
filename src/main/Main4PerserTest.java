package main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import data.RDF.Ontology;
import grammar.NaturalLanguage;
import modules.Generator;

public class Main4PerserTest {

	public static void main(String[] args) {
		//String readFile = "gooText生物-動物名-All.txt";
		//String readFile = "writing/gooText生物-動物名-お.txt";
		Path textFile = Paths.get("./resource/input/goo/text/gooText生物-動物名-あ2.txt");
		///*

		NaturalLanguage nl1 = new NaturalLanguage("クジラは哺乳類である。");
		//NaturalLanguage nl2 = new NaturalLanguage("カニの味噌汁は美味しいぞ");
		//NaturalLanguage nl3 = new NaturalLanguage("葵貝は雌は貝殻を持ち、殻は扁平で直径10〜25センチ、白色で放射状のひだがある。");
		NaturalLanguage nl4 = new NaturalLanguage("アイアイはアイアイ科の原始的な猿");
		NaturalLanguage[] nls = new NaturalLanguage[]{nl1, nl4};
		List<List<NaturalLanguage>> nlLists = Arrays.asList(Arrays.asList(nls));
		//*/


		///*
		Generator generator = new Generator();
		Ontology ontology = generator.generate(nlLists);
		//Ontology ontology = generator.generate(textFile);
		ontology.getTriples().stream().forEach(System.out::println);
		//*/
	}
}