package syntacticParse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import grammar.Clause;
import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.Word;

public interface ParserInterface {
	/*******************************/
	/********** 解析器実行部 **********/
	/*******************************/

	/*** 自然言語文をParserに通し，出力結果をListに保管 ***/
	/** 入力: Path or NaturalLanguage or List<NaturalLanguage> **/
	/** 出力: List<String> **/	// Listの1要素=出力の1行
	/* 入力がテキストファイル */
	List<String> executeParser(Path nlTextFilePath);
	/* 入力が1文のみ */
	List<String> executeParser(NaturalLanguage nlText);
	/* 入力がList */
	List<String> executeParser(List<NaturalLanguage> nlTextList);
	/* 入力が配列 */
	List<String> executeParser(NaturalLanguage[] nlTexts);

	/* 入力されたList<NL>が空だった場合の処理 */
	default List<String> emptyInput() {
		System.err.println("Input List is Empty!!!");
		return new ArrayList<String>();
	}


	/*******************************/
	/******** 解析結果階層化部 ********/
	/*******************************/
	/*** 出力(List<String>)を分解 ***/
	List<Sentence> readProcessOutput(List<String> outputList);

	/** Sentence(Clause1(Word1,Word2,..),Clause2(),Clause3(),...)の構成に変換 **/
	Sentence createSentence(List<String> output);
	Clause createClause(List<String> output);
	Word createWord(String output);
}