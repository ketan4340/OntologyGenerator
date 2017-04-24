package syntacticParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grammar.Clause;
import grammar.Sentence;
import grammar.Word;

public class Parser {
	public String tool; // どの解析器を使うか(stanford,cabocha,knp)
	public String analysed;
	public List<Integer> chunkList;

	public Parser(String howto) {
		tool = howto;
		analysed = new String();
		chunkList = new ArrayList<Integer>();
	}
	public Parser() {
		this(null);
	}

	public Sentence run(String text) {
		Sentence sent = null;
		switch (tool){
		case "cabocha":
			sent = runCaboCha(text);
			break;
		case "knp":
			System.out.println("KNPは未実装です。");
			break;
		default:
			System.out.println(tool+"には対応しておりません。");
			break;
		}
		return sent;
	}
	public Sentence runCaboCha(String text) {
		try {
			//UTF-8のBOMを除去するための準備←textファイルから読み込む場合を考慮
			byte [] bytes = {-17, -69, -65};
			String btmp= new String(bytes, "UTF-8");
			//BOM除去
			text=text.replaceAll(btmp, "");

			//cabochaの実行開始　lattice形式で出力(-f1の部分で決定、詳しくはcabochaのhelp参照)
			ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/cabocha", "-f1", "-n1");
			Process process = pb.start();

			//実行途中で文字列を入力(コマンドプロンプトで文字を入力する操作に相当)
			OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
			osw.write(text);
			osw.close();

			//出力結果を読み込む
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));

			//出力結果に格納するための文字列を用意
			String line = new String();
			Clause chk = null;
			List<Integer> wdl = null;
			int depto = -1;
			int border = 0;
			boolean sbj_fnc;
			int nextID = 0;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("EOS")) {		// EOSがきたら終了
					chk.setClause(wdl, depto);
					chunkList.add(chk.clauseID);

				}else if(line.startsWith("*")) {	// *で始まる場合直前までのChunkを閉じ、新しいChunkを用意
					if(wdl != null) {				// 最初は直前までのChunkが存在しないので回避
						chk.setClause(wdl, depto);
						chunkList.add(chk.clauseID);
					}else {
						nextID = Clause.clauseSum;	// *要注意というか汚い*
					}
					wdl = new ArrayList<Integer>();
					chk = new Clause();
					String[] chunkInfo = line.split(" ");
					String dep_str = chunkInfo[2];
					depto = Integer.decode(dep_str.substring(0, dep_str.length()-1));
					if(depto!=-1) depto += nextID;	// *要注意(上に同じ)*
					String[] border_str = chunkInfo[3].split("/");
					border = Integer.decode(border_str[0]);
				}else {								// 他は単語の登録
					String[] wordInfo = line.split("\t");
					sbj_fnc = (wdl.size() <= border)
							? true
							: false;
					Word wd = new Word();
					wd.setWord(wordInfo[0], Arrays.asList(wordInfo[1].split(",")), chk.clauseID, sbj_fnc);
					wdl.add(wd.wordID);
				}
				//読み込んだ行を格納
				analysed += line + "\n";
			}

			// chunkの係り受け関係を更新
			Clause.updateAllDependency();
			//System.out.println(analysed);

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

		return makeSentence();
	}

	public Sentence makeSentence() {
		Sentence sent = new Sentence();
		sent.setSentence(chunkList);
		return sent;
	}
}