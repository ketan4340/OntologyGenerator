package syntacticParse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import grammar.Clause;
import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.Word;

public class Cabocha extends AbstractProcessManager implements ParserInterface{
	/** CaboChaの基本実行コマンド **/
	private static final List<String> command4mac = new LinkedList<String>(Arrays.asList("/usr/local/bin/cabocha"));
	private static final List<String> command4windows = new LinkedList<String>(Arrays.asList("cmd", "/c", "cabocha"));
	/** CaboChaのオプション **/
	private static final String opt_Lattice = "-f1"; 			// 格子状に並べて出力
	private static final String opt_XML = "-f3";				// XML形式で出力
	private static final String opt_nonNE = "-n0";				// 固有表現解析を行わない
	private static final String opt_NE_Constraint = "-n1";		// 文節の整合性を保ちつつ固有表現解析を行う
	private static final String opt_NE_noConstraint = "-n2";	// 文節の整合性を保たずに固有表現解析を行う
	private static final String opt_output2File = "--output=";	// CaboChaの結果をファイルに書き出す

	/* parserの入力ファイル，出力ファイルの保存先 */
	private static final Path inputFilePath = Paths.get("parserIO/parserInput.txt");
	private static final Path outputFilePath = Paths.get("parserIO/parserOutput.txt");

	/* 外部プロセスを起動するコマンド */
	private static List<String> command;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public Cabocha() {
		// デフォルトではオプション(-f1,-n1)でセッティング
		this(Arrays.asList(opt_Lattice, opt_NE_Constraint));
	}
	public Cabocha(List<String> options) {
		command = (PlatformUtil.isMac()) ?		new LinkedList<String>(command4mac)
				: (PlatformUtil.isWindows()) ?	new LinkedList<String>(command4windows)
				: null;		// mac, windows以外のOSは実装予定なし
		command.addAll(options);
	}


	/********************************************/
	/** AbstractProcessManagerの抽象メソッドの実装 **/
	/********************************************/

	/*******************************************/
	/***** ParserInterfaceの抽象メソッドの実装 *****/
	/*******************************************/
	@Override
	public List<String> executeParser(Path nlTextFilePath) {
		// CaboChaの入力も出力もファイルになるよう，コマンドを用意
		command.add(nlTextFilePath.toString());					// 入力をファイルから受け取る
		command.add(opt_output2File+outputFilePath.toString());	// ファイルに出力する
		startProcess(command);
		List<String> result = new LinkedList<String>();
		try (Stream<String> stream = Files.lines(nlTextFilePath, UTF8)) {
		  stream.forEach(line -> result.add(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public List<String> executeParser(NaturalLanguage nlText) {
		startProcess(command);
		writeInput2Process(nlText.toString());	// 入力待ちプロセスにテキスト入力
		return readProcessResult();				// 結果を読み込んで返す
	}
	@Override
	public List<String> executeParser(List<NaturalLanguage> nlTextList) {
		int inputSize = nlTextList.size();
		switch (inputSize) {
		case 0:		// 入力文0
			return inputEmpty();
		case 1:		// 1文のみ
			return executeParser(nlTextList.get(0));
		default:	// 2文以上
			Path path = output_ParserInput(nlTextList);	// 一旦ファイルに出力
			return executeParser(path);					// そのファイルを入力として解析
		}
	}
	@Override
	public List<String> executeParser(NaturalLanguage[] nlTexts) {
		return executeParser(Arrays.asList(nlTexts));	// リストにして同名メソッドに投げる
	}


	@Override
	public List<Sentence> readProcessOutput(List<String> outputList) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	@Override
	public Sentence createSentence(String output, List<Clause> clauseList) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	@Override
	public Clause createClause(String output, List<Word> wordList) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	@Override
	public Word createWord(String output) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


	/*******************************************/
	/********** Cabocha専用メソッドの実装 **********/
	/*******************************************/

	/* 入力されたList<NL>が空だった場合の処理 */
	private static List<String> inputEmpty() {
		System.err.println("Input List is Empty!!!");
		return new ArrayList<String>();
	}

	/** 入力するテキスト(List<NL> or NL[])を一旦ファイル(parserInput)に出力 **/
	/* List<NL>,NL[]のサイズが2以上ならこれらを呼び出し、executeParser(Path)に渡される */
	private Path output_ParserInput(List<NaturalLanguage> nlTextList) {
		// List<NL>からList<String>へ
		try {
			return Files.write(inputFilePath, NaturalLanguage.toStringList(nlTextList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private Path output_ParserInput(NaturalLanguage[] nlTexts) {
		return output_ParserInput(Arrays.asList(nlTexts));	// Listにして上記の同名メソッドへ
	}
}
