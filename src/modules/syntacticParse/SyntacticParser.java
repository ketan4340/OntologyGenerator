package modules.syntacticParse;

import java.util.List;
import java.util.stream.Collectors;

import grammar.naturalLanguage.NaturalLanguage;
import grammar.naturalLanguage.NaturalParagraph;
import grammar.paragraph.Paragraph;
import grammar.sentence.Sentence;

public class SyntacticParser {

	public SyntacticParser() {
	}
	
	/**
	 * 自然言語文の段落のリストを構文解析し，段落のリストを返す. 未使用
	 * @param naturalLanguageParagraphs 自然言語文の段落のリスト
	 * @return 段落のリスト
	 */
	public List<Paragraph> parseParagraphs(List<NaturalParagraph> naturalLanguageParagraphs) {
		Cabocha cabocha = new Cabocha();
		return naturalLanguageParagraphs.stream()
				.map(NaturalParagraph::getTexts)
				.map(cabocha::texts2sentences)
				.map(Paragraph::new)
				.collect(Collectors.toList());
	}
	/**
	 * 自然言語文のリストを構文解析し，文のリストを返す.
	 * @param naturalLanguages 自然言語文のリスト
	 * @return 文のリスト
	 */
	public List<Sentence> parseSentences(List<NaturalLanguage> naturalLanguages) {
		return new Cabocha().texts2sentences(naturalLanguages);
	}
	

}