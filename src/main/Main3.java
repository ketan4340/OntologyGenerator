package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import grammar.Sentence;
import syntacticParse.Parser;

public class Main3 {

	public static void main(String[] args) {

		List<String> writingList = null;
		try {
			writingList = Files.readAllLines(Paths.get("./writings/gooText生物-動物名-さ.txt"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		List<Sentence> sentList = new ArrayList<>();

		for(final String writing: writingList) {
			/*** 構文解析Module ***/
			Parser parse = new Parser("cabocha");
			Sentence originalSent = parse.run(writing);

			/*** 文章整形Module ***/
			/** Step1: 単語結合 **/
			String[][] tagNouns = {{"接頭詞"}, {"名詞"}, {"接尾"}, {"形容詞"}};
			String[][] tagDo = {{"名詞"}, {"動詞", "する"}};
			String[][] tagDone = {{"動詞"}, {"動詞", "接尾"}};
			originalSent.connect(tagNouns);
			originalSent.connect(tagDo);
			originalSent.connect(tagDone);
			/* 名詞と形容詞だけ取り出す */
			// これらがClauseの末尾につくものを隣のClauseにつなげる
			String[][] tags_NP = {{"形容詞", "-連用テ接続"}, {"連体詞"}, {"助詞", "連体化"}, {"助動詞", "体言接続"}, {"名詞"}};
			originalSent.connect2Next(tags_NP, false);

			/** Step2: 長文分割 **/
			/* 長文を分割し複数の短文に分ける */
			originalSent.printDetail();
			for(final Sentence shortSent: originalSent.divide2()) {
				//shortSent.printDep();
				for(final Sentence partSent: shortSent.divide3()) {
					partSent.uniteSubject();
					//partSent.printDep();
					sentList.add(partSent);
				}
			}
		}

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		File fileText = new File("texts/text"+sdf.format(c.getTime())+".txt");	// ついでに分割後のテキストを保存
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileText));
			for(final Sentence partSent: sentList) {
				/* 単語間の関係を見つけ，グラフにする(各単語及び関係性をNodeのインスタンスとする) */
				bw.write(partSent.toString());		// 分割後の文を出力
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//

		/*
		List<String> records = new ArrayList<>();

		for (Sentence sent : sentList) {
			records.add(sent.toRecord());
		}

		Path dst = Paths.get("./dataset.csv");
		try {
			Files.write(dst, records, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		*/
	}
}
