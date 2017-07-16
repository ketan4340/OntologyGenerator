package main;

import java.util.ArrayList;
import java.util.List;

import grammar.NaturalLanguage;
import syntacticParse.Cabocha;
import syntacticParse.ParserInterface;

public class Main2 {

	public static void main(String[] args) {
		String text1 = "吾輩は猫である。",
				text2 = "カニの味噌汁は美味しいぞ。";

		NaturalLanguage nl1 = new NaturalLanguage(text1);
		NaturalLanguage nl2 = new NaturalLanguage(text2);
		NaturalLanguage[] nls = new NaturalLanguage[]{nl1, nl2};

		Cabocha cabocha = new Cabocha();
		List<String> result = cabocha.executeParser(nls);

		//result.forEach(System.out::println);

		for (List<String> list : ParserInterface.splitResultList("EOS", result)) {
			System.out.println(list.get(0) + "~" + list.get(list.size()-1));
		}
	}
}
