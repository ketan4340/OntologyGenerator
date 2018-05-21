package modules.syntacticParse;

import java.util.List;
import java.util.stream.Collectors;

import data.id.SentenceIDMap;
import grammar.NaturalLanguage;
import grammar.NaturalParagraph;
import grammar.Paragraph;
import grammar.Sentence;

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
		Cabocha cabocha = new Cabocha();
		return cabocha.texts2sentences(naturalLanguages);
	}
	
	/**
	 * 文のリストにIDTupleを初期化して付与する.
	 * @param sentences
	 * @return 文とIDタプルのリンクトマップ
	 */
	public SentenceIDMap attachIDTuples(List<Sentence> sentences) {
		SentenceIDMap sentenceMap = SentenceIDMap.create(sentences);
		// 全てのIDTuple.scoreを0で初期化
		sentenceMap.scoreAllInit();
		return sentenceMap;
	}
}