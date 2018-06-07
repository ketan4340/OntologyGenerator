package modules.syntacticParse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import grammar.Concept;
import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.clause.AbstractClause;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;
import util.StringListUtil;

public class Cabocha extends AbstractProcessManager implements ParserInterface{
	/* CaboChaの基本実行コマンド */
	private static final List<String> COMMAND4MACOS = new LinkedList<String>(Arrays.asList("/usr/local/bin/cabocha"));
	private static final List<String> COMMAND4WINDOWS = new LinkedList<String>(Arrays.asList("cmd", "/c", "cabocha"));
	/* CaboChaのオプション */
	private static final String OPTION_LATTICE				= "-f1"; 		// 格子状に並べて出力
	//private static final String OPTION_XML				= "-f3";		// XML形式で出力
	//private static final String OPTION_NO_NE				= "-n0";		// 固有表現解析を行わない
	private static final String OPTION_NE_CONSTRAINT		= "-n1";		// 文節の整合性を保ちつつ固有表現解析を行う
	//private static final String OPTION_NE_UNCONSTRAINT	= "-n2";		// 文節の整合性を保たずに固有表現解析を行う
	private static final String OPTION_OUTPUT2FILE			= "--output=";	// CaboChaの結果をファイルに書き出す


	/* parserの入力ファイル，出力ファイルの保存先 */
	private static final Path PATH_INPUT = Paths.get("tmp/parserIO/CaboChaInput.txt");
	private static final Path PATH_OUTPUT = Paths.get("tmp/parserIO/CaboChaOutput.txt");

	/** 読み込み時，文節ごとの係り受け関係をインデックスで保管するMap.
	 * 都度clearして使い回す.
	 */
	Map<AbstractClause<?>, Integer> dependingMap = new HashMap<>();
	
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	/* デフォルトではオプション(-f1,-n1)でセッティング */
	public Cabocha() {
		this(OPTION_LATTICE, OPTION_NE_CONSTRAINT);
	}
	/* オプションをリストで渡すことも可能 */
	public Cabocha(String... options) {
		command = PlatformUtil.isMac() ?		COMMAND4MACOS
				: PlatformUtil.isWindows() ?	COMMAND4WINDOWS
				: null;		// mac, windows以外のOSは実装予定なし
		command.addAll(Arrays.asList(options));
	}

	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/

	/********************************************/
	/** AbstractProcessManagerの抽象メソッドの実装 **/
	/********************************************/
	// nothing
	
	/*******************************************/
	/***** ParserInterfaceの抽象メソッドの実装 *****/
	/*******************************************/
	/********************************/
	/***** 解析器・階層化呼び出し部 *****/
	/********************************/
	@Override
	public Sentence text2sentence(NaturalLanguage nlText){
		List<String> parseOutput = parse(nlText);
		return decodeProcessOutput(parseOutput).get(0);
	}
	@Override
	public List<Sentence> texts2sentences(List<NaturalLanguage> nlTextList){
		List<String> parseOutput;
		
		System.out.println("The number of text is "+nlTextList.size()+".");
		// サイズが1の時は，内部で同名メソッド(NL)を呼ぶ
		// サイズが2以上の時は，ファイルに出力してから同名メソッド(Path)を呼ぶ
		switch (nlTextList.size()) {
		case 0:		// 入力テキスト数: 0
			parseOutput = emptyInput();
			break;
		case 1:		// 入力テキスト数: 1
			parseOutput = parse(nlTextList.get(0));
			break;
		default:		// 入力テキスト数: 2以上
			Path textFile = output_ParserInput(nlTextList);	// 一旦ファイルに出力
			parseOutput = parse(textFile);					// そのファイルを入力として解析
			break;
		}
		return decodeProcessOutput(parseOutput);
	}
	@Override
	public List<Sentence> texts2sentences(Path inputFilePath){
		List<String> parseOutput = parse(inputFilePath);
		return decodeProcessOutput(parseOutput);
	}
	
	
	/********************************/
	/********** 解析器実行部 **********/
	/********************************/
	public List<String> parse(NaturalLanguage nlText) {
		startProcess(command);									// プロセス開始
		writeInput2Process(nlText.toString());					// 入力待ちプロセスにテキスト入力
		List<String> result = readProcessResult();				// 結果を読み込む
		finishProcess();											// プロセス終了
		return result;
	}
	public List<String> parse(List<NaturalLanguage> nlList) {
		Path path = output_ParserInput(nlList);	// 一旦ファイルに出力
		return parse(path);						// そのファイルを入力として解析
	}
	public List<String> parse(Path inputFilePath) {
		// CaboChaの入力も出力もファイルになるよう，コマンドを用意
		command.add(inputFilePath.toString());						// 入力テキストのファイル名
		command.add(OPTION_OUTPUT2FILE + PATH_OUTPUT.toString());	// ファイルに出力するコマンドを追加
		startProcess(command);								// プロセス開始
		finishProcess();									// プロセス終了
		try {
			return Files.readAllLines(PATH_OUTPUT, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	
	/**
	 * プロセスへの入力を繰り返し出力をまとめて得る. 多分，数が多いと使えない.
	 * @param nlList
	 * @return 解析結果の文字列リスト
	 */
	public List<String> passContinualArguments(List<NaturalLanguage> nlList) {
		startProcess(command);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)));
		nlList.forEach(nl -> pw.println(nl.toString()));
		pw.close();
		List<String> result = readProcessResult();		// 結果を読み込む
		finishProcess();								// プロセス終了
		return result;
	}

	
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
		List<AbstractClause<?>> clauses = clauseInfoList.stream()
				.map(clauseInfo -> decode2Clause(clauseInfo))
				.map(clause -> (Clause) clause)
				.collect(Collectors.toList());
		return new Sentence(clauses, dependingMap);
	}
	public Clause decode2Clause(List<String> parsedInfo4clause) {
		//// 一要素目は文節に関する情報
		// ex) * 0 -1D 0/1 0.000000...
		int[] cabochaClauseIndexes = cabochaClauseIndexes(parsedInfo4clause.get(0));
		int depIndex = cabochaClauseIndexes[0];		// 係る先の文節の番号. ex) "-1D"の"-1"部分
		int subjEndIndex = cabochaClauseIndexes[1];	// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = cabochaClauseIndexes[2];	// 機能語の終端の番号. ex) "0/1"の"1"部分
		
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
		
		Clause clause = new Clause(headWord, functionWords, otherWords);
		dependingMap.put(clause, depIndex);
		return clause;
	}
	public Word decode2Word(List<List<String>> parsedInfo4word) {
		// 一つの単語が複数の形態素からなる場合もあるのでListで渡される
		Concept concept = decode2Concept(parsedInfo4word);
		return new Word(concept);
	}
	public Categorem decode2Categorem(List<List<String>> parsedInfo4word) {
		Concept concept = decode2Concept(parsedInfo4word);
		return new Categorem(concept);
	}
	public Adjunct decode2Adjunct(List<List<String>> parsedInfo4word) {
		Concept concept = decode2Concept(parsedInfo4word);
		return new Adjunct(concept);
	}
	public Concept decode2Concept(List<List<String>> parsedInfo4concept) {
		List<Morpheme> morphemes = parsedInfo4concept.stream()
				.map(morphemeInfo -> decode2Morpheme(morphemeInfo))
				.collect(Collectors.toList());
		return Concept.getOrNewInstance(morphemes);
	}
	public Morpheme decode2Morpheme(List<String> parsedInfo4morpheme) {
		// CaboChaの場合は形態素の情報は必ず一行なのでget(0)
		String[] morphemeInfos = parsedInfo4morpheme.get(0).split("\t");
		String name = morphemeInfos[0];
		List<String> tags = Arrays.asList(morphemeInfos[1].split(","));
		if (Objects.equals(tags.get(6), "*"))	// CaboChaの半角は7番目が原形になっていないので補完
			tags.set(6, name);
		return Morpheme.getOrNewInstance(name, tags);
	}


	/*******************************************/
	/********** Cabocha専用メソッドの実装 *********/
	/*******************************************/
	
	/**
	 * CaboCha特有の文節に関するIndex情報3つをまとめた配列を返す.
	 * @param cabochaClauseInfo CaboChaの文節に関する出力
	 * @return Indexをまとめた長さ3の配列
	 */
	private int[] cabochaClauseIndexes(String cabochaClauseInfo) {
		int[] indexes = new int[3];
		String[] clauseInfos = cabochaClauseInfo.split(" ");				// "*","0","-1D","0/1","0.000000..."
		int depIndex = Integer.decode(clauseInfos[2].substring(0, clauseInfos[2].length()-1));	// -1で'D'を除去.
		String[] subjFuncIndexes = clauseInfos[3].split("/");				// ex) "0/1"->("0","1")
		int subjEndIndex = Integer.decode(subjFuncIndexes[0])+1;			// 主辞の終端の番号. ex) "0/1"の"0"部分
		int funcEndIndex = Integer.decode(subjFuncIndexes[1])+1;			// 機能語の終端の番号. ex) "0/1"の"1"部分
		indexes[0] = depIndex;	indexes[1] = subjEndIndex;	indexes[2] = funcEndIndex;
		return indexes;
	}
	
	/** 入力したいテキスト(List<NL>)を一旦ファイル(parserInput)に出力 **/
	/* List<NL>のサイズが2以上ならこれらを呼び出し、executeParser(Path)に渡される */
	private static Path output_ParserInput(List<NaturalLanguage> nlTextList) {
		// List<NL>からList<String>へ
		try {
			return Files.write(PATH_INPUT, NaturalLanguage.toStringList(nlTextList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}