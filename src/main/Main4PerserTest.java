package main;

import java.util.List;

import grammar.NaturalLanguage;
import grammar.Sentence;
import syntacticParse.Cabocha;
import syntacticParse.StringListUtil;

public class Main4PerserTest {

	public static void main(String[] args) {
		String text1 = "クジラは哺乳類である。",
				text2 = "カニの味噌汁は美味しいぞ",
				text3 = "葵貝は雌は貝殻を持ち、殻は扁平で直径10〜25センチ、白色で放射状のひだがある。";

		NaturalLanguage nl1 = new NaturalLanguage(text1);
		NaturalLanguage nl2 = new NaturalLanguage(text2);
		NaturalLanguage nl3 = new NaturalLanguage(text3);
		NaturalLanguage[] nls = new NaturalLanguage[]{nl1, nl2, nl3};

		//Parser parser = new Parser(Parser.CABOCHA);
		//Sentence originalSent = parser.parse(text3);
		
		Cabocha cabocha = new Cabocha();
	
		List<Sentence> sents = cabocha.texts2sentences(nls);
		
		sents.forEach(s -> s.printDep());
		sents.forEach(s -> s.printC());
		sents.forEach(s -> s.printSF());
	}
}
