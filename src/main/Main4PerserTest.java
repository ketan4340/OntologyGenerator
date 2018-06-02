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
		String textFileName = args.length == 1?
				args[0] :
				"./resource/input/goo/text/gooText生物-動物名-あ.txt";
		//textFileName = "./test/literalText.txt";
		Path textFilePath = Paths.get(textFileName);

		String[] texts = {
			"クジラは哺乳類である。",
			"カニの味噌汁は美味しいぞ",
			"アイアイはアイアイ科の原始的な猿",
			"ミュウは南アメリカに分布",
			"馬は体長1メートルほど。",
			"藍鮫はアイザメ科の海水魚の総称。"
		};
		List<NaturalLanguage> nlLists = Arrays.asList( NaturalLanguage.toNaturalLanguageArray(texts));

		///*
		Generator generator = new Generator();
		//Ontology ontology = generator.generate(nlLists);
		Ontology ontology = generator.generate(textFilePath);
		ontology.getTriples().stream().limit(20).forEach(System.out::println);
		//*/
	}
}