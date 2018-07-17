package modules.syntacticParse;

import java.util.List;

import grammar.naturalLanguage.NaturalLanguage;
import grammar.sentence.Sentence;
import parser.Cabocha;

public class SyntacticParser {

	public SyntacticParser() {
	}

	/**
	 * 自然言語文のリストを構文解析し，文のリストを返す.
	 * @param naturalLanguages 自然言語文のリスト
	 * @return 文のリスト
	 */
	public List<Sentence> parseSentences(List<NaturalLanguage> naturalLanguages) {
		Cabocha cabocha = new Cabocha();	//new Cabocha("-f1", "-n1", "-d", "/usr/local/lib/mecab/dic/mecab-ipadic-neologd");
		List<String> parseResult = cabocha.parse(NaturalLanguage.toStringList(naturalLanguages));
		return new CabochaDecoder().decodeProcessOutput(parseResult);
	}

}