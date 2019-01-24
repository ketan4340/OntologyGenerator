package modules.syntacticParse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import grammar.clause.Clause;
import grammar.clause.SingleClause;
import grammar.morpheme.Morpheme;
import grammar.morpheme.MorphemeFactory;
import grammar.sentence.DependencyMap;
import grammar.sentence.Sentence;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.NamedEntityTag;
import grammar.word.Word;
import language.pos.CabochaTags;
import language.pos.TagsFactory;
import util.StringListUtil;

public class CabochaDecoder {

	private static final int MAXIMUM_TAGS_LENGTH = 9;

	private final Set<Indexes> dependingIndexSet = new HashSet<>();

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

	private Sentence decode2Sentence(List<String> parsedInfo4sentence) {
		List<List<String>> clauseInfoList = StringListUtil.splitStartWith("\\A(\\* ).*", parsedInfo4sentence);	// "* "ごとに分割
		List<Clause<?>> clauses = clauseInfoList.stream()
				.map(clauseInfo -> decode2Clause(clauseInfo))
				.collect(Collectors.toList());
		DependencyMap dm = compileDependencyMap(clauses);
		dependingIndexSet.clear();
		Sentence s = new Sentence(clauses);
		s.initDependency(dm);
		return s;
	}

	private SingleClause decode2Clause(List<String> parsedInfo4clause) {
		//// 一要素目は文節に関する情報
		// ex) * 0 -1D 0/1 0.000000...
		Indexes cabochaClauseIndexes = cabochaClauseIndexes(parsedInfo4clause.get(0));
		int subjEndIndex = cabochaClauseIndexes.subjectEndIndex+1;	// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = cabochaClauseIndexes.functionEndIndex+1;	// 機能語の終端の番号. ex) "0/1"の"1"部分
		// List#sublistは返り値のサブリストに引数endIndex番目の要素は含まれないので1を足しておく
		
		//// 残り(Index1以降)は単語に関する情報
		// CaboChaは形態素の情報は一行(Stringで十分)だが，ParserInterface(を実装するKNP)に合わせてList<String>とする．
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
		dependingIndexSet.add(cabochaClauseIndexes);
		return new SingleClause(headWord, functionWords, otherWords);
	}

	private Word decode2Word(List<List<String>> parsedInfo4word) {
		// 一つの単語が複数の形態素からなる場合もあるのでListで渡される
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return new Word(morphemes);
	}
	private Categorem decode2Categorem(List<List<String>> parsedInfo4word) {
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		Categorem c = new Categorem(morphemes);
		// 固有表現タグは自立語の一番目の形態素に含まれているという仮定に基づくget(0)
		String[] morphemeInfos = parsedInfo4word.get(0).get(0).split("\t");
		String netag_str = morphemeInfos.length>3 ? morphemeInfos[2] : null;
		c.setNETag(NETagOf(netag_str));
		return c;
	}
	private Adjunct decode2Adjunct(List<List<String>> parsedInfo4word) {
		List<Morpheme> morphemes = parsedInfo4word.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return new Adjunct(morphemes);
	}

	private Morpheme decode2Morpheme(List<String> parsedInfo4morpheme) {
		// CaboChaの場合は形態素の情報は必ず一行なのでget(0)
		String[] morphemeInfos = parsedInfo4morpheme.get(0).split("\t");
		String name = morphemeInfos[0];
		String[] tagArray = morphemeInfos[1].split(",");
		CabochaTags tags = supplyPoSIfSingleByteChar(tagArray, name);
		return MorphemeFactory.getInstance().getMorpheme(name, tags);
	}
	
	/* ================================================== */
	/* ==========    Cabocha専用メソッドの実装    ========== */
	/* ================================================== */
	private DependencyMap compileDependencyMap(List<Clause<?>> clauses) {
		return dependingIndexSet.stream().collect(
				Collectors.toMap(
						idxs -> clauses.get(idxs.thisIndex),
						idxs -> idxs.dependIndex==-1 ? SingleClause.ROOT : clauses.get(idxs.dependIndex), 
						(k1, k2) -> k1,
						DependencyMap::new));
	}
	private CabochaTags supplyPoSIfSingleByteChar(String[] tagArray, String infinitive) {
		TagsFactory factory = TagsFactory.getInstance();
		if (tagArray.length < MAXIMUM_TAGS_LENGTH)	// sizeが9未満．つまり半角文字
			return factory.getCabochaTags(tagArray[0], tagArray[1], tagArray[2], tagArray[3], tagArray[4], tagArray[5], infinitive, infinitive, infinitive);
		return factory.getCabochaTags(tagArray[0], tagArray[1], tagArray[2], tagArray[3], tagArray[4], tagArray[5], tagArray[6], tagArray[7], tagArray[8]);
	}

	/**
	 * CaboCha特有の文節に関するIndex情報3つをまとめた配列を返す.
	 * @param cabochaClauseInfo CaboChaの文節に関する出力
	 * @return Indexをまとめた長さ3の配列
	 */
	private Indexes cabochaClauseIndexes(String cabochaClauseInfo) {
		String[] clauseInfos = cabochaClauseInfo.split(" ");				// "*","0","-1D","0/1","0.000000..."
		int thisIndex = Integer.decode(clauseInfos[1]);
		int depIndex = Integer.decode(clauseInfos[2].substring(0, clauseInfos[2].length()-1));	// -1で'D'を除去.
		String[] subjFuncIndexes = clauseInfos[3].split("/");				// ex) "0/1"->("0","1")
		int subjEndIndex = Integer.decode(subjFuncIndexes[0]);				// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = Integer.decode(subjFuncIndexes[1]);				// 機能語の終端の番号. ex) "0/1"の"1"部分
		Indexes indexes = new Indexes(thisIndex, depIndex, subjEndIndex, funcEndIndex);
		return indexes;
	}
	private class Indexes {
		protected final int thisIndex; 
		protected final int dependIndex;
		protected final int subjectEndIndex;
		protected final int functionEndIndex;
		public Indexes(int thisIndex, int dependIndex, int subjectEndIndex, int functionEndIndex) {
			this.thisIndex = thisIndex;
			this.dependIndex = dependIndex;
			this.subjectEndIndex = subjectEndIndex;
			this.functionEndIndex = functionEndIndex;
		}
	}
	
	private NamedEntityTag NETagOf(String netag_str) {
		if (Objects.isNull(netag_str) || netag_str.equals("O"))
			return null;
		netag_str = netag_str.substring(2);
		switch (netag_str) {
			case "OPTIONAL": return NamedEntityTag.OPTIONAL;
			case "ORGANIZATION": return NamedEntityTag.ORGANIZATION;
			case "PERSON": return NamedEntityTag.PERSON;
			case "LOCATION": return NamedEntityTag.LOCATION;
			case "ARTIFACT": return NamedEntityTag.ARTIFACT;
			case "DATE": return NamedEntityTag.DATE;
			case "TIME": return NamedEntityTag.TIME;
			case "MONEY": return NamedEntityTag.MONEY;
			case "PERCENT": return NamedEntityTag.PERCENT;
			default: return null;
		}
	}

}