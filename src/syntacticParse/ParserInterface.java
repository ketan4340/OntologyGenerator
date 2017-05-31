package syntacticParse;

import java.nio.file.Path;
import java.util.List;

import grammar.Clause;
import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.Word;

public interface ParserInterface {
	/*** 自然言語文をParserに通し，出力結果をListに保管 ***/
	/** 入力: Path or NaturalLanguage or List<NaturalLanguage> **/
	/** 出力: List<String> **/	// Listの1要素=出力の1行
	/* 入力がテキストファイル */
	List<String> executeParser(Path nlTextFilePath);
	/* 入力が1文のみ */
	List<String> executeParser(NaturalLanguage nlText);
	/* 入力がList, 配列 */
	// サイズが1の時は，内部で同名メソッド(NL)を呼ぶ
	// サイズが2以上の時は，ファイルに出力してから同名メソッド(Path)を呼ぶ
	List<String> executeParser(List<NaturalLanguage> nlTextList);
	List<String> executeParser(NaturalLanguage[] nlTexts);


	/*** 出力(List<String>)を分解 ***/
	List<Sentence> readProcessOutput(List<String> outputList);

	/** Sentence(Clause1(Word1,Word2,..),Clause2(),Clause3(),...)の構成に変換 **/
	Sentence createSentence(String output, List<Clause> clauseList);
	Clause createClause(String output, List<Word> wordList);
	Word createWord(String output);
}