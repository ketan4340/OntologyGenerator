package syntacticParse;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import grammar.Clause;
import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.Word;

public class Cabocha extends AbstractProcessManager implements ParserInterface{
	/* CaboChaの基本実行コマンド */
	private static final List<String> command4mac = new LinkedList<String>(Arrays.asList("/usr/local/bin/cabocha"));
	private static final List<String> command4windows = new LinkedList<String>(Arrays.asList("cmd", "/c", "cabocha"));
	/* CaboChaのオプション */
	private static final String opt_Lattice			= "-f1"; 		// 格子状に並べて出力
	//private static final String opt_XML				= "-f3";			// XML形式で出力
	//private static final String opt_nonNE			= "-n0";			// 固有表現解析を行わない
	private static final String opt_NE_Constraint	= "-n1";			// 文節の整合性を保ちつつ固有表現解析を行う
	//private static final String opt_NE_noConstraint	= "-n2";			// 文節の整合性を保たずに固有表現解析を行う
	private static final String opt_output2File		= "--output=";	// CaboChaの結果をファイルに書き出す


	/* parserの入力ファイル，出力ファイルの保存先 */
	private static final Path inputFilePath = Paths.get("parserIO/parserInput.txt");
	private static final Path outputFilePath = Paths.get("parserIO/parserOutput.txt");

	/* 読み込み時，文節ごとの係り受け関係をインデックスで保管するMap */
	// 都度clearして使い回す
	Map<Clause, Integer> dependingMap = new HashMap<>();
	
	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	/* デフォルトではオプション(-f1,-n1)でセッティング */
	public Cabocha() {
		this(opt_Lattice, opt_NE_Constraint);
	}
	/* オプションをリストで渡すことも可能 */
	public Cabocha(String... options) {
		command = (PlatformUtil.isMac()) ?		new LinkedList<String>(command4mac)
				: (PlatformUtil.isWindows()) ?	new LinkedList<String>(command4windows)
				: null;		// mac, windows以外のOSは実装予定なし
		command.addAll(Arrays.asList(options));
	}


	/********************************************/
	/** AbstractProcessManagerの抽象メソッドの実装 **/
	/********************************************/
	// nothing
	
	/*******************************************/
	/***** ParserInterfaceの抽象メソッドの実装 *****/
	/*******************************************/
	@Override
	public Sentence text2sentence(NaturalLanguage nlText){
		List<String> parseOutput = parse(nlText);
		return decode2Sentence(parseOutput);
	}
	@Override
	public List<Sentence> texts2sentences(List<NaturalLanguage> nlTextList){
		List<String> parseOutput;
		
		int inputSize = nlTextList.size();
		// サイズが1の時は，内部で同名メソッド(NL)を呼ぶ
		// サイズが2以上の時は，ファイルに出力してから同名メソッド(Path)を呼ぶ
		switch (inputSize) {
		case 0:		// 入力テキスト数:0
			System.out.println("The number of text is "+inputSize+".");
			parseOutput = emptyInput();
		case 1:		// 入力テキスト数:1
			System.out.println("The number of text is "+inputSize+".");
			parseOutput = parse(nlTextList.get(0));
		default:		// 入力テキスト数:2以上
			System.out.println("The number of text is "+inputSize+".");
			Path textFile = output_ParserInput(nlTextList);	// 一旦ファイルに出力
			parseOutput = parse(textFile);					// そのファイルを入力として解析
			//parseOutput = passContinualArguments(nlList);
		}
		return decodeProcessOutput(parseOutput);
	}
	@Override
	public List<Sentence> texts2sentences(NaturalLanguage[] nlTexts){
		return texts2sentences(Arrays.asList(nlTexts));
	}
	@Override
	public List<Sentence> texts2sentences(Path inputFilePath){
		List<String> parseOutput = parse(inputFilePath);
		return decodeProcessOutput(parseOutput);
	}
	
		
	@Override
	public List<String> parse(NaturalLanguage nlText) {
		startProcess(command);									// プロセス開始
		writeInput2Process(nlText.toString());					// 入力待ちプロセスにテキスト入力
		List<String> result = readProcessResult();				// 結果を読み込む
		finishProcess();											// プロセス終了
		return result;
	}
	@Override
	public List<String> parse(List<NaturalLanguage> nlList) {
		int inputSize = nlList.size();
		// サイズが1の時は，内部で同名メソッド(NL)を呼ぶ
		// サイズが2以上の時は，ファイルに出力してから同名メソッド(Path)を呼ぶ
		switch (inputSize) {
		case 0:		// 入力テキスト数:0
			System.out.println("The number of text is "+inputSize+".");
			return emptyInput();
		case 1:		// 入力テキスト数:1
			System.out.println("The number of text is "+inputSize+".");
			return parse(nlList.get(0));
		default:	// 入力テキスト数:2以上
			System.out.println("The number of text is "+inputSize+".");
			Path path = output_ParserInput(nlList);	// 一旦ファイルに出力
			return parse(path);						// そのファイルを入力として解析
			//return passContinualArguments(nlList);
		}
	}
	@Override
	public List<String> parse(NaturalLanguage[] nlTexts) {	// 配列の場合
		return parse(Arrays.asList(nlTexts));	// リストにして同名メソッドに投げる
	}
	@Override
	public List<String> parse(Path inputFilePath) {
		// CaboChaの入力も出力もファイルになるよう，コマンドを用意
		command.add(inputFilePath.toString());					// 入力テキストのファイル名
		command.add(opt_output2File + outputFilePath.toString());	// ファイルに出力するコマンドを追加
		startProcess(command);									// プロセス開始
		finishProcess();											// プロセス終了
		try {
			return Files.readAllLines(outputFilePath, UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	
	/**
	 * プロセスに繰り返し入力し, 出力をまとめて得る. 多分,数が多いと使えない.
	 * @param nlList
	 * @return 解析結果の文字列リスト
	 */
	public List<String> passContinualArguments(List<NaturalLanguage> nlList) {
		startProcess(command);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), UTF8)));
		nlList.forEach(nl -> pw.println(nl.toString()));
		pw.close();
		List<String> result = readProcessResult();		// 結果を読み込む
		finishProcess();								// プロセス終了
		return result;
	}

	

	@Override
	public List<Sentence> decodeProcessOutput(List<String> parsedInfo4all) {
		List<List<String>> sentenceInfoList = StringListUtil.split("\\AEOS\\z", parsedInfo4all);	// "EOS"ごとに分割. EOSの行はここで消える.
		List<Sentence> sentences = sentenceInfoList.stream()
				.map(sentenceInfo -> decode2Sentence(sentenceInfo))
				.collect(Collectors.toList());
		return sentences;
	}
	@Override
	public Sentence decode2Sentence(List<String> parsedInfo4sentence) {
		dependingMap.clear();
		List<List<String>> clauseInfoList = StringListUtil.splitStartWith("\\A(\\* ).*", parsedInfo4sentence);	// "* "ごとに分割	
		List<Clause> clauses = clauseInfoList.stream()
				.map(clauseInfo -> decode2Clause(clauseInfo))
				.collect(Collectors.toList());
		return new Sentence(clauses, dependingMap);
	}
	@Override
	public Clause decode2Clause(List<String> parsedInfo4clause) {
		// 一要素目は文節に関する情報
		// ex) * 0 -1D 0/1 0.000000...
		String clauseInfo = parsedInfo4clause.get(0);							// "* 0 -1D 0/1 0.000000..."
		String[] clauseInfos = clauseInfo.split(" ");							// "*","0","-1D","0/1","0.000000..."
		String dep_str = clauseInfos[2];											// 係り先の情報. ex) "2D","-1D"
		int depIndex = Integer.decode(dep_str.substring(0, dep_str.length()-1));	// -1で'D'の部分を除去. ex) 2,-1
		int isSbjIndex = Integer.decode(clauseInfos[3].split("/")[0]);			// 主辞の番号. ex)"0/1"の"0"部分

		// 残りは単語に関する情報
		List<List<String>> wordInfoList = parsedInfo4clause.subList(1, parsedInfo4clause.size())
				.stream().map(info -> Arrays.asList(info)).collect(Collectors.toList());

		List<Word> words = wordInfoList.stream()
				.map(wordInfo -> decode2Word(wordInfo))
				.collect(Collectors.toList());
		Clause clause = new Clause(words, depIndex, isSbjIndex);
		dependingMap.put(clause, depIndex);
		return clause;
	}
	@Override
	public Word decode2Word(List<String> parsedInfo4word) {
		// 一応Listで受け取るものの，きっと1行だけしかない．よってget(0)
		String[] wordInfo = parsedInfo4word.get(0).split("\t");
		List<String> tags;
		try {
			tags = Arrays.asList(wordInfo[1].split(","));
		} catch (Exception e) {
			e.printStackTrace();
			tags = new ArrayList<>();
		}
		return new Word(wordInfo[0], tags);
	}
	

	/*******************************************/
	/********** Cabocha専用メソッドの実装 *********/
	/*******************************************/

	/** 入力したいテキスト(List<NL>)を一旦ファイル(parserInput)に出力 **/
	/* List<NL>のサイズが2以上ならこれらを呼び出し、executeParser(Path)に渡される */
	private static Path output_ParserInput(List<NaturalLanguage> nlTextList) {
		// List<NL>からList<String>へ
		try {
			return Files.write(inputFilePath, NaturalLanguage.toStringList(nlTextList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}