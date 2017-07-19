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
import java.util.LinkedList;
import java.util.List;

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

	private static final String BORDER = "EOS";

	/* parserの入力ファイル，出力ファイルの保存先 */
	private static final Path inputFilePath = Paths.get("parserIO/parserInput.txt");
	private static final Path outputFilePath = Paths.get("parserIO/parserOutput.txt");


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	/* デフォルトではオプション(-f1,-n1)でセッティング */
	public Cabocha() {
		this(Arrays.asList(opt_Lattice, opt_NE_Constraint));
	}
	/* オプションをリストで渡すことも可能 */
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
	public List<String> executeParser(Path inputFilePath) {
		// CaboChaの入力も出力もファイルになるよう，コマンドを用意
		command.add(inputFilePath.toString());					// 入力をファイルから受け取る
		command.add(opt_output2File+outputFilePath.toString());	// Cabochaのオプションを使いファイルに出力する
		startProcess(command);									// プロセス開始
		finishProcess();										// プロセス終了
		try {
			return Files.readAllLines(outputFilePath, UTF8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<String> executeParser(NaturalLanguage nlText) {
		startProcess(command);
		writeInput2Process(nlText.toString());		// 入力待ちプロセスにテキスト入力
		List<String> result = readProcessResult();	// 結果を読み込む
		finishProcess();							// プロセス終了
		return result;
	}
	@Override
	public List<String> executeParser(List<NaturalLanguage> nlList) {
		int inputSize = nlList.size();
		// サイズが1の時は，内部で同名メソッド(NL)を呼ぶ
		// サイズが2以上の時は，ファイルに出力してから同名メソッド(Path)を呼ぶ
		switch (inputSize) {
		case 0:		// 入力テキスト数:0
			System.out.println("The number of text is "+inputSize+".");
			return emptyInput();
		case 1:		// 入力テキスト数:1
			System.out.println("The number of text is "+inputSize+".");
			return executeParser(nlList.get(0));
		default:	// 入力テキスト数:2以上
			System.out.println("The number of text is "+inputSize+".");
			Path path = output_ParserInput(nlList);	// 一旦ファイルに出力
			return executeParser(path);					// そのファイルを入力として解析
			//return passContinualArguments(nlList);
		}
	}
	@Override
	public List<String> executeParser(NaturalLanguage[] nlTexts) {	// 配列の場合
		return executeParser(Arrays.asList(nlTexts));	// リストにして同名メソッドに投げる
	}
	/* プロセスに繰り返し入力し，出力をまとめて得る */
	// 多分,数が多いと使えない
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
	public List<Sentence> readProcessOutput(List<String> parsedInfo4all) {
		List<Sentence> sentences = new ArrayList<>();
		List<List<String>> sentenceInfoList = StringListUtil.splitStringList(BORDER, true, parsedInfo4all);

		sentenceInfoList.stream().forEach(sentenceInfo -> sentences.add(createSentence(sentenceInfo)));
		return sentences;
	}
	@Override
	public Sentence createSentence(List<String> parsedInfo4sentence) {
		List<Clause> clauses = new ArrayList<>();
		List<List<String>> clauseInfoList = StringListUtil.splitStringList(BORDER, true, parsedInfo4sentence);
		clauseInfoList.stream().forEach(clauseInfo -> clauses.add(createClause(clauseInfo)));

		Clause clause = null;
		List<Word> wdl = null;
		List<Integer> clauseList = new LinkedList<Integer>();
		Clause depto = null;	// clauseの係り先
		int border = 0;			// あるclauseの主たる単語が何番目かを示す
		boolean isSubject;		// 主辞か機能語か
		int nextID = 0;

		for (String line : parsedInfo4sentence) {
			if(line.startsWith("EOS")) {		// EOSがきたら終了
				if(wdl == null) {				// 初手EOSだった場合、文章が正しく渡されていない
					return null;
				}else {
					clause.setClause(wdl);
					clause.setDepending(depto);
				}
				clauseList.add(clause.id);

			}else if(line.startsWith("* ")) {	// * で始まる場合，直前までのClauseを閉じ、新しいClauseを用意
				if(wdl != null) {				// 最初は直前までのClauseが存在しないので回避
					clause.setClause(wdl);
					clause.setDepending(depto);
					clauseList.add(clause.id);
				}else {
					nextID = Clause.clauseSum;	// *要注意というか汚い*
				}
				wdl = new ArrayList<>();
				clause = new Clause();
				String[] clauseInfo = line.split(" ");
				String dep_str = clauseInfo[2];
				int deptoID = Integer.decode(dep_str.substring(0, dep_str.length()-1));
				if(deptoID!=-1) deptoID += nextID;	// *要注意(上に同じ)*
				depto = Clause.get(deptoID);
				String[] border_str = clauseInfo[3].split("/");
				border = Integer.decode(border_str[0]);
			}else {								// 他は単語の登録
				String[] wordInfo = line.split("\t");
				isSubject = (wdl.size() <= border)? true: false;
				Word word = new Word();
				word.setWord(wordInfo[0], Arrays.asList(wordInfo[1].split(",")), clause.id, isSubject);
				wdl.add(word);
			}
		}

		// clauseの係り受け関係を更新
		Clause.updateAllDependency();

		return new Sentence(clauses);
	}
	@Override
	public Clause createClause(List<String> parsedInfo4clause) {
		List<Word> words = new ArrayList<>();
		List<List<String>> wordInfoList = StringListUtil.splitStringList(BORDER, true, parsedInfo4clause);
		wordInfoList.stream().forEach(wordInfo -> words.add(createWord(wordInfo)));
		return new Clause(words);
	}
	@Override
	public Word createWord(List<String> parsedInfo4word) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


	/*******************************************/
	/********** Cabocha専用メソッドの実装 **********/
	/*******************************************/

	/** 入力するテキスト(List<NL> or NL[])を一旦ファイル(parserInput)に出力 **/
	/* List<NL>,NL[]のサイズが2以上ならこれらを呼び出し、executeParser(Path)に渡される */
	private static Path output_ParserInput(List<NaturalLanguage> nlTextList) {
		// List<NL>からList<String>へ
		try {
			return Files.write(inputFilePath, NaturalLanguage.toStringList(nlTextList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Path output_ParserInput(NaturalLanguage[] nlTexts) {
		return output_ParserInput(Arrays.asList(nlTexts));	// Listにして上記の同名メソッドへ
	}


}
