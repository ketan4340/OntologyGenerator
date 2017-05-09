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

import grammar.Sentence;
import relationExtract.OntologyBuilder;
import syntacticParse.Parser;

public class GenerateProcess {
	private List<Sentence> sentList;
	private List<List<String>> relations;

	public GenerateProcess() {
		sentList = new ArrayList<Sentence>();
		relations = new ArrayList<List<String>>();
	}

	public List<Sentence> getSentList() {
		return sentList;
	}
	public void setSentList(List<Sentence> sentList) {
		this.sentList = sentList;
	}
	public List<List<String>> getRelations() {
		return relations;
	}
	public void setRelations(List<List<String>> relations) {
		this.relations = relations;
	}

	public void run(String text) {
		List<String> writingList = new ArrayList<String>();
		if(text.isEmpty()) {
			writingList.add("これはデフォルトの文章です。");
		}else {
			String[] writings = text.split("\n");
			writingList.addAll(Arrays.asList(writings));
		}
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

		for(final String writing: writingList) {
			/*** 構文解析Module ***/
			//System.out.println("\n\t Step0");
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
			originalSent.print();
			for(final Sentence shortSent: originalSent.divide2()) {
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
		File fileText = new File("texts/text"+sdf.format(c.getTime())+".txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileText));
			for(final Sentence partSent: sentList) {
				/* 単語間の関係を見つけ，グラフにする(各単語及び関係性をNodeのインスタンスとする) */
				bw.write(partSent.toString());		// 分割後の文を出力
				bw.newLine();
				List<List<String>> relation = partSent.extractRelation();
				relations.addAll(relation);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//重複排除
		relations = new ArrayList<List<String>>(new LinkedHashSet<List<String>>(relations));

		// 得られた関係を読み取り，uriとtriplesに入れる
		List<String> uri = new ArrayList<String>();	// ここに入れた単語はWordとしての情報を失いその後は文字列として扱う
		uri.add("rdf:type");			// 0
		uri.add("rdfs:subClassOf");		// 1
		uri.add("rdfs:subPropertyOf");	// 2
		uri.add("rdfs:domain");			// 3
		uri.add("rdfs:range");			// 4
		List<List<Integer>> triples = new ArrayList<List<Integer>>();

		File fileCSV = new File("csvs/relation"+sdf.format(c.getTime())+".csv");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileCSV));

			for(final List<String> relation: relations) {
	        	//System.out.println("relation = " + relation);
	        	List<Integer> triple_id = new ArrayList<Integer>(3);
	        	for(final String concept: relation) {
	        		bw.write(concept+",");
	        		if( !uri.contains(concept) ) uri.add(concept);
	        		triple_id.add(uri.indexOf(concept));
	        	}
	        	bw.newLine();
	        	triples.add(triple_id);
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//List<Node> nodes = Node.setTriples2Nodes(uri, triples);
		/*
		for(int i=0; i<uri.size(); i++) {
			System.out.println(i + ":\t" + uri.get(i));
			nodes.get(i).printNode1();
			nodes.get(i).printNode2();
		}
		*/

		/*** OWL DL Axiom Module ***/
		OntologyBuilder ob = new OntologyBuilder("n3", uri, triples);
		ob.output("owls/ontology"+sdf.format(c.getTime()));	// 渡すのは保存先のパス(拡張子は含まない)

		System.out.println("Finished.");
		System.out.println("Sentences: " + writingList.size() + "\t->dividedSentences: " + sentList.size());
		System.out.println("Relations: " + triples.size());
	}
}
