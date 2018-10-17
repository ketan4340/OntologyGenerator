package modules.syntacticParse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dic.CabochaTags;
import grammar.clause.Clause;
import grammar.clause.SingleClause;
import grammar.morpheme.Morpheme;
import grammar.sentence.Sentence;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;
import util.StringListUtil;

public class CabochaDecoder {

	private static final int MAXIMUM_TAGS_LENGTH = 9;

	private Map<Clause<?>, Integer> dependingMap = new HashMap<>();

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public CabochaDecoder() {
	}


	/* ================================================== */
	/* ================= Member Method ================== */
	/* ================================================== */
	public List<Sentence> decodeProcessOutput(List<String> parsedInfo4all) {
		List<List<String>> sentenceInfoList = StringListUtil.split("\\AEOS\\z", parsedInfo4all);	// "EOS"ごとに分割. EOSの行はここで消える.
		List<Sentence> sentences = sentenceInfoList.stream()
				.map(sentenceInfo -> decode2Sentence(sentenceInfo))
				.collect(Collectors.toList());
		return sentences;
	}

	public Sentence decode2Sentence(List<String> parsedInfo4sentence) {
		dependingMap.clear();
		List<List<String>> clauseInfoList = StringListUtil.splitStartWith("\\A(\\* ).*", parsedInfo4sentence);	// "* "ごとに分割
		List<Clause<?>> clauses = clauseInfoList.stream()
				.map(clauseInfo -> decode2Clause(clauseInfo))
				.collect(Collectors.toList());
		return new Sentence(clauses, dependingMap);
	}

	public SingleClause decode2Clause(List<String> parsedInfo4clause) {
		//// 一要素目は文節に関する情報
		// ex) * 0 -1D 0/1 0.000000...
		Indexes cabochaClauseIndexes = cabochaClauseIndexes(parsedInfo4clause.get(0));
		int depIndex = cabochaClauseIndexes.dependIndex;			// 係る先の文節の番号. ex) "-1D"の"-1"部分
		int subjEndIndex = cabochaClauseIndexes.subjectEndIndex;	// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = cabochaClauseIndexes.functionEndIndex;	// 機能語の終端の番号. ex) "0/1"の"1"部分

		//// 残り(Index1以降)は単語に関する情報
		// CaboChaは形態素の情報は一行 (本来Stringで十分)だが，ParserInterface(を実装するKNP)に合わせてList<String>とする．
		List<List<String>> wordInfoLists = parsedInfo4clause.subList(1, parsedInfo4clause.size())
				.stream().map(info -> Arrays.asList(info)).collect(Collectors.toList());

		Categorem headWord = decode2Categorem(wordInfoLists.subList(0, subjEndIndex));
		List<Adjunct> functionWords = wordInfoLists.subList(subjEndIndex, funcEndIndex)
				.stream()
				.map(wordInfo -> decode2Adjunct(Arrays.asList(wordInfo)))
				.collect(Collectors.toList());
		List<Word> otherWords = wordInfoLists.subList(funcEndIndex, wordInfoLists.size())
				.stream()
				.map(wordInfo -> decode2Word(Arrays.asList(wordInfo)))
				.collect(Collectors.toList());

		SingleClause clause = new SingleClause(headWord, functionWords, otherWords);
		dependingMap.put(clause, depIndex);
		return clause;
	}

	public Word decode2Word(List<List<String>> parsedInfo4word) {
		// 一つの単語が複数の形態素からなる場合もあるのでListで渡される
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return new Word(morphemes);
	}
	public Categorem decode2Categorem(List<List<String>> parsedInfo4word) {
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return new Categorem(morphemes);
	}
	public Adjunct decode2Adjunct(List<List<String>> parsedInfo4word) {
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return new Adjunct(morphemes);
	}

	public Morpheme decode2Morpheme(List<String> parsedInfo4morpheme) {
		// CaboChaの場合は形態素の情報は必ず一行なのでget(0)
		String[] morphemeInfos = parsedInfo4morpheme.get(0).split("\t");
		String name = morphemeInfos[0];
		String[] tagArray = morphemeInfos[1].split(",");
		CabochaTags tags = getTagsSuppliedSingleByteChar(tagArray, name);
		return Morpheme.getInstance(name, tags);
	}


	/* ================================================== */
	/* ==========    Cabocha専用メソッドの実装    ========== */
	/* ================================================== */
	private CabochaTags getTagsSuppliedSingleByteChar(String[] tagArray, String infinitive) {
		if (tagArray.length < MAXIMUM_TAGS_LENGTH)	// sizeが9未満．つまり半角文字
			return CabochaTags.getInstance(tagArray[0], tagArray[1], tagArray[2], tagArray[3], tagArray[4], tagArray[5], infinitive, infinitive, infinitive);
		return CabochaTags.getInstance(tagArray[0], tagArray[1], tagArray[2], tagArray[3], tagArray[4], tagArray[5], tagArray[6], tagArray[7], tagArray[8]);
	}

	/**
	 * CaboCha特有の文節に関するIndex情報3つをまとめた配列を返す.
	 * @param cabochaClauseInfo CaboChaの文節に関する出力
	 * @return Indexをまとめた長さ3の配列
	 */
	private Indexes cabochaClauseIndexes(String cabochaClauseInfo) {
		String[] clauseInfos = cabochaClauseInfo.split(" ");				// "*","0","-1D","0/1","0.000000..."
		int depIndex = Integer.decode(clauseInfos[2].substring(0, clauseInfos[2].length()-1));	// -1で'D'を除去.
		String[] subjFuncIndexes = clauseInfos[3].split("/");				// ex) "0/1"->("0","1")
		int subjEndIndex = Integer.decode(subjFuncIndexes[0])+1;			// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = Integer.decode(subjFuncIndexes[1])+1;			// 機能語の終端の番号. ex) "0/1"の"1"部分
		Indexes indexes = new Indexes(depIndex, subjEndIndex, funcEndIndex);
		return indexes;
	}
	private class Indexes {
		protected int dependIndex;
		protected int subjectEndIndex;
		protected int functionEndIndex;
		public Indexes(int dependIndex, int subjectEndIndex, int functionEndIndex) {
			this.dependIndex = dependIndex;
			this.subjectEndIndex = subjectEndIndex;
			this.functionEndIndex = functionEndIndex;
		}
	}

}