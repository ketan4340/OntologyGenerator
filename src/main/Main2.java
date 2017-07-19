package main;

import grammar.NaturalLanguage;
import grammar.Sentence;
import syntacticParse.Parser;

public class Main2 {

	public static void main(String[] args) {
		String text1 = "クジラは哺乳類である。",
				text2 = "カニの味噌汁は美味しいぞ",
				text3 = "葵貝は雌は貝殻を持ち、殻は扁平で直径10〜25センチ、白色で放射状のひだがある。";

		NaturalLanguage nl1 = new NaturalLanguage(text1);
		NaturalLanguage nl2 = new NaturalLanguage(text2);
		NaturalLanguage[] nls = new NaturalLanguage[]{nl1, nl2};
		/*
		Cabocha cabocha = new Cabocha();
		List<String> result = cabocha.executeParser(nls);
		result.forEach(System.out::println);
		 */
		Parser parse = new Parser("cabocha");
		Sentence originalSent = parse.run(text3);

		originalSent.printDep();
	}
}
