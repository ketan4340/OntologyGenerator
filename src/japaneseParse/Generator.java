package japaneseParse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.RDF.RDFTriple;
import grammar.Sentence;
import relationExtract.OntologyWriter;
import syntacticParse.Parser;

public class Generator {
	private List<Sentence> sentList;
	private List<RDFTriple> triples;

	public Generator() {
		sentList = new ArrayList<Sentence>();
		triples = new ArrayList<RDFTriple>();
	}

	public List<Sentence> getSentList() {
		return sentList;
	}
	public void setSentList(List<Sentence> sentList) {
		this.sentList = sentList;
	}
	public List<RDFTriple> getTriples() {
		return triples;
	}
	public void setRelations(List<RDFTriple> triples) {
		this.triples = triples;
	}

	public void run(String text) {
		List<String> writingList = new ArrayList<String>();
		if(text.isEmpty())
			writingList.add("これはデフォルトの文章です。");
		else
			writingList.addAll(Arrays.asList(text.split("\n")));
		run(writingList);
	}

	public void run(List<String> writingList) {
		//*/
		/*** Collecting Entries ***/
		/* 外部ファイルから日本語テキストを読み込む */
		/*
		String readFile = "gooText生物-動物名-All.txt";
		//String readFile = "writings/gooText生物-動物名-お.txt";
		File file = new File(readFile);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while(line != null) {
				writingList.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//*/
		/*スタンバーイ
		List<NaturalLanguage> nlList = writingList.stream().map(w -> new NaturalLanguage(w)).collect(Collectors.toList());
		Cabocha cabocha = new Cabocha();
		cabocha.executeParser(nlList);
		 */
		for(final String writing: writingList) {
			/*** 構文解析Module ***/
			System.out.println("\n\t Step0");
			Parser parse = new Parser(Parser.CABOCHA);
			Sentence originalSent = parse.parse(writing);
			
			System.out.println("---------marker 1--------");

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

			System.out.println("---------marker 2--------");

			/** Step2: 長文分割 **/
			/* 長文を分割し複数の短文に分ける */
			originalSent.printDep();
			for(final Sentence shortSent: originalSent.divide2()) {
				//shortSent.printDep();
				for(final Sentence partSent: shortSent.divide3()) {
					partSent.uniteSubject();
					//partSent.printDep();
					sentList.add(partSent);
				}
			}
		}

		System.out.println("------------関係抽出モジュール------------");

		/*** 関係抽出モジュール ***/
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		File fileText = new File("texts/text"+sdf.format(c.getTime())+".txt");	// ついでに分割後のテキストを保存
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileText));
			for(final Sentence partSent: sentList) {
				/* 単語間の関係を見つけ，グラフにする(各単語及び関係性をNodeのインスタンスとする) */
				bw.write(partSent.toString());		// 分割後の文を出力
				bw.newLine();
				triples.addAll(partSent.extractRelation());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//重複排除
		triples = new ArrayList<RDFTriple>(new LinkedHashSet<RDFTriple>(triples));

		File fileCSV = new File("csvs/relation"+sdf.format(c.getTime())+".csv");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileCSV));

			for(final RDFTriple triple: triples) {
				bw.write(triple.toString());
		        	bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*** OWL DL Axiom Module ***/
		OntologyWriter ob = new OntologyWriter(OntologyWriter.N_TRIPLES, triples);
		ob.output("owls/ontology"+sdf.format(c.getTime()));	// 渡すのは保存先のパス(拡張子は含まない)

		System.out.println("Finished.");
		System.out.println("Sentences: " + writingList.size() + "\t->dividedSentences: " + sentList.size());
		System.out.println("Relations: " + triples.size());
	}
}
