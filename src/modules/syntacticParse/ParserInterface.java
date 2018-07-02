package modules.syntacticParse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import grammar.clause.SingleClause;
import grammar.concept.Concept;
import grammar.morpheme.Morpheme;
import grammar.naturalLanguage.NaturalLanguage;
import grammar.sentence.Sentence;
import grammar.word.Word;

public interface ParserInterface {
	/******************************************/
	/********** 解析器・階層化呼び出し部 **********/
	/******************************************/
	/** 入力が自然言語文1文のみ */
	public Sentence text2sentence(NaturalLanguage nlText);
	/** 入力が自然言語文のList */
	public List<Sentence> texts2sentences(List<NaturalLanguage> nlTextList);
	/** 入力がテキストファイル */
	public List<Sentence> texts2sentences(Path nlTextFilePath);
	
	
	/******************************************/
	/**********      解析器実行部      **********/
	/******************************************/

	/*** 自然言語文をParserに通し，出力結果をListに保管 ***/
	/** 入力: Path or NaturalLanguage or List<NaturalLanguage> **/
	/** 出力: List<String> **/	// Listの1要素=出力の1行
	/** 入力が自然言語文1文のみ */
	public List<String> parse(NaturalLanguage nlText);
	/** 入力が自然言語文のList */
	public List<String> parse(List<NaturalLanguage> nlTextList);
	/** 入力がテキストファイル */
	public List<String> parse(Path nlTextFilePath);

	/** 入力されたList<NL>が空だった場合の処理 */
	public default List<String> emptyInput() {
		System.err.println("Input List is Empty!!!");
		return new ArrayList<>();
	}


	/******************************************/
	/**********     解析結果階層化部    **********/
	/******************************************/
	/*** 出力(List<String>)を分解 ***/
	public List<Sentence> decodeProcessOutput(List<String> parseResult4all);

	/** Sentence1(Clause1(Word1,Word2,..),Clause2(Word,Word,..),Clause3(..),...)の構成に変換 **/
	public Sentence decode2Sentence(List<String> parseResult4sentence);
	public SingleClause decode2Clause(List<String> parseResult4clause);
	public Word decode2Word(List<List<String>> parseResult4word);			// 複数の形態素からなる場合を考慮
	public Concept decode2Concept(List<List<String>> parseResult4concept);	// Wordに同じ
	public Morpheme decode2Morpheme(List<String> parseResult4morpheme);		// 形態素の情報が複数行の場合を考慮
}