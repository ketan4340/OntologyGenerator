package syntacticParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import grammar.Clause;
import grammar.Sentence;
import grammar.Word;

public class Parser {
	public static final int CABOCHA = 1;
	public static final int KNP = 2;
	public static final int STANFORD = 3;
	

	private int tool; // どの解析器を使うか(stanford,cabocha,knp)

	public Parser(int tool) {
		this.tool = tool;
	}

	public Sentence parse(String text) {
		switch (tool){
		case CABOCHA:
			return runCabocha(text);
		case KNP:
			System.out.println("KNPは未実装です。");
			return null;
		default:
			System.out.println(tool+"には対応しておりません。");
			return null;
		}
	}

	// 一文だけ渡してもらって解析
	private Sentence runCabocha(String text) {
		Clause clause = null;
		List<Word> wordList = null;
		List<Clause> clauseList = new LinkedList<>();
		//Clause depto = null;	// clauseの係り先のID
		int deptoIndex = -1;
		Map<Clause, Integer> dependingMap = new HashMap<>();
		int border = 0;			// あるclauseの主辞が何番目かを示す
		boolean sbj_fnc;		//

		try {
			//UTF-8のBOMを除去するための準備←textファイルから読み込む場合を考慮
			byte [] bytes = {-17, -69, -65};
			String btmp= new String(bytes, "UTF-8");
			//BOM除去
			text=text.replaceAll(btmp, "");

			//cabochaの実行開始　lattice形式で出力(-f1の部分で決定、詳しくはcabochaのhelp参照)
			ProcessBuilder pb =
					// MacOSの場合
					(PlatformUtil.isMac()) ? new ProcessBuilder("/usr/local/bin/cabocha", "-f1", "-n1")
					// Windowsの場合
					: (PlatformUtil.isWindows()) ? new ProcessBuilder("cmd", "/c", "cabocha", "-f1", "-n1")
					// 他は実装予定なし
					: null;

			Process process = pb.start();

			//実行途中で文字列を入力(コマンドプロンプトでテキストを入力する操作に相当)
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
			osw.write(text);
			osw.close();

			//出力結果を読み込む
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));

			//出力結果に格納するための文字列を用意
			String line = new String();
			while ((line = br.readLine()) != null) {
				if(line.startsWith("EOS")) {		// EOSがきたら終了
					if(wordList == null) {				// 初手EOSだった場合、文章が正しく渡されていない
						return null;
					}else {
						clause.setWords(wordList);
					}
					clauseList.add(clause);

				}else if(line.startsWith("* ")) {	// * で始まる場合，直前までのClauseを閉じ、新しいClauseを用意
					if(wordList != null) {				// 最初は直前までのClauseが存在しないので回避
						clause.setWords(wordList);
						clauseList.add(clause);
					}
					/*
					else {
						nextID = Clause.clauseSum;	// *要注意というか汚い*
					}
					 */
					wordList = new ArrayList<Word>(4);
					clause = new Clause(new ArrayList<Word>(), -1);
					String[] clauseInfo = line.split(" ");
					String dep_str = clauseInfo[2];
					deptoIndex = Integer.decode(dep_str.substring(0, dep_str.length()-1));	// xxDの"xx"部分を切り取る
					dependingMap.put(clause, deptoIndex);
					//if(deptoInt!=-1) deptoInt += nextID;	// *要注意(上に同じ)*
					String[] border_str = clauseInfo[3].split("/");
					border = Integer.decode(border_str[0]);
				}else {								// 他は単語の登録
					String[] wordInfo = line.split("\t");
					sbj_fnc = (wordList.size() <= border)? true: false;
					Word wd = new Word(wordInfo[0], Arrays.asList(wordInfo[1].split(",")));
					wd.comeUnder = clause;
					wd.isCategorem = sbj_fnc;
					wordList.add(wd);
				}
			}

			// clauseの係り受け関係を更新
			for (Map.Entry<Clause, Integer> entry : dependingMap.entrySet()) {
				Clause cls = entry.getKey();	int dep = entry.getValue();
				cls.depending = (dep >= 0)
						? clauseList.get(dep)
						: null;
				System.out.println(cls.toString() +" -> "+ cls.depending);
			}
			clauseList.stream().forEach(c -> System.out.println(c.depending));
			//Clause.updateAllDependency();

			// プロセス終了
			is.close();
			br.close();
			process.destroy();
			process.waitFor();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new Sentence(clauseList);
	}
}
