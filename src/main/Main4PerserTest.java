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
		Path textFile = Paths.get("./resource/input/goo/text2/gooText生物-動物名-え2.txt");
		///*

		String[] texts = {
			"クジラは哺乳類である。",
			/*
			"カニの味噌汁は美味しいぞ",
			"アイアイはアイアイ科の原始的な猿",
			"ミュウは南アメリカに分布",
			"馬は体長1メートルほど。"
			*/
		};
		NaturalLanguage[] nls = NaturalLanguage.toNaturalLanguageArray(texts);
		List<List<NaturalLanguage>> nlLists = Arrays.asList(Arrays.asList(nls));
		//*/


		///*
		Generator generator = new Generator();
		//Ontology ontology = generator.generate(nlLists);
		Ontology ontology = generator.generate(textFile);
		ontology.getTriples().stream().forEach(System.out::println);
		//*/
	}
}